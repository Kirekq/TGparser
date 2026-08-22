package com.github.kirekq.pricebot.command;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import com.github.kirekq.pricebot.parse.parseNicePrice;

import java.io.IOException;
import java.util.HashMap;

@Component
public class CheckNicePrice implements BotCommand {
    private final String commandName = "/checkniceprice";
    private final TelegramClient telegramClient;
    private final parseNicePrice parser;
    CheckNicePrice(TelegramClient telegramClient, parseNicePrice parser){
        this.telegramClient = telegramClient;
        this.parser = parser;
    }
    @Override
    public String getCommandName(){
        return commandName;
    }
    @Override
    public void execute(long chat_id, String[] fullMessage){
        String Article = fullMessage[1];
        try {
            HashMap<String, String> fullData = parser.parse(Article);
            String messageText;
            if (fullData.isEmpty()){
                messageText = "Неправильный артикул!";
            } else {
                messageText = fullData.get("Name") + " добавлен!\nЦена = " + fullData.get("Price") + "₽";
            }
            SendMessage message = SendMessage
                    .builder()
                    .chatId(chat_id)
                    .text(messageText)
                    .build();
            telegramClient.execute(message);
        } catch (TelegramApiException | IOException e) {
            e.printStackTrace();
        }
    }
}
