package com.theonrd.tgbot.BotCommands;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

public class FuckYouCommand extends ABotCommand {

    public FuckYouCommand(Message updateMessageRef, boolean isRequireAdminRights) {

        super(updateMessageRef, isRequireAdminRights);
    }

    @Override
    public void messageDerive(Message message, SendMessage ans) {

        ans.setReplyToMessageId(message.getMessageId());
        ans.setChatId(message.getChatId().toString());
        ans.setText("Сам иди нахуй, " + message.getFrom().getUserName() + " :(");
    }
}
