package com.github.kirekq.pricebot.command;

import com.github.kirekq.pricebot.data.Product;
import com.github.kirekq.pricebot.data.ProductRepository;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import com.github.kirekq.pricebot.parse.ParseNicePrice;

import java.io.IOException;
import java.util.Map;

@Component
public class CheckNicePrice implements BotCommand {
    private final String commandName = "/checkniceprice";
    private final TelegramClient telegramClient;
    private final ParseNicePrice parser;
    private final ProductRepository productRepository;
    CheckNicePrice(TelegramClient telegramClient, ParseNicePrice parser, ProductRepository productRepository){
        this.telegramClient = telegramClient;
        this.parser = parser;
        this.productRepository = productRepository;
    }
    private void addToData(long chat_id, String article, String name, String price, String priceNew){
        Product product = new Product();
        product.setChatId(chat_id);
        product.setPrice(Double.parseDouble(price));
        product.setArticle(article);
        product.setName(name);
        product.setPriceNew(Double.parseDouble(priceNew));
        productRepository.save(product);
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
            } else if (productRepository.existsByChatIdAndArticle(chat_id, Article)) {
                messageText = "Артикул " + Article + " уже отслеживается";
            } else {
                String name = fullData.get("Name");
                String price = fullData.get("Price");
                addToData(chat_id, Article, name, price, price);
                messageText = name + " добавлен!\nЦена = " + price + "₽";
            }
            sendMessage(chat_id, messageText);
        } catch (TelegramApiException | IOException e) {
            e.printStackTrace();
        }
    }
}
