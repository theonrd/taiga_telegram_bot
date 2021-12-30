package com.theonrd.tgbot;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;

public class ConfigHelper {

    public static String
            username = "taiga_user",
            password = "taiga_password",
            api_address = "http://example:xxxx/api/v1",
            site_address = "http://example.com",
            tgbot_id = "bot_id",
            tgbot_token = "bot_token",
            readyCategory = "ready";
    public static String[] tg_admins = new String[]{"null"};
    public static long
            update_timer_time = 3600000,
            timezone_different = 3600000 * 3; // +3 GMT
    public static long[] alerts_chat_ids = new long[]{};

    private static final String configFilePath = "./config.json";

    public static void ConfigLoad() throws IOException, ParseException {

        // If config doesn't exist,
        // create a new one by current values (usually defaults).
        if (new File(configFilePath).createNewFile()) {

            ConfigSave();
            System.exit(0);
        }

        var jsonObject = (JSONObject) new JSONParser().parse(new FileReader(configFilePath));

        username = (String) jsonObject.get("username");
        password = (String) jsonObject.get("password");
        api_address = (String) jsonObject.get("api_address");
        tgbot_id = (String) jsonObject.get("tgbot_id");
        tgbot_token = (String) jsonObject.get("tgbot_token");
        readyCategory = (String) jsonObject.get("ready_category");
        site_address = (String) jsonObject.get("site_address");
        timezone_different = (Long) jsonObject.get("timezone_diff");

        tg_admins = ((String) jsonObject.get("admins")).contains(",") ?
                ((String) jsonObject.get("admins")).split(",") : new String[]{((String) jsonObject.get("admins"))};

        update_timer_time = (Long) jsonObject.get("update_timer");

        if (jsonObject.containsKey("chat_ids")) {

            String[] parsedIDs = ((String) jsonObject.get("chat_ids")).contains(",") ?
                    ((String) jsonObject.get("chat_ids")).split(",") : new String[]{((String) jsonObject.get("chat_ids"))};

            if (!parsedIDs[0].equals("") && !parsedIDs[0].equals("null")) {
                alerts_chat_ids = new long[parsedIDs.length];
                for (int i = 0; i < parsedIDs.length; i++) {
                    alerts_chat_ids[i] = Long.parseLong(parsedIDs[i]);
                }
            }
        }
    }

    @SuppressWarnings("unchecked") // json.simple uses unchecked calls, so ignore this
    public static void ConfigSave() throws IOException {

        var jsonObject = new JSONObject();

        // Default variables
        jsonObject.put("username", username);
        jsonObject.put("password", password);
        jsonObject.put("api_address", api_address);
        jsonObject.put("tgbot_token", tgbot_token);
        jsonObject.put("tgbot_id", tgbot_id);
        jsonObject.put("update_timer", update_timer_time);
        jsonObject.put("ready_category", readyCategory);
        jsonObject.put("site_address", site_address);
        jsonObject.put("timezone_diff", timezone_different);

        // Admins array
        var array_converted = new StringBuilder();
        for (var i : tg_admins)
            array_converted.append(i).append(",");
        jsonObject.put("admins", array_converted.substring(0, array_converted.length() - 1));

        // Subscribed chats. Relevant only if we have some subscribes
        if (alerts_chat_ids.length > 0) {
            array_converted = new StringBuilder();
            for (var i : alerts_chat_ids)
                array_converted.append(i).append(",");
            jsonObject.put("chat_ids", array_converted.substring(0, array_converted.length() - 1));
        }

        // Write result
        var writer = new FileWriter(configFilePath);
        writer.write(jsonObject.toJSONString());
        writer.close();
    }
}
