package com.github.kirekq.pricebot;
import com.github.kirekq.pricebot.command.BotCommand;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.AfterBotRegistration;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class BotInitializer implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {
    private final String botToken;
    private final Map<String, BotCommand> commands;

    public BotInitializer(@Value("${telegram.bot.token}") String botToken, List<BotCommand> botCommands) {
        this.botToken = botToken;
        this.commands = botCommands.stream()
                .collect(Collectors.toMap(BotCommand::getCommandName, cmd -> cmd));
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message_text = update.getMessage().getText().trim();
            String[] fullMessage = message_text.split("\\s+", 2);
            String commandName = fullMessage[0];
            long chat_id = update.getMessage().getChatId();

            BotCommand command = commands.get(commandName);

            if (command != null) {
                command.execute(chat_id, fullMessage);
            } else {
                System.out.println("Неизвестная команда или текст: " + message_text);
            }
        }
    }

    @AfterBotRegistration
    public void afterRegistration(BotSession botSession) {
        System.out.println("Registered bot running state is: " + botSession.isRunning());
    }
}
