package com.discord.bot.listeners;

import com.discord.bot.CommandManager;
import com.discord.bot.DatabaseManagement;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.sql.SQLException;


/**
 * Extend the <code>ListenerAdapter</code> to divide events from Discord.
 * Filter the bot messages to send them to <code>CommandManager
 * #execute(String cmd, MessageChannel channel, Message msg)</code> method.
 *
 * @author L2G4
 * @version %I%, %G%
 * @see com.discord.bot.BotMain#BotMain()
 * @see com.discord.bot.CommandManager
 * @see com.discord.bot.CommandManager#execute(String, MessageChannel, Message)
 * @see net.dv8tion.jda.api.hooks.ListenerAdapter
 * @since 1.0
 */
public class CommandListener extends ListenerAdapter {

    private static final Logger LOGGER = LogManager.getLogger(CommandListener.class.getName());

    /**
     * Detect that a Message is received in either a guild or private Discord channel.
     * The <code>#onMessageReceived(@Nonnull MessageReceivedEvent event)</code>
     * method gets them and separate the BotCommand(starts with '!').
     *
     * @param event Message channel. Includes public and private channel.
     */
    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {

        MessageChannel channel = event.getChannel();
        String message = event.getMessage().getContentRaw();

        // Check if the input is a command.
        new Thread(() -> {
            if (message.startsWith("!")) {
                String[] args = message.split(" ");

                // Skips everything else if message comes from the bot itself
                if (event.getAuthor().getId().equals(channel.getJDA().getSelfUser().getId())) {
                    return;
                }

                /*
                 * Call execute method in CommandManager.
                 * returnedValue = 0 --> execute was successful
                 * returnedValue = 1 --> command doesn't exist at all
                 * returnedValue = 2 --> command was written in wrong chat type
                 */
                int commandExists = CommandManager.getInstance().execute(args[0], channel, event.getMessage());

                if (commandExists == 1) {
                    try {
                        if (DatabaseManagement.getINSTANCE().registeredCheck(event.getAuthor().getId())) {
                            channel.sendMessage("Unknown Command. Use `!help` to see an overview of all available commands.").queue();
                            event.getMessage().addReaction("U+2753").queue();
                        }
                    } catch (SQLException e) {
                        LOGGER.fatal(String.format("SQLException.%n%s", e));
                    }
                } else if (commandExists == 2) {
                    channel.sendMessage("This command is for private chat only.").queue();
                    event.getMessage().addReaction("U+1F645").queue();
                }
            }
        }).start();
    }
}
