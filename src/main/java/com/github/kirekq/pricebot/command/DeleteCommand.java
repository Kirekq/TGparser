package com.github.kirekq.pricebot.command;

import com.github.kirekq.pricebot.data.ProductRepository;
import com.github.kirekq.pricebot.data.ProductRepositoryWildberries;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class DeleteCommand implements BotCommand{
    private final String commandName = "/delete";
    private final TelegramClient telegramClient;
    private final ProductRepository productRepository;
    private final ProductRepositoryWildberries productRepositoryWildberries;
    DeleteCommand(TelegramClient telegramClient, ProductRepository productRepository, ProductRepositoryWildberries productRepositoryWildberries){
        this.telegramClient = telegramClient;
        this.productRepository = productRepository;
        this.productRepositoryWildberries = productRepositoryWildberries;
    }

    private void sendMessage(long chat_id, String messageText) throws TelegramApiException {
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
    @Transactional
    public void execute(long chat_id, String[] fullMessage){
        if (fullMessage.length < 2){
            try {
                sendMessage(chat_id, "Данный артикул не добавлен!");
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            return;
        }
        String Article = fullMessage[1];
        try {
            long deletedNicePrice = productRepository.deleteByChatIdAndArticle(chat_id, Article);

            long deletedWB = productRepositoryWildberries.deleteByChatIdAndArticle(chat_id, Article);

            String messageText;
            if (deletedNicePrice > 0 || deletedWB > 0) {
                messageText = "Товар с артикулом " + Article + " успешно удален из отслеживания";
            } else {
                messageText = "Товар с артикулом " + Article + " не найден в вашем списке отслеживания";
            }

            sendMessage(chat_id, messageText);

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
