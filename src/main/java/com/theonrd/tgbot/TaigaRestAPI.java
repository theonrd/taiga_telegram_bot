package com.theonrd.tgbot;

import com.nimbusds.jose.*;
import com.theonrd.tgbot.BotCommands.TasksStruct;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class TaigaRestAPI {

    private String api_token, refresh_value;
    private final String api_content = "application/json";

    public TaigaRestAPI() throws IOException, java.text.ParseException, JOSEException {

        var file = new File("./token");

        // Create new file and token if it isn't already created.
        if (file.createNewFile()) {
            var jsonAuth = "{" + "\"password\":\"" + ConfigHelper.password + "\"," + "\"type\":\"normal\"," + "\"username\":\"" + ConfigHelper.username + "\"}";

            // Get, Parse and save token to variable
            try {
                var jsonObject = (JSONObject) new JSONParser().parse(makePostRequest(jsonAuth, "/auth"));
                this.api_token = (String) jsonObject.get("auth_token");
                this.refresh_value = (String) jsonObject.get("refresh");
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else this.refresh_value = new String(Files.readAllBytes(file.toPath()));

        String refreshed = makePostRequest("{\"refresh\":\"" + this.refresh_value + "\"}", "/auth/refresh");

        try {
            var jsonObject = (JSONObject) new JSONParser().parse(refreshed);

            this.refresh_value = (String) jsonObject.get("refresh");
            this.api_token = (String) jsonObject.get("auth_token");

            // Write updated token
            if (this.refresh_value != null) {
                var writer = new FileWriter(file);
                writer.write(this.refresh_value);
                writer.close();
            } else {
                System.out.println("Invalid refresh operation!");
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getLastUpdatedUserStories() throws java.text.ParseException {

        var tasksList = new ArrayList<TasksStruct>();

        try {
            var initJson = (JSONArray) new JSONParser().parse(makeGetRequestToken("/userstories", true));

            for (Object o : initJson) {

                String jsonCurrentObject = o.toString();

                var currentJson = (JSONObject) new JSONParser().parse(jsonCurrentObject);
                var sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                var sModDate = ((String) currentJson.get("modified_date")).replace('T', ' ');

                var sTaskName = (String) currentJson.get("subject");
                var sCategory = (String) ((JSONObject) currentJson.get("status_extra_info")).get("name");
                var lMillis = sdf.parse(sModDate.substring(0, sModDate.indexOf('.'))).getTime();

                var currentTask = new TasksStruct(lMillis, sTaskName, sCategory);

                if (currentTask.isActualToQuery())
                    tasksList.add(currentTask);
            }

            System.out.println("Найдено " + tasksList.size() + " задач");

            if (tasksList.isEmpty())
                return null;

            var sbResult = new StringBuilder("Последние обновления в доске задач (Всего: ").append(tasksList.size()).append(")\n");

            var sbReady = new StringBuilder();
            var sbChanged = new StringBuilder();
            for (var task: tasksList){

                if (task.getCategoryName().equals(ConfigHelper.readyCategory))
                    sbReady.append(" > ").append(task.getTaskName()).append("\n");
                else
                    sbChanged.append(" > [").append(task.getCategoryName()).append("] ").append(task.getTaskName()).append("\n");
            }

            if (!sbReady.toString().equals(""))
                sbResult.append("\nЗадачи завершены:\n").append(sbReady);

            if (!sbChanged.toString().equals(""))
                sbResult.append("\nНовые или измененные задачи:\n").append(sbChanged);

            sbResult.append("\nПодробнее: ").append(ConfigHelper.site_address);

            return sbResult.toString();

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Example:
     * String s = makePostRequest("{JSON BODY}", "/some-api-address");
     *
     * @param body    JSON Body
     * @param address Address. API root will be automatically added by <code>ConfigHelper.api_address</>
     * @return Body of Message from server in UTF-8 String
     * @throws IOException If pizdec happens o:
     */
    public String makePostRequest(String body, String address) throws IOException {

        var client = HttpClients.createDefault();

        var httpPost = new HttpPost(ConfigHelper.api_address + address);
        httpPost.setHeader("Content-Type", this.api_content);
        httpPost.setEntity(new StringEntity(body));

        var result = EntityUtils.toString(client.execute(httpPost).getEntity(), "UTF-8");

        client.close();

        return result;
    }

    /**
     * Example
     * String s = makeGetRequestToken("/roles");
     *
     * @param address Address. API root will be automatically added by <code>ConfigHelper.api_address</>
     * @return Body of Message from server in UTF-8 String
     * @throws IOException If pizdec happens o:
     */
    public String makeGetRequestToken(String address, boolean disablePagination) throws IOException {

        var http = (HttpURLConnection) new URL(ConfigHelper.api_address + address).openConnection();

        http.setRequestProperty(HttpHeaders.CONTENT_TYPE, this.api_content);
        http.setRequestProperty(HttpHeaders.AUTHORIZATION, "Bearer " + this.api_token);

        if (disablePagination)
            http.setRequestProperty("x-disable-pagination", "True");

        http.disconnect();

        if (http.getResponseCode() == 200) {

            var bufferedReader = new BufferedReader(new InputStreamReader((http.getInputStream())));
            var stringBuilder = new StringBuilder();

            String output;
            while ((output = bufferedReader.readLine()) != null) {
                stringBuilder.append(output);
            }
            return stringBuilder.toString();
        }

        return "Error code: " + http.getResponseCode() + " & " + http.getResponseMessage();
    }
}
