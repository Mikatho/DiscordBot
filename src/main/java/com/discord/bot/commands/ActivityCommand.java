package com.discord.bot.commands;

import com.discord.bot.UserManagement;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/**
 * The <code>ActivityCommand</code> Class implements the <code>CommandInterface</code>
 * to get the variables in the @Override <code>#executeCommand(MessageChannel channel, Message msg)</code>
 * method. It controls syntax and interpret the commands to call the right method in the
 * <code>UserManagement</code> class.
 *
 * @author      L2G4
 * @version     %I%, %G%
 * @see         com.discord.bot.UserManagement
 * @see         UserManagement#getINSTANCE() 
 * @see         com.discord.bot.UserManagement#startActivity(String) 
 * @see         com.discord.bot.UserManagement#stopActivity(String) 
 * @see         com.discord.bot.commands.CommandInterface
 * @see         com.discord.bot.commands.CommandInterface#executeCommand(MessageChannel, Message)
 * @since       1.0
 */
public class ActivityCommand implements CommandInterface {

    /*
    !activity running
    !activity start
    !activity stop
     */
    /**
     * This method is called whenever the <code>CommandManager#execute(String, MessageChannel, Message)</code>
     * method is executed, because a Discord input with [!activity] command was made.
     *
     * @param   channel   Discord channel.
     * @param   msg       the Discord input.
     */
    @Override
    public void executeCommand(MessageChannel channel, Message msg) {

        String[] patterns = {
                "!activity running",
                "!activity start",
                "!activity stop"};

        /**
         * Check if the command is typed correctly.
         */
        if (!msg.getContentRaw().contains(" ")) {
            channel.sendMessage("Use one of the following patterns:\n"
                    + "```" + patterns[0] + "\n" + patterns[1] + "\n" + patterns[2] + "```").queue();
            return;
        }

        String[] args = msg.getContentRaw().split(" ");

        /**
         * Get the time of the command call and converts it into the correct pattern.
         */
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        LocalDateTime localTime = LocalDateTime.now();

        switch (args[1].toLowerCase()) {
            case "running":
                //Check if activity is running   todo
                break;
            case "start":
                UserManagement.getINSTANCE().startActivity(formatter.format(localTime));
                break;
            case "stop":
                UserManagement.getINSTANCE().stopActivity(formatter.format(localTime));
                break;
            default:
                channel.sendMessage(String.format("Unknown command: `%s` does not exist.", args[1])).queue();
                break;
        }
    }
}
