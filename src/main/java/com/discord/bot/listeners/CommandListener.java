package com.discord.bot.listeners;

import com.discord.bot.CommandManager;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;


/**
 * Extend the <code>ListenerAdapter</code> to divide events from Discord.
 * Filter the bot messages to send them to <code>CommandManager
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

    /**
     * Detect that a Message is received in either a guild or private Discord channel.
     * The <code>#onMessageReceived(@Nonnull MessageReceivedEvent event)</code>
     * method gets them and separate the BotCommand(starts with '!').
     *
     * @param   event   Message channel. Includes public and private channel.
     */
    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {

        String message = event.getMessage().getContentRaw();
        MessageChannel channel = event.getChannel();

        /**
         * Check if the input is a command.
         */
        if (message.startsWith("!")) {
            String[] args = message.split(" ");

            /**
             * Skips everything else if message comes from the bot itself
             */
            if (event.getAuthor().getId().equals(channel.getJDA().getSelfUser().getId())) {
                return;
            }

            /**
             * Call execute method in CommandManager.
             */

            if (!CommandManager.getInstance().execute(args[0], channel, event.getMessage())) {
                channel.sendMessage("Unknown Command. Use `!help` to see an overview of all available commands.").queue();
            }
        }
    }
}
