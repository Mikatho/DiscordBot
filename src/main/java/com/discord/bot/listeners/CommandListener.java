package com.discord.bot.listeners;

import com.discord.bot.CommandManager;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;


/**
 * Extend the <code>ListenerAdapter</code> to divide events from Discord,
 * filter the bot messages to send them to <code>CommandManager
 * #execute(String cmd, MessageChannel channel, Message msg)</code> method.
 *
 * @author      L2G4
 * @version     %I%, %G%
 * @see         com.discord.bot.BotMain#BotMain()
 * @see         com.discord.bot.CommandManager
 * @see         com.discord.bot.CommandManager#execute(String, MessageChannel, Message) 
 * @see         net.dv8tion.jda.api.hooks.ListenerAdapter
 * @since       1.0
 */
public class CommandListener extends ListenerAdapter {

    //Commands können in allen Arten von Chats geschrieben werden (Server, PM, etc.)
    /**
     * Detect that a Message is received in either a guild- or private Discord channel.
     * The <code>#onMessageReceived(@Nonnull MessageReceivedEvent event)</code>
     * method get them and separates the BotCommand(starts with '!').
     *
     * @param   event   Message channel. Includes public and private channel.
     */
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
