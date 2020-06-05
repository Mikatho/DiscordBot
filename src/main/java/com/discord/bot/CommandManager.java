package com.discord.bot;

import com.discord.bot.commands.*;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.concurrent.ConcurrentHashMap;

public class CommandManager {

    private static final CommandManager INSTANCE = new CommandManager();

    private ConcurrentHashMap<String, CommandInterface> commandsGuild;
    private ConcurrentHashMap<String, CommandInterface> commandsPM;

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

        //Commands für private Nachrichten an den Bot
        commandsPM.put("activity", new ActivityCommand());
        commandsPM.put("user", new UserCommand());
    }

    public static CommandManager getInstance() {
        return INSTANCE;
    }

    public boolean execute(String cmd, MessageChannel channel, Message msg) {

        CommandInterface cmdInter;

        //Fügt Command in Log-Liste (auch nicht vorhandene Commands)
        LoggingManagement.getINSTANCE().addToLog(cmd);

        //Member ist bei Nachrichten im privaten Chat mit dem Bot "null"
        if (msg.getMember() == null) {
            cmdInter = commandsPM.get(cmd.substring(1).toLowerCase());
        } else {
            cmdInter = commandsGuild.get(cmd.substring(1).toLowerCase());
        }

        //Wenn der Command (in der jeweiligen HashMap) nicht exisitert
        if (cmdInter == null) {
            return false;
        }

        //Führt Command aus
        cmdInter.executeCommand(channel, msg);
        return true;
    }
}
