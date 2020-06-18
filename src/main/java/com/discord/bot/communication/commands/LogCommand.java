package com.discord.bot.communication.commands;

import com.discord.bot.communication.CommandInterface;
import com.discord.bot.external_functions.LoggingManagement;
import com.discord.bot.user.UserManagement;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;


/**
 * The <code>LogCommand</code> Class implements the <code>CommandInterface</code>
 * to get the variables in the @Override <code>#executeCommand(MessageChannel channel, Message msg)</code>
 * method. It controls syntax and interprets the commands to call the right method in the
 * <code>LoggingManagement</code> class.
 *
 * @author L2G4
 * @version %I%, %G%
 * @see CommandInterface
 * @see CommandInterface#executeCommand(MessageChannel, Message)
 * @see LoggingManagement
 * @see LoggingManagement#getINSTANCE()
 * @since 1.0
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
     * @param channel Discord channel
     * @param msg     the Discord inputs.
     */
    @Override
    public void executeCommand(MessageChannel channel, Message msg) {

        // Command pattern of the usable commands.
        String[] patterns = {
                "!log show",
                "!log save"};

        // Checks if the author is a registered user and exists in the database.
        if (!UserManagement.getINSTANCE().userIsRegistered(msg.getAuthor().getId())) {
            msg.getAuthor().openPrivateChannel().complete().sendMessage("Please use `!register` first to execute this command.").queue();
            msg.addReaction("U+274C").queue();
            return;
        }

        // Check if the command is typed correctly.
        if (!msg.getContentRaw().contains(" ")) {
            channel.sendMessage("Use one of the following patterns:\n"
                    + "```" + patterns[0] + "\n" + patterns[1] + "```").queue();
            msg.addReaction("U+1F4AF").queue();
            return;
        }

        // Splits the command into the arguments.
        String[] args = msg.getContentRaw().replaceAll(" +", " ").split(" ");

        // Check the second parameter of the command.
        switch (args[1].toLowerCase()) {
            // If the second argument is [show].
            case "show":
                channel.sendMessage("```" + LoggingManagement.getINSTANCE().logToConsole() + "```").queue();
                msg.addReaction("U+1F9FE").queue();
                break;

            // If the second argument is [save].
            case "save":
                /*
                 * LoggingManagement saves the log into [commands.log] data.
                 *
                 * Replies with a success messages if the log is saved.
                 */
                LoggingManagement.getINSTANCE().saveToFile();
                channel.sendMessage("Successfully saved the log.").queue();
                msg.addReaction("U+1F4A9").queue();
                break;

            // If the second argument is [unknown].
            default:
                channel.sendMessage(String.format("Unknown command: `%s` does not exist.", args[1])).queue();
                msg.addReaction("U+2753").queue();
        }
    }
}
