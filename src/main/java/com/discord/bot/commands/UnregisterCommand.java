package com.discord.bot.commands;

import com.discord.bot.UserManagement;
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
 * @see com.discord.bot.DatabaseManagement
 * @see UserManagement#getINSTANCE()
 * @see com.discord.bot.UserManagement#delete(String)
 * @see com.discord.bot.commands.CommandInterface
 * @see com.discord.bot.commands.CommandInterface#executeCommand(MessageChannel, Message)
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

        /**
         * Checks if the user is erasable. If not, replies with feedback message.
         */
        if (!UserManagement.getINSTANCE().delete(msg.getAuthor().getId())) {
            channel.sendMessage("Unfortunately we could not unregister you.").queue();
            return;
        }

        /**
         * If the unregister was successfull, replies with feedback message.
         */
        channel.sendMessage("The unregister was successfull!").queue();
    }
}
