package com.discord.bot.listeners;

import com.discord.bot.CommandManager;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class CommandListener extends ListenerAdapter {

    //Commands können in allen Arten von Chats geschrieben werden (Server, PM, etc.)
    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {

        String message = event.getMessage().getContentRaw();
        MessageChannel channel = event.getChannel();

        //Guckt, ob es sich um einen Command handelt
        if (message.startsWith("!")) {
            String[] args = message.split(" ");

            //Wenn es den Command nicht gibt
            if (!CommandManager.getInstance().execute(args[0], channel, event.getMessage())) {
                channel.sendMessage("Unknown Command.").queue();
            }
        }
    }
}
