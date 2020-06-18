package com.discord.bot.communication.commands;

import com.discord.bot.communication.CommandInterface;
import com.discord.bot.external_functions.DatabaseManagement;
import com.discord.bot.user.UserManagement;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

/**
 * The <code>UnregisterCommand</code> Class implements the <code>CommandInterface</code>
 * to get the variables in the @Override <code>#executeCommand(MessageChannel channel, Message msg)</code>
 * method. It controls syntax and interprets the commands to call the right methods in
 * <code>UserManagement</code>.
 *
 * @author L2G4
 * @version %I%, %G%
 * @see DatabaseManagement
 * @see UserManagement#getINSTANCE()
 * @see UserManagement#delete(String)
 * @see CommandInterface
 * @see CommandInterface#executeCommand(MessageChannel, Message)
 * @since 1.0
 */
public class UnregisterCommand implements CommandInterface {

    /*
    !unregister
    */

    /**
     * This method is called whenever the user types !unregister to unregister himself
     * from the database.
     *
     * @param channel Discord channel.
     * @param msg     the Discord input.
     */
    @Override
    public void executeCommand(MessageChannel channel, Message msg) {

        UserManagement userManager = UserManagement.getINSTANCE();

        // Checks if the author is a registered user and exists in the database.
        if (!userManager.userIsRegistered(msg.getAuthor().getId())) {
            channel.sendMessage("You are not even registered.\nPlease use `!register` first to execute this command.").queue();
            msg.addReaction("U+274C").queue();
            return;
        }

        // Checks if the user is erasable. If not, replies with feedback message.
        if (!userManager.delete(msg.getAuthor().getId())) {
            channel.sendMessage("Unfortunately we could not unregister you.").queue();
            return;
        }

        // If the unregister was successfull, replies with feedback message.
        channel.sendMessage("The unregister was successful!").queue();
        msg.addReaction("U+1F44B").queue();
    }
}
