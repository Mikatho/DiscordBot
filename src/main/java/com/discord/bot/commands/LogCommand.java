package com.discord.bot.commands;

import com.discord.bot.LoggingManagement;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;


/**
 * The <code>LogCommand</code> Class implements the <code>CommandInterface</code>
 * to get the variables in the @Override <code>#executeCommand(MessageChannel channel, Message msg)</code>
 * method. It controls syntax and interprets the commands to call the right method in the
 * <code>LoggingManagement</code> class.
 *
 * @author      L2G4
 * @version     %I%, %G%
 * @see         com.discord.bot.commands.CommandInterface
 * @see         com.discord.bot.commands.CommandInterface#executeCommand(MessageChannel, Message)
 * @see         com.discord.bot.LoggingManagement
 * @see         LoggingManagement#getINSTANCE()
 * @since       1.0
 */
public class LogCommand implements CommandInterface {

    /*
    !log show
    !log save
     */
    /**
     * This method is called whenever the <code>CommandManager#execute(String, MessageChannel, Message)</code>
     * method is executed, because a Discord input with [!log] command was made.
     *
     * @param channel   Discord channel
     * @param msg       the Discord inputs.
     */
    @Override
    public void executeCommand(MessageChannel channel, Message msg) {

        String[] patterns = {
                "!log show",
                "!log save"};

        /**
        * Check if the command is typed correctly.
        */
        if (!msg.getContentRaw().contains(" ")) {
            channel.sendMessage("Use one of the following patterns:\n"
                    + "```" + patterns[0] + "\n" + patterns[1] + "```").queue();
            return;
        }

        String[] args = msg.getContentRaw().replaceAll(" +", " ").split(" ");

        /**
         * Check the second parameter of the command.
         */
        switch (args[1].toLowerCase()) {
            case "show":
                channel.sendMessage(LoggingManagement.getINSTANCE().logToConsole()).queue();
                break;
            case "save":
                LoggingManagement.getINSTANCE().saveToFile();
                channel.sendMessage("Successfully saved the log.").queue();
                break;
            default:
                channel.sendMessage(String.format("Unknown command: `%s` does not exist.", args[1])).queue();
        }
    }
}
