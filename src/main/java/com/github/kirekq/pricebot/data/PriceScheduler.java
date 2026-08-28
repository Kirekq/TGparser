package com.github.kirekq.pricebot.data;

import com.github.kirekq.pricebot.parse.ParseWildberries;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.github.kirekq.pricebot.parse.ParseNicePrice;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;


import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class PriceScheduler {
    private final ProductRepository productRepository;
    private final ProductRepositoryWildberries productRepositoryWildberries;
    private final ParseWildberries parserWildberries;
    private final ParseNicePrice parserNicePrice;
    private final TelegramClient telegramClient;
    public PriceScheduler(ProductRepository productRepository, ProductRepositoryWildberries productRepositoryWildberries, ParseNicePrice parserNicePrice, TelegramClient telegramClient, ParseWildberries parserWildberries) {
        this.productRepository = productRepository;
        this.productRepositoryWildberries = productRepositoryWildberries;
        this.parserNicePrice = parserNicePrice;
        this.telegramClient = telegramClient;
        this.parserWildberries = parserWildberries;
    }

    @Scheduled(cron = "0 0 12 * * *")
    public void checkPrices() {
        checkPricesNicePrice();
        checkPricesWildberries();
    }

    public void checkPricesNicePrice() {
        List<Product> allProducts = productRepository.findAll();
        for (Product product : allProducts) {
            try {
                Map<String, String> currentData = parserNicePrice.parse(product.getArticle());
                if (currentData != null && currentData.containsKey("Price")) {
                    double actualPrice = Double.parseDouble(currentData.get("Price"));
                    if (actualPrice != product.getPriceNew()) {
                        double difference = actualPrice - product.getPriceNew();
                        System.out.println("Цена по артикулу NicePrice " + product.getArticle() + " изменилась на " + difference);
                        SendMessage message = SendMessage
                                .builder()
                                .chatId(product.getChatId())
                                .text("Цена по артикулу NicePrice " + product.getArticle() + " изменилась на " + difference + "₽ с " + product.getPriceNew() +
                                        "₽ до " + actualPrice + "₽ \nСтартовая цена: " + product.getPrice())
                                .build();
                        telegramClient.execute(message);
                        product.setPriceNew(actualPrice);
                        productRepository.save(product);
                    }
                } else {
                    System.out.println("Произошла ошибка по артикулу NicePrice " + product.getArticle());
                }
            } catch (IOException | TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    public void checkPricesWildberries() {
        List<ProductWildberries> allProducts = productRepositoryWildberries.findAll();
        for (ProductWildberries product : allProducts) {
            try {
                Map<String, String> currentData = parserWildberries.parse(product.getArticle());
                if (currentData != null && currentData.containsKey("Price")) {
                    double actualPrice = Double.parseDouble(currentData.get("Price"));
                    if (actualPrice != product.getPriceNew()) {
                        double difference = actualPrice - product.getPriceNew();
                        System.out.println("Цена по артикулу Wildberries " + product.getArticle() + " изменилась на " + difference);
                        SendMessage message = SendMessage
                                .builder()
                                .chatId(product.getChatId())
                                .text("Цена по артикулу Wildberries " + product.getArticle() + " изменилась на " + difference + "₽ с " + product.getPriceNew() +
                                        "₽ до " + actualPrice + "₽ \nСтартовая цена: " + product.getPrice())
                                .build();
                        telegramClient.execute(message);
                        product.setPriceNew(actualPrice);
                        productRepositoryWildberries.save(product);
                    }
                } else {
                    System.out.println("Произошла ошибка по артикулу Wildberries " + product.getArticle());
                }
            } catch (IOException | TelegramApiException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
