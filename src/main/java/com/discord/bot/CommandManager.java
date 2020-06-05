package com.discord.bot;

import com.discord.bot.commands.*;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.concurrent.ConcurrentHashMap;


/**
 * Instantiate the Command Pattern and execute the <code>CommandInterface</code>.
 *
 * @author      L2G4
 * @version     %I%, %G%
 * @see         com.discord.bot.UserManagement
 * @see         com.discord.bot.MeetingManagement
 * @see         UserManagement#getINSTANCE()
 * @see         MeetingManagement#getINSTANCE()
 * @see         com.discord.bot
 * @since       1.0
 */
public class CommandManager {

    private static final CommandManager INSTANCE = new CommandManager();

    private ConcurrentHashMap<String, CommandInterface> commandsGuild;  // public chat
    private ConcurrentHashMap<String, CommandInterface> commandsPM;     // private chat

    /**
     * Create instances of all objects in CommandPattern and integrate them in ConcurrentHashMaps.
     *
     * @see com.discord.bot.commands.RegisterCommand
     * @see com.discord.bot.commands.DeleteCommand
     * @see com.discord.bot.commands.ClearCommand
     * @see com.discord.bot.commands.HelpCommand
     * @see com.discord.bot.commands.LogCommand
     * @see com.discord.bot.commands.MeetingCommand
     * @see com.discord.bot.commands.ActivityCommand
     * @see com.discord.bot.commands.UserCommand
     * @see com.discord.bot.commands.CommandInterface
     */
    private CommandManager() {

        commandsGuild = new ConcurrentHashMap<>();
        commandsPM = new ConcurrentHashMap<>();

        //Werden vorrausstichtlich wieder gelöscht/ersetzt
        commandsPM.put("register", new RegisterCommand());
        commandsPM.put("unregister", new UnregisterCommand());
        commandsPM.put("clear", new ClearCommand());

        //Commands für Server-Nachrichten
        commandsGuild.put("help", new HelpCommand());
        commandsGuild.put("log", new LogCommand());
        commandsGuild.put("meeting", new MeetingCommand());
        commandsGuild.put("user", new UserCommand());

        //Commands für private Nachrichten an den Bot
        commandsPM.put("activity", new ActivityCommand());
    }

    /**
     * This method return the instance of the CommandManager object.
     *
     * @return  INSTANCE    instance of the CommandManager object.
     */
    public static CommandManager getInstance() {
        return INSTANCE;
    }

    /**
     * Initialize the CommandInterface and send them the discord inputs.
     *
     * @param cmd       first paramter of discord command
     * @param channel   Discord channel
     * @param msg       The Discord inputs.
     * @return  <code>true</code> If the command executes;
     *                  <code>false</code> if the command doesn´t exist in HashMap.
     */
    public boolean execute(String cmd, MessageChannel channel, Message msg) {

        CommandInterface cmdInter;

        /**
         * Save commands in Log List, even unknown commands.
         */
        LoggingManagement.getINSTANCE().addToLog(cmd);

        /**
         * In private user bot chat member is "null"
         */
        if (msg.getMember() == null) {
            cmdInter = commandsPM.get(cmd.substring(1).toLowerCase());
        } else {
            cmdInter = commandsGuild.get(cmd.substring(1).toLowerCase());
        }

        /**
         * If command doesn´t exists in associated HashMap
         */
        if (cmdInter == null) {
            return false;
        }

        /**
         * execute command.
         */
        cmdInter.executeCommand(channel, msg);
        return true;
    }
}
