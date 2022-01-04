package com.theonrd.tgbot;

import com.theonrd.tgbot.BotCommands.DebugApiGetRequest;
import com.theonrd.tgbot.BotCommands.FuckYouCommand;
import com.theonrd.tgbot.BotCommands.SubscribeChatID;
import com.theonrd.tgbot.BotCommands.UpdateManager;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.text.ParseException;

public class BotMain extends TelegramLongPollingBot {

    final int RECONNECT_PAUSE = 10000;

    private final String botToken, botUserName;
    public static TaigaRestAPI taigaRestAPI_Instance;

    public BotMain(String token, String userName) {

        this.botToken = token;
        this.botUserName = userName;

        taigaRestAPI_Instance = new TaigaRestAPI();
    }

    @Override
    public String getBotUsername() {
        return this.botUserName;
    }

    @Override
    public String getBotToken() {
        return this.botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {

        var message = update.getMessage();
        var received = message.getText();

        try {
            if (received.equals("Бот иди нахуй") || received.contains("бота нахуй"))
                execute(new FuckYouCommand(message, false).getExecutor());

            else if (received.startsWith("/taiga_api_debug "))
                execute(new DebugApiGetRequest(message, true).getExecutor());

            else if (received.startsWith("/subscribe"))
                execute(new SubscribeChatID(message, true).getExecutor());

            else if (received.equals("/alert"))
                new UpdateManager(message, true);

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void taigaAlert() {

        try {
            var sendMessage = new SendMessage();

            var userStories = BotMain.taigaRestAPI_Instance.getLastUpdatedUserStories();

            if(userStories == null)
                return;

            sendMessage.setText(userStories);

            // Send to all subscribed chats
            for (var id : ConfigHelper.alerts_chat_ids) {
                sendMessage.setChatId(Long.toString(id));

                execute(sendMessage);
                Thread.sleep(500); // Little anti-spam delay
            }
        } catch (TelegramApiException | InterruptedException | ParseException e) {
            e.printStackTrace();
        }
    }

    public void openConnection() {

        try {
            new TelegramBotsApi(DefaultBotSession.class).registerBot(this);

        } catch (TelegramApiException e) {

            try {
                Thread.sleep(RECONNECT_PAUSE);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
                return;
            }
            // Try to reconnect after every RECONNECT_PAUSE
            System.out.println("[" + java.time.LocalDateTime.now() + "] " + "Connection is invalid, trying again...");
            openConnection();
        }
        System.out.println("[" + java.time.LocalDateTime.now() + "] " + "Bot is connected!");
    }
}
