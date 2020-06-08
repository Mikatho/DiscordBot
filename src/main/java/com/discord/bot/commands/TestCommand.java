package com.discord.bot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.awt.*;

public class TestCommand implements CommandInterface {
    @Override
    public void executeCommand(MessageChannel channel, Message msg) {

        channel.sendMessage("test").queue();
    }
}
