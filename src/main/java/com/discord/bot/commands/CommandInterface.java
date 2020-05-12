package com.discord.bot.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public interface CommandInterface {

    void executeCommand(MessageChannel channel, Message msg);
}
