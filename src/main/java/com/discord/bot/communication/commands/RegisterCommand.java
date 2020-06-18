package com.discord.bot.communication.commands;

import com.discord.bot.communication.CommandInterface;
import com.discord.bot.external_functions.DatabaseManagement;
import com.discord.bot.user.UserManagement;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

/**
 * The <code>RegisterCommand</code> Class implements the <code>CommandInterface</code>
 * to get the variables in the @Override <code>#executeCommand(MessageChannel channel, Message msg)</code>
 * method. It controls syntax and interprets the commands to call the right methods in
 * <code>UserManagement</code>.
 *
 * @author L2G4
 * @version %I%, %G%
 * @see DatabaseManagement
 * @see UserManagement#getINSTANCE()
 * @see UserManagement
 * @see CommandInterface
 * @see CommandInterface#executeCommand(MessageChannel, Message)
 * @since 1.0
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
     * @param channel Discord channel.
     * @param msg     the Discord input.
     */
    @Override
    public void executeCommand(MessageChannel channel, Message msg) {

        // User who typed [!register] is temporary saved in
        User user = msg.getAuthor();

        UserManagement userManager = UserManagement.getINSTANCE();

        // Checks if the author is a registered user and exists in the database.
        if (userManager.userIsRegistered(msg.getAuthor().getId())) {
            channel.sendMessage("You are already registered!").queue();
            msg.addReaction("U+2753").queue();
            return;
        }

        // Checks if the user who tried to register is registered.
        if (!userManager.register(user.getId())) {
            channel.sendMessage(user.getAsMention() + " could not be added to the Database!").queue();
            return;
        }

        /*
         * If the register was successful the user will be greeted with a welcoming
         * message and will be advised, that the bot creates a Google ID for him and that
         * he is able to use the bot commands.
         */
        channel.sendMessage("Welcome kindly sir.\n"
                        + "Use `!help` to receive a list of all available commands.\n"
                        + "Creating your Google-Calendar...\n").queue();
        msg.addReaction("U+1F44A").queue();

        // Creating the Google Calendars ID.
        String calendarID = userManager.googleCalendarID(user.getName());

        /*
         * If the creating of the Google Calenders ID was not successful, the user will
         * be advised that the process failed.
         */
        if (calendarID == null) {
            channel.sendMessage("Unfortunately we could not create a Google Calendar for you.").queue();
        } else {

            /*
             * If the creating of the Google Calenders ID was successful the database
             * gets updated with Link of the Calender belonging to the user.
             *
             * User gets message with the Calender link.
             */
            userManager.update(user.getId(), "gCalendarLink", calendarID);
            channel.sendMessage("Here is your Link to your Google-Calendar:\n" + userManager.googleCalendarLink(calendarID)).queue();
        }
    }
}
