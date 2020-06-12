package com.discord.bot.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;


/**
 * Every class in the Command-Pattern has to implement the <code>CommandInterface</code>. It overrides the
 * <code>#executeCommand(MessageChannel channel, Message msg)</code> method which gets the Discord inputs from
 * the <code>CommandManager</code> class.
 *
 * @author      L2G4
 * @version     %I%, %G%
 * @see         com.discord.bot.CommandManager
 * @see         com.discord.bot.CommandManager#execute(String, MessageChannel, Message)
 * @see         com.discord.bot.listeners.CommandListener
 * @see         com.discord.bot.commands.ActivityCommand#executeCommand(MessageChannel, Message)
 * @see         com.discord.bot.commands.BotMeetingCommand#executeCommand(MessageChannel, Message)
 * @see         com.discord.bot.commands.ClearCommand#executeCommand(MessageChannel, Message)
 * @see         com.discord.bot.commands.HelpCommand#executeCommand(MessageChannel, Message)
 * @see         com.discord.bot.commands.LogCommand#executeCommand(MessageChannel, Message)
 * @see         com.discord.bot.commands.MeetingCommand#executeCommand(MessageChannel, Message)
 * @see         com.discord.bot.commands.RegisterCommand#executeCommand(MessageChannel, Message)
 * @see         com.discord.bot.commands.UnregisterCommand#executeCommand(MessageChannel, Message)
 * @see         com.discord.bot.commands.UserCommand#executeCommand(MessageChannel, Message)
 * @see         com.discord.bot.commands.WaiterCommand#executeCommand(MessageChannel, Message)
 * @see         com.discord.bot.commands.UserCommand#executeCommand(MessageChannel, Message)
 * @see         com.discord.bot.commands.NotifyCommand#executeCommand(MessageChannel, Message)
 * @see         com.discord.bot.commands.UserCommand#executeCommand(MessageChannel, Message)
 * @see         com.discord.bot.commands.TestCommand#executeCommand(MessageChannel, Message)
 * @since       1.0
 */
public interface CommandInterface {

    /**
     * This method is called whenever the <code>CommandManager#execute(String, MessageChannel, Message)</code>
     * method is executed, because Discords inputs was made.
     *
     * @param   channel   Discord channel.
     * @param   msg       the Discord input.
     */
    void executeCommand(MessageChannel channel, Message msg);
}
