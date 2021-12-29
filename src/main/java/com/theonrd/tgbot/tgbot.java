package com.theonrd.tgbot;

import com.theonrd.tgbot.BotCommands.UpdateManager;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public class tgbot {

    public static BotMain botInstance;

    public static void main(String[] args) {

        try {
            ConfigHelper.ConfigLoad();
            System.out.println("[OK] Config is loaded");
        } catch (IOException | ParseException e) {
            System.out.println("[FATAL] Config Error");
            return;
        }

        botInstance = new BotMain(ConfigHelper.tgbot_token, ConfigHelper.tgbot_id);
        botInstance.openConnection();

        UpdateManager.MainHandlerBus();
    }
}
