package com.discord.bot;

import com.discord.bot.commands.*;
<<<<<<< HEAD
=======
import net.dv8tion.jda.api.entities.Member;
>>>>>>> fa80302b304130282fe7f33c3855d11f3576c846
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.concurrent.ConcurrentHashMap;

public class CommandManager {

    private static final CommandManager INSTANCE = new CommandManager();

<<<<<<< HEAD
    private ConcurrentHashMap<String, CommandInterface> commandsGuild;
    private ConcurrentHashMap<String, CommandInterface> commandsPM;

    private CommandManager() {

        commandsGuild = new ConcurrentHashMap<>();
        commandsPM = new ConcurrentHashMap<>();

        //Werden vorrausstichtlich wieder gelöscht/ersetzt
        commandsPM.put("register", new RegisterCommand());
        commandsPM.put("delete", new DeleteCommand());
        commandsPM.put("clear", new ClearCommand());

        //Commands für Server-Nachrichten
        commandsGuild.put("help", new HelpCommand());
        commandsGuild.put("log", new LogCommand());
        commandsGuild.put("meeting", new MeetingCommand());

        //Commands für private Nachrichten an den Bot
        commandsPM.put("activity", new ActivityCommand());
        commandsPM.put("data", new DataCommand());
        commandsPM.put("update", new UpdateCommand());
=======
    private ConcurrentHashMap<String, CommandInterface> commands;

    private CommandManager() {
        commands = new ConcurrentHashMap<>();

        //Werden wieder gelöscht/ersetzt
        commands.put("register", new RegisterCommand());
        commands.put("delete", new DeleteCommand());
        commands.put("clear", new ClearCommand());

        commands.put("help", new HelpCommand());
        commands.put("log", new LogCommand());
        commands.put("meeting", new MeetingCommand());
        commands.put("activity", new ActivityCommand());
        commands.put("data", new DataCommand());
        commands.put("update", new UpdateCommand());
>>>>>>> fa80302b304130282fe7f33c3855d11f3576c846
    }

    public static CommandManager getInstance() {
        return INSTANCE;
    }

<<<<<<< HEAD
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
=======
    public boolean execute(String cmd, Member m, MessageChannel channel, Message msg) {
        CommandInterface cmdInter;

        if((cmdInter = commands.get(cmd.substring(1).toLowerCase())) == null) {
            return false;
        }
        cmdInter.executeCommand(m, channel, msg);
        LoggingManagement.getINSTANCE().addToLog(cmd);
>>>>>>> fa80302b304130282fe7f33c3855d11f3576c846
        return true;
    }
}
