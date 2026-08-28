package com.github.kirekq.pricebot.command;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class HelpCommand implements BotCommand {
    private final String helpMessage = "Команды:\n" +
            "/checkwildberries - начать отслеживать цену на артикул Wildberries\n" +
            "/checkniceprice - начать отслеживать цену на артикул NicePrice62\n" +
            "/delete - перестать отслеживать артикул Wildberries или NicePrice62";
    private final String commandName = "/help";
    private final TelegramClient telegramClient;
    HelpCommand(TelegramClient telegramClient){
        this.telegramClient = telegramClient;
    }

    public String getHelpMessage() {
        return helpMessage;
    }

    @Override
    public String getCommandName(){
        return commandName;
    }
    @Override
    public void execute(long chat_id, String[] fullMessage){
        SendMessage message = SendMessage
                .builder()
                .chatId(chat_id)
                .text(getHelpMessage())
                .build();
        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
