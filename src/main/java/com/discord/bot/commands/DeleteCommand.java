package com.discord.bot.commands;

import com.discord.bot.UserManagement;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;


/**
 * The <code>DeleteCommand</code> Class implements the <code>CommandInterface</code>
 * to get the variables in the @Override <code>#executeCommand(MessageChannel channel, Message msg)</code>
 * method. It controls syntax and interpret the command to call the right method in the
 * <code>UserManagement</code> class.
 *
 * @author      L2G4
 * @version     %I%, %G%
 * @see         com.discord.bot.commands.CommandInterface
 * @see         com.discord.bot.commands.CommandInterface#executeCommand(MessageChannel, Message)
 * @see         com.discord.bot.UserManagement
 * @see         UserManagement#getINSTANCE() 
 * @see         com.discord.bot.UserManagement#delete(String) 
 * @since       1.0
 */
public class DeleteCommand implements CommandInterface {


    /**
     * This method is called whenever the <code>CommandManager#execute(String, MessageChannel, Message)</code>
     * method is executed, because a Discord input with [!delete] command was made.
     *
     * @param   channel   Discord channel.
     * @param   msg       the Discord input.
     */
    @Override
    public void executeCommand(MessageChannel channel, Message msg) {

        /**
         * Check if the command [!delete] is written without other parameter.
         */
        if (!msg.getContentRaw().contains(" ")) {
            channel.sendMessage("Use `!delete [userID]` to delete!").queue();
            return;
        }

        String[] args = msg.getContentRaw().split(" ");

        /**
         * Delete user in database.
         */
        if (!UserManagement.getINSTANCE().delete(args[1])) {
            channel.sendMessage("Could not delete the User.").queue();
            return;
        }

        channel.sendMessage("Successfully deleted the User.").queue();
    }
}
