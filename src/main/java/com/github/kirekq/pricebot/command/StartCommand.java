package com.github.kirekq.pricebot.command;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class StartCommand implements BotCommand {
    private final String startMessage = "Приветствую! Чтобы узнать что данный бот умеет напиши /help";
    private final String commandName = "/start";
    private final TelegramClient telegramClient;
    StartCommand(TelegramClient telegramClient){
        this.telegramClient = telegramClient;
    }

    public String getStartMessage() {
        return startMessage;
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
                .text(getStartMessage())
                .build();
        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
