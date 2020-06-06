package com.discord.bot.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class TestCommand implements CommandInterface {
    @Override
    public void executeCommand(MessageChannel channel, Message msg) {

        channel.sendMessage("!help").queue();
    }
}
