package com.github.kirekq.pricebot.command;

public interface BotCommand {
    String getCommandName();
    void execute(long chat_id, String[] fullMessage);
}

