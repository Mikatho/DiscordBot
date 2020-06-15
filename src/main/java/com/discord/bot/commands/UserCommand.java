package com.discord.bot.commands;

import com.discord.bot.UserManagement;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.Objects;


/**
 * The <code>UserCommand</code> Class implements the <code>CommandInterface</code>
 * to get the variables in the @Override <code>#executeCommand(MessageChannel channel, Message msg)</code>
 * method. It controls syntax and interprets the command to call the right method in the
 * <code>UserManagement</code> class.
 *
 * @author L2G4
 * @version %I%, %G%
 * @see UserManagement#getINSTANCE()
 * @see com.discord.bot.UserManagement
 * @see com.discord.bot.commands.CommandInterface
 * @see com.discord.bot.commands.CommandInterface#executeCommand(MessageChannel, Message)
 * @since 1.0
 */
public class UserCommand implements CommandInterface {

    /*
    !user data
    !user search [userID]
    !user update [value to change] [new value]
     */

    /**
     * This method is called whenever the <code>CommandManager#execute(String, MessageChannel, Message)</code>
     * method is executed, because a Discord input have been made.
     *
     * @param channel Discord channel
     * @param msg     the Discord inputs
     */
    @Override
    public void executeCommand(MessageChannel channel, Message msg) {

        // Command pattern of the usable commands.
        String[] patterns = {
                "!user data",
                "!user search [userID]",
                "!user update address [new value]",
                "!user update interests [new value1, new value2, etc.]",
                "!user update competencies [new value1, new value2, etc.]"};

        //Saves the author of the message.
        User user = msg.getAuthor();
        // Contains all user User data.
        Object[] receivedData;
        // Output of the UserData.
        String data;

        // Creates an instance of the [userManagement].
        UserManagement userManager = UserManagement.getINSTANCE();

        // Checks if the user is existing in the database.
        if (!userManager.userIsRegistered(msg.getAuthor().getId())) {
            user.openPrivateChannel().complete().sendMessage("Please use `!register` first to execute this command.").queue();
            msg.addReaction("U+274C").queue();
            return;
        }

        // Checks if just the command was entered without any parameters.
        if (!msg.getContentRaw().contains(" ")) {
            channel.sendMessage(user.getAsMention() + " Use one of the following patterns:\n" +
                    "```" + patterns[0] + "\n" + patterns[1] + "\n" + patterns[2] + "\n" + patterns[3] + "\n" + patterns[4] + "```").queue();
            msg.addReaction("U+14AF").queue();
            return;
        }

        // Splits the command into the arguments.
        String[] args = msg.getContentRaw().replaceAll(" +", " ").split(" ", 4);

        switch (args[1].toLowerCase()) {

            // If the second argument is [data].
            case "data":

                // Saves the data of the received message about the author.
                if (userManager.search(msg.getAuthor().getId()).length == 0) {
                    channel.sendMessage(user.getAsMention() + " Could not search for the user.").queue();
                    return;
                }
                receivedData = userManager.search(msg.getAuthor().getId());

                // If the bot could not load any data for the message.
                if (receivedData == null) {
                    channel.sendMessage(user.getAsMention() + " Unfortunately we could not load your data.").queue();
                    return;
                }

                // Loads the data of the user out of his instance
                data = "Nickname: " + msg.getAuthor().getName()
                        + "\nAddress: " + receivedData[0]
                        + "\nInterests: " + receivedData[1]
                        + "\nCompetencies: " + receivedData[2]
                        + "\nGoogle Calendar Link: " + userManager.googleCalendarLink(Objects.toString(receivedData[3]));
                channel.sendMessage(data).queue();
                msg.addReaction("U+1F44D").queue();
                break;

            // If the second argument is [search].
            case "search":

                // Checks if all parameters were entered for this argument.
                if (args.length == 2) {
                    channel.sendMessage(user.getAsMention() + " Please add the userID [@userName]!").queue();
                    return;
                }

                // Saves the input [@user] in a string and filters the UserID.
                if (!args[2].matches("<@!\\d{18}>")) {
                    channel.sendMessage(user.getAsMention() + " User is not valid. Please use ` @UserName `!").queue();
                    return;
                }

                String searchID = args[2].substring(3, 21);

                // Checks if the input [UserID] is existing in the database.
                if (!userManager.userIsRegistered(searchID)) {

                    channel.sendMessage(user.getAsMention() + " User does not exist").queue();
                    msg.addReaction("U+1F645").queue();
                    return;
                }

                if (userManager.search(msg.getAuthor().getId()).length == 0) {
                    channel.sendMessage(user.getAsMention() + " Could not search for the user.").queue();
                    return;
                }
                receivedData = userManager.search(searchID);

                // If no data could be loaded the user will be informed about the fail.
                if (receivedData == null) {
                    channel.sendMessage(user.getAsMention() + " Unfortunately we could not load the data.").queue();
                    return;
                }

                // Loads the data of the user out of his instance.
                String nickname = msg.getContentDisplay().split(" ")[2];

                data = "Nickname: " + nickname
                        + "\nAddress: " + receivedData[0]
                        + "\nInterests: " + receivedData[1]
                        + "\nCompetencies: " + receivedData[2];

                channel.sendMessage(data).queue();
                msg.addReaction("U+1F44D").queue();
                break;

            // If the second argument is [break].
            case "update":
                args[2] = args[2].toLowerCase();

                /*
                 * Checks if the first parameter were entered and fits one of the patterns.
                 * !user update address
                 * !user update interests
                 * !user update competencies 
                 */
                if (!args[2].matches("address|interests|competencies")) {
                    channel.sendMessage(String.format("%s Unknown value to update: `%s` does not exist.", user.getAsMention(), args[1])).queue();
                    return;
                }

                // Checks if additional parameters were entered.
                if (args.length == 3) {
                    channel.sendMessage(user.getAsMention() + " Please add the new values!").queue();
                    return;
                }

                // Tries to update the user information that were entered in the command.
                if (!userManager.update(user.getId(), args[2], args[3])) {
                    channel.sendMessage(String.format("%s Could not update your %s", user.getAsMention(), args[2])).queue();
                    return;
                }

                // Informs the user if the task was a success.
                channel.sendMessage(String.format("%s Successfully updated your: %s", user.getAsMention(), args[2])).queue();
                msg.addReaction("U+1F4A3").queue();
                break;

            // If the second argument is [unknown]. The user will be informed that his input was invalid.
            default:
                channel.sendMessage(String.format("%s Unknown command: `%s` does not exist.", user.getAsMention(), args[1])).queue();
                msg.addReaction("U+2753").queue();
                break;
        }
    }
}
