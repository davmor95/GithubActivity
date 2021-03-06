package ecs189.querying.github;

import ecs189.querying.Util;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Vincent on 10/1/2017.
 */
public class GithubQuerier {

    private static final String BASE_URL = "https://api.github.com/users/";
    private static final String push = "PushEvent";

    public static String eventsAsHTML(String user) throws IOException, ParseException {
        List<JSONObject> response = getEvents(user);
        StringBuilder sb = new StringBuilder();
        sb.append("<div>");
        for (int i = 0; i < response.size(); i++) {
            JSONObject event = response.get(i);
            // Get event type
            String type = event.getString("type");
            // Get created_at date, and format it in a more pleasant style
            String creationDate = event.getString("created_at");
            SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
            SimpleDateFormat outFormat = new SimpleDateFormat("dd MMM, yyyy");
            Date date = inFormat.parse(creationDate);
            String formatted = outFormat.format(date);

            // Add type of event as header
            sb.append("<h3 class=\"type\">");
            sb.append(i+1);
            sb.append(") Push Event on ");
            sb.append(formatted);
            sb.append("</h3>");
            // Add formatted date

            sb.append("<br />");
            // Add collapsible JSON textbox (don't worry about this for the homework; it's just a nice CSS thing I like)
            sb.append("<a data-toggle=\"collapse\" href=\"#event-" + i + "\" style =\"margin-left:25px\">JSON & Commits</a>");
            sb.append("<div id=event-" + i + " class=\"collapse\" style=\"height: auto;margin-left:25px\">");
            sb.append("<h4> JSON: </h4> <pre style = margin-left:15px>");
            sb.append(event.toString());
            sb.append("</pre> <h4> Commits: </h4> <ol style = margin-left:15px>");
            // Add Commit SHA/Comments
            JSONObject payload = event.getJSONObject("payload");
            JSONArray commits = payload.getJSONArray("commits");
            for(int j = 0; j < commits.length(); j++){
                sb.append("<li> SHA: <pre>");
                JSONObject temp = commits.getJSONObject(j);
                sb.append(temp.getString("sha"));
                sb.append("</pre> Comment: <pre>");
                sb.append(temp.getString("message"));
                sb.append("</pre>");
            }

            sb.append("</div> </li>");
        }
        sb.append("</ol> </div>");
        return sb.toString();
    }

    private static List<JSONObject> getEvents(String user) throws IOException {
        List<JSONObject> eventList = new ArrayList<JSONObject>();
        String url = BASE_URL + user + "/events";
        System.out.println(url);
        JSONObject json = Util.queryAPI(new URL(url));
        System.out.println(json);
        JSONArray events = json.getJSONArray("root");
        int x = 0, i = 0;
        while(i < events.length() && x < 10) {
            JSONObject temp = events.getJSONObject(i);
            if(temp.getString("type").equals(push)) {
                eventList.add(temp);
                x++;
            }
            i++;
        }
        return eventList;
    }
}