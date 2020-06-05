package com.discord.bot.commands;

import com.discord.bot.UserManagement;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;


/**
 * The <code>RegisterCommand</code> Class implements the <code>CommandInterface</code>
 * to get the variables in the @Override <code>#executeCommand(MessageChannel channel, Message msg)</code>
 * method. It controls syntax and interpret the commands to call the right method in the
 * <code>UserManagement</code> class.
 *
 * @author      L2G4
 * @version     %I%, %G%
 * @see         com.discord.bot.UserManagement
 * @see         UserManagement#getINSTANCE()
 * @see         com.discord.bot.commands.CommandInterface
 * @see         com.discord.bot.commands.CommandInterface#executeCommand(MessageChannel, Message) 
 * @since       1.0
 */
public class RegisterCommand implements CommandInterface {

    /**
     * This method is called whenever the <code>CommandManager#execute(String, MessageChannel, Message)</code>
     * method is executed, because a Discord input with [!register] command was made.
     *
     * @param   channel   Discord channel.
     * @param   msg       the Discord input.
     */
    @Override
    public void executeCommand(MessageChannel channel, Message msg) {

        User user = msg.getAuthor();

        /**
         * Register the user.
         */
        if (!UserManagement.getINSTANCE().register(user.getId())) {
            channel.sendMessage(user.getAsMention() + " could not be added to the Database!").queue();
            return;
        }

        /**
         * Send acknowledgment in private discord channel.
         */
        msg.getAuthor().openPrivateChannel().queue((userChannel) -> userChannel.sendMessage("Congratulations!\n" +
                "You registered successfully\nNow you can create meetings which will be insert automatically " +
                "in your google calender, log your commands and insert personal data to your account.").queue());
    }
}
