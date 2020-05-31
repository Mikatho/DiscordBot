package com.discord.bot.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class HelpCommand implements CommandInterface {

    @Override
    public void executeCommand(MessageChannel channel, Message msg) {

        channel.sendMessage("Whatever. ^_^").queue();

        //m.getUser().openPrivateChannel().complete().sendMessage("Hallo v2.").queue();
    }
}
