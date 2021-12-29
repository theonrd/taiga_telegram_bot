package com.theonrd.tgbot.BotCommands;

import com.theonrd.tgbot.BotMain;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.IOException;
import java.util.Arrays;

public class DebugApiGetRequest extends ABotCommand {

    public DebugApiGetRequest(Message updateMessageRef, boolean isRequireAdminRights) {
        super(updateMessageRef, isRequireAdminRights);
    }

    @Override
    public void messageDerive(Message message, SendMessage ans) {

        ans.setReplyToMessageId(message.getMessageId());
        ans.setChatId(message.getChatId().toString());

        try {
            var answer_message = BotMain.taigaRestAPI_Instance.makeGetRequestToken(message.getText().substring("/taiga_api_debug ".length()), true);
            if (answer_message.length() > 2048)
                answer_message = answer_message.substring(0, 2048) + "\n\n[...and " + (answer_message.length() - 2048) + " more symbols]";

            ans.setText(answer_message);
        } catch (IOException e) {
            ans.setText(Arrays.toString(e.getStackTrace()));
        }
    }
}
