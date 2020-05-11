package com.discord.bot.listeners;

import com.discord.bot.CommandManager;
<<<<<<< HEAD
import net.dv8tion.jda.api.entities.*;
=======
import net.dv8tion.jda.api.entities.MessageChannel;
>>>>>>> fa80302b304130282fe7f33c3855d11f3576c846
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class CommandListener extends ListenerAdapter {

<<<<<<< HEAD
    //Commands können in allen Arten von Chats geschrieben werden (Server, PM, etc.)
=======
>>>>>>> fa80302b304130282fe7f33c3855d11f3576c846
    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {

        String message = event.getMessage().getContentRaw();
        MessageChannel channel = event.getChannel();

<<<<<<< HEAD
        //Guckt, ob es sich um einen Command handelt
        if (message.startsWith("!")) {
            String[] args = message.split(" ");

            //Wenn es den Command nicht gibt
            if (!CommandManager.getInstance().execute(args[0], channel, event.getMessage())) {
=======
        if(message.startsWith("!")) {
            String[] args = message.split(" ");

            if(!CommandManager.getInstance().execute(args[0], event.getMember(), channel, event.getMessage())) {
>>>>>>> fa80302b304130282fe7f33c3855d11f3576c846
                channel.sendMessage("Unknown Command.").queue();
            }
        }
    }
}
