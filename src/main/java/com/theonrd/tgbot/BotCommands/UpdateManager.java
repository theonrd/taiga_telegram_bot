package com.theonrd.tgbot.BotCommands;

import com.theonrd.tgbot.ConfigHelper;
import com.theonrd.tgbot.tgbot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

public class UpdateManager extends ABotCommand {

    public static void MainHandlerBus() {

        while (true) {
            try {
                Thread.sleep(ConfigHelper.update_timer_time);
                tgbot.botInstance.taigaAlert();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    public UpdateManager(Message updateMessageRef, boolean isRequireAdminRights) {

        super(updateMessageRef, isRequireAdminRights);
    }

    @Override
    public void messageDerive(Message message, SendMessage ans) {

        tgbot.botInstance.taigaAlert();
    }
}
