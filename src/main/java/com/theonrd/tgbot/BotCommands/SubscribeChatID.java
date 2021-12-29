package com.theonrd.tgbot.BotCommands;

import com.theonrd.tgbot.ConfigHelper;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.IOException;
import java.util.ArrayList;

public class SubscribeChatID extends ABotCommand {

    public SubscribeChatID(Message updateMessageRef, boolean isRequireAdminRights) {

        super(updateMessageRef, isRequireAdminRights);
    }

    @Override
    public void messageDerive(Message message, SendMessage ans) {

        ans.setReplyToMessageId(message.getMessageId());
        ans.setChatId(message.getChatId().toString());

        try {

            if (message.getText().equals("/subscribe cancel")) {

                var toRemove = message.getChatId();
                var alertsArray = new ArrayList<Long>();

                for (var currentAlert : ConfigHelper.alerts_chat_ids)
                    if (currentAlert != toRemove) alertsArray.add(currentAlert);

                var result = new long[alertsArray.size()];

                for (var i = 0; i < alertsArray.size(); i++)
                    result[i] = alertsArray.get(i);

                ConfigHelper.alerts_chat_ids = result;
                ConfigHelper.ConfigSave();

                ans.setText("♂oh shit, im sorry♂, больше не будет рассылки.");

            } else {

                for (var i : ConfigHelper.alerts_chat_ids)
                    if (i == message.getChatId()) {
                        ans.setText("Этот чат уже добавлен в рассылку! \nЧтобы отписаться, введи /subscribe cancel");
                        return;
                    }

                var tempArray = new long[ConfigHelper.alerts_chat_ids.length + 1];

                System.arraycopy(ConfigHelper.alerts_chat_ids, 0, tempArray, 0, ConfigHelper.alerts_chat_ids.length);
                tempArray[tempArray.length - 1] = message.getChatId();

                ConfigHelper.alerts_chat_ids = tempArray;
                ConfigHelper.ConfigSave();

                ans.setText("♂Yes sir!♂");
            }

        } catch (IOException e) {
            e.printStackTrace();
            ans.setText("♂Oh fuck you!♂\n" + e.getMessage());
        }
    }
}
