package com.theonrd.tgbot.BotCommands;

import com.theonrd.tgbot.ConfigHelper;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

public abstract class ABotCommand {

    private final SendMessage answer;
    private final Message updMessage;

    public ABotCommand(Message updateMessageRef, boolean isRequireAdminRights) {

        this.answer = new SendMessage();
        this.updMessage = updateMessageRef;

        if (isRequireAdminRights && !isAdmin()) {
            this.answer.setReplyToMessageId(this.updMessage.getMessageId());
            this.answer.setChatId(this.updMessage.getChatId().toString());
            this.answer.setText("Ты не мой ♂Dungeon Master♂, чтобы такое делать, " + this.updMessage.getFrom().getUserName() + "!");
        } else
            messageDerive(this.updMessage, this.answer);
    }

    public boolean isAdmin() {

        for (var s: ConfigHelper.tg_admins)
            if (this.updMessage.getFrom().getUserName().equals(s))
                return true;

        return false;
    }

    public SendMessage getExecutor() {

        return this.answer;
    }

    public abstract void messageDerive(Message updMessage_, SendMessage ans);
}
