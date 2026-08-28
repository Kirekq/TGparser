package com.github.kirekq.pricebot.command;

import com.github.kirekq.pricebot.data.ProductWildberries;
import com.github.kirekq.pricebot.data.ProductRepositoryWildberries;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import com.github.kirekq.pricebot.parse.ParseWildberries;

import java.io.IOException;
import java.util.Map;
@Component
public class CheckWildberries implements BotCommand{
    private final String commandName = "/checkwildberries";
    private final TelegramClient telegramClient;
    private final ParseWildberries parser;
    private final ProductRepositoryWildberries productRepositoryWildberries;
    CheckWildberries(TelegramClient telegramClient, ParseWildberries parser, ProductRepositoryWildberries productRepositoryWildberries){
        this.telegramClient = telegramClient;
        this.parser = parser;
        this.productRepositoryWildberries = productRepositoryWildberries;
    }
    private void addToData(long chat_id, String article, String name, String price, String priceNew){
        ProductWildberries product = new ProductWildberries();
        product.setChatId(chat_id);
        product.setPrice(Double.parseDouble(price));
        product.setArticle(article);
        product.setName(name);
        product.setPriceNew(Double.parseDouble(priceNew));
        productRepositoryWildberries.save(product);
    }

    private void sendMessage(long chat_id, String messageText) throws TelegramApiException{
        SendMessage message = SendMessage
                .builder()
                .chatId(chat_id)
                .text(messageText)
                .build();
        telegramClient.execute(message);
    }
    @Override
    public String getCommandName(){
        return commandName;
    }

    @Override
    public void execute(long chat_id, String[] fullMessage){
        if (fullMessage.length < 2){
            try {
                sendMessage(chat_id, "Неправильный артикул!");
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            return;
        }
        String Article = fullMessage[1];
        try {
            Map<String, String> fullData = parser.parse(Article);
            String messageText;
            if (fullData.isEmpty()){
                messageText = "Неправильный артикул!";
            } else {
                String name = fullData.get("Name");
                String price = fullData.get("Price");
                addToData(chat_id, Article, name, price, price);
                messageText = name + " добавлен!\nЦена = " + price + "₽";
            }
            sendMessage(chat_id, messageText);
        } catch (TelegramApiException | IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
