package com.discord.bot.commands;

import com.discord.bot.UserManagement;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

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
         *  
         * @param   user
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
         * message and will be advised, that the bot creates a Google ID for him.
         */
        channel.sendMessage("Welcome kindly sir.\nCreating your Google-Calendar...\n").queue();

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
