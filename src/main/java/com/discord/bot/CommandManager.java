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

    private ConcurrentHashMap<String, CommandInterface> commands;  // all chat
    private ConcurrentHashMap<String, CommandInterface> commandsPM;     // private chat

    /**
     * Create instances of all objects in CommandPattern and integrate them in ConcurrentHashMaps.
     *
     * @see com.discord.bot.commands.RegisterCommand
     * @see com.discord.bot.commands.UnregisterCommand
     * @see com.discord.bot.commands.ClearCommand
     * @see com.discord.bot.commands.HelpCommand
     * @see com.discord.bot.commands.LogCommand
     * @see com.discord.bot.commands.MeetingCommand
     * @see com.discord.bot.commands.ActivityCommand
     * @see com.discord.bot.commands.UserCommand
     * @see com.discord.bot.commands.CommandInterface
     */
    private CommandManager() {

        commands = new ConcurrentHashMap<>();
        commandsPM = new ConcurrentHashMap<>();

        //Commands für private Nachrichten
        commandsPM.put("register", new RegisterCommand());
        commandsPM.put("unregister", new UnregisterCommand());

        //Commands für Server-Nachrichten
        commands.put("help", new HelpCommand());
        commands.put("log", new LogCommand());
        commands.put("meeting", new MeetingCommand());
        commands.put("user", new UserCommand());
        //Bot-Bot-Commands
        commands.put("_meeting", new BotMeetingCommand());

        //Werden wieder gelöscht
        commands.put("test", new TestCommand());
        commands.put("clear", new ClearCommand());
        commands.put("wait", new WaiterCommand());
        commands.put("notify", new NotifyCommand());

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
     * @return  <code>0</code> If the command executes;
     *                  <code>1</code> if the command doesn´t exist in HashMap.
     *                  <code>2</code> if the command was written in wrong chat type
     */
    public int execute(String cmd, MessageChannel channel, Message msg) {

        CommandInterface cmdInter;

        /**
         * Save commands in Log List, even unknown commands.
         */
        LoggingManagement.getINSTANCE().addToLog(cmd);

        /**
         * In private user bot chat member is "null"
         * Checks if command was written in private chat and is for private chat only
         */
        if (msg.getMember() == null && commandsPM.containsKey(cmd.substring(1).toLowerCase())) {
            cmdInter = commandsPM.get(cmd.substring(1).toLowerCase());
        } else {
            /**
             * If command was written in public chat but is supposed to be written in private chat
             */
            if (commandsPM.containsKey(cmd.substring(1).toLowerCase())) {
                return 2;
            }
            cmdInter = commands.get(cmd.substring(1).toLowerCase());
        }

        /**
         * If command doesn´t exists in associated HashMap
         */
        if (cmdInter == null) {
            return 1;
        }

        /**
         * executes command
         */
        cmdInter.executeCommand(channel, msg);
        return 0;
    }
}
