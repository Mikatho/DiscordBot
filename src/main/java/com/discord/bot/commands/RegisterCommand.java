package com.discord.bot.commands;

import com.discord.bot.UserManagement;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

/**
 * The <code>RegisterCommand</code> Class implements the <code>CommandInterface</code>
 * to get the variables in the @Override <code>#executeCommand(MessageChannel channel, Message msg)</code>
 * method. It controls syntax and interprets the commands to call the right methods in
 * <code>UserManagement</code>.
 *
 * @author      L2G4
 * @version     %I%, %G%
 * @see         com.discord.bot.DatabaseManagement
 * @see         UserManagement#getINSTANCE()
 * @see         com.discord.bot.UserManagement
 * @see         com.discord.bot.UserManagement#register(userID)
 * @see         LoggingManagement#getINSTANCE()
 * @see         com.discord.bot.commands.CommandInterface
 * @see         com.discord.bot.commands.CommandInterface#executeCommand(MessageChannel, Message)
 * @since       1.0
 */
public class RegisterCommand implements CommandInterface {

    /*
    !register
    */
    /**
     * This method is called whenever the <code>CommandManager#execute(String, MessageChannel, Message)</code>
     * method is executed, because the Discord input [!register] was made.
     * The bot will reply in private chat.
     * 
     * @param   channel   Discord channel.
     * @param   msg       the Discord input.
     */
    @Override
    public void executeCommand(MessageChannel channel, Message msg) {

        /**
         * User who typed [!register] is temporary saved in
         */
        User user = msg.getAuthor();

        UserManagement userManager = UserManagement.getINSTANCE();

        /**
         * Checks if the user who tried to register is registrable.
         */
        if (!userManager.register(user.getId())) {
            channel.sendMessage(user.getAsMention() + " could not be added to the Database!").queue();
            return;
        }
        
        /**
         * If the register was sucessfull the user will be greeted with a welcoming
         * message and will be advised, that the bot creates a Google ID for him and that
         * he is able to use the bot commands.
         */
        channel.sendMessage(
            "Welcome kindly sir.\n" + 
            "Your registration was successful.\n" +
            "Now you can log your commands, insert personal data to your account and create meetings\n" +
            "which will be inserted automatically in your Google Calender.\n" +
            "Creating your Google-Calendar...\n").queue();

        /**
         * Creating the Google Calendars ID.
         */
        String calendarID = userManager.googleCalendarID(user.getName());

        /**
         * If the creating of the Google Calenders ID was not sucessfull, the user will
         * be advised that the process failed.
         */
        if (calendarID == null) {
            channel.sendMessage("Unfortunately we could not create a Google Calendar for you.").queue();
        } else {

            /**
             * If the creating of the Google Calenders ID was sucessfull the database 
             * gets updated with Link of the Calender belonging to the user.
             * 
             * User gets message with the Calender link.
             */
            userManager.update(user.getId(), "gCalendarLink", calendarID);
            channel.sendMessage("Here is your Link to your Google-Calendar:\n" + userManager.googleCalendarLink(calendarID)).queue();
        }
    }
}
