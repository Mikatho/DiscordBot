package com.discord.bot.communication;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;


/**
 * Every class in the Command-Pattern has to implement the <code>CommandInterface</code>. It overrides the
 * <code>#executeCommand(MessageChannel channel, Message msg)</code> method which gets the Discord inputs from
 * the <code>CommandManager</code> class.
 *
 * @author L2G4
 * @version %I%, %G%
 * @see CommandManager
 * @see CommandManager#execute(String, MessageChannel, Message)
 * @see CommandListener
 * @see com.discord.bot.communication.commands.BotMeetingCommand#executeCommand(MessageChannel, Message)
 * @see com.discord.bot.communication.commands.HelpCommand#executeCommand(MessageChannel, Message)
 * @see com.discord.bot.communication.commands.LogCommand#executeCommand(MessageChannel, Message)
 * @see com.discord.bot.communication.commands.MeetingCommand#executeCommand(MessageChannel, Message)
 * @see com.discord.bot.communication.commands.RegisterCommand#executeCommand(MessageChannel, Message)
 * @see com.discord.bot.communication.commands.UnregisterCommand#executeCommand(MessageChannel, Message)
 * @see com.discord.bot.communication.commands.UserCommand#executeCommand(MessageChannel, Message)
 * @see com.discord.bot.communication.commands.UserCommand#executeCommand(MessageChannel, Message)
 * @see com.discord.bot.communication.commands.UserCommand#executeCommand(MessageChannel, Message)
 * @since 1.0
 */
public interface CommandInterface {

    /**
     * This method is called whenever the <code>CommandManager#execute(String, MessageChannel, Message)</code>
     * method is executed, because Discords inputs was made.
     *
     * @param channel Discord channel.
     * @param msg     the Discord input.
     */
    void executeCommand(MessageChannel channel, Message msg);
}
