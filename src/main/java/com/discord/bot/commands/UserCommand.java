package com.discord.bot.commands;

import com.discord.bot.DatabaseManagement;
import com.discord.bot.UserManagement;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.Arrays;


/**
 * The <code>UserCommand</code> Class implements the <code>CommandInterface</code>
 * to get the variables in the @Override <code>#executeCommand(MessageChannel channel, Message msg)</code>
 * method. It controls syntax and interprets the commands to call the right method in the
 * <code>UserManagement</code> class.
 *
 * @author      L2G4
 * @version     %I%, %G%
 * @see         com.discord.bot.commands.CommandInterface
 * @see         com.discord.bot.commands.CommandInterface#executeCommand(MessageChannel, Message)
 * @see         com.discord.bot.UserManagement
 * @since       1.0
 */
public class UserCommand implements CommandInterface {

    /*
    !user data
    !user update [value to change] [new value]
    !user search [userID]
     */
    /**
     * This method is called whenever the <code>CommandManager#execute(String, MessageChannel, Message)</code>
     * method is executed, because a Discord input with [!user] attribute was made.
     *
     * @param channel   Discord channel.
     * @param msg       the Discord inputs.
     */
    @Override
    public void executeCommand(MessageChannel channel, Message msg) {

        User user = msg.getAuthor();
        Object[] receivedData;  // contains all user User data
        String data;    // contains all UserData
        String searchID;    // UserID of wanted person //user search [searchID]

        /**
         * Command patterns.
         */
        String[] patterns = {"!user data", "!user update address [new value]",
                "!user update interests [new value1, new value2, etc.]",
                "!user update competencies [new value1, new value2, etc.]"};

        /**
         * Check if the command [update] is written without other parameter.
         */
        if (!msg.getContentRaw().contains(" ")) {
            channel.sendMessage("Use one of the following patterns:\n" +
                    "```" + patterns[0] + "\n" + patterns[1] + "\n" + patterns[2] + "\n" + patterns[3] + "```").queue();
            return;
        }

        String[] args = msg.getContentRaw().split(" ", 4);

        switch (args[1].toLowerCase()) {
            case "data":

                /**
                 * Get userData and save them in return array.
                 */
                receivedData = UserManagement.getINSTANCE().search(msg.getAuthor().getId());

                /**
                 * Format and send receivedData.
                 */
                data = "Nickname: " + msg.getAuthor().getName()
                        + "\nAddress: " + receivedData[0]
                        + "\nInterests: " + receivedData[1]
                        + "\nCompetencies: " + receivedData[2];
                channel.sendMessage(data).queue();
                break;
            case "update":
                args[2] = args[2].toLowerCase();

                /**
                 * Check if first additional parameter exists: [interests} / [competencies] / [address].
                 */
                if (!args[2].matches("address|interests|competencies")) {
                    channel.sendMessage(String.format("Unknown value to update: `%s` does not exist.", args[1])).queue();
                    return;
                }

                /**
                 * Check for more additional parameter
                 */
                if (args.length == 3) {
                    channel.sendMessage("Please add the new values!").queue();
                    return;
                }

                /**
                 * Call update() method in <code>UserManagement</code> to update
                 * the manipulated userData.
                 */
                if (!UserManagement.getINSTANCE().update(user.getId(), args[2], args[3])) {
                    channel.sendMessage("Could not update your " + args[2]).queue();
                    return;
                }

                channel.sendMessage("Successfully updated your: " + args[2]).queue();
                break;
            case "search":

                /**
                 * Check if the input contains a third parameter[userID].
                 */
                if (args.length == 2) {
                    channel.sendMessage("Please add the userID [@userName]!").queue();
                    return;
                }

                /**
                 * Save the input [@user] and splits the string to get the userID.
                 */
                searchID = msg.getContentRaw().substring( 16, 34 );

                /**
                 * Check if there is an entry of the userID.
                 */
                if(!DatabaseManagement.getINSTANCE().registeredCheck( searchID )) {
                    channel.sendMessage("User doesn´t exist").queue();
                    return;
                }

                /**
                 * ReceivedData calls the search(searchID(@user)) method in UserManagement to
                 * get the userData as Object[].
                 */
                receivedData = UserManagement.getINSTANCE().search( searchID );

                data =    "\nAddress: " + receivedData[0]
                        + "\nInterests: " + receivedData[1]
                        + "\nCompetencies: " + receivedData[2];

                channel.sendMessage(data).queue();
                break;
            default:
                channel.sendMessage(String.format("Unknown command: `%s` does not exist.", args[1])).queue();
                break;
        }
    }
}
