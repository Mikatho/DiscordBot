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
    !user search [userID]
    !user update [value to change] [new value]
     */
    /**
     * This method is called whenever the <code>CommandManager#execute(String, MessageChannel, Message)</code>
     * method is executed, because a Discord input have been made.
     *
     * @param channel   Discord channel
     * @param msg       the Discord inputs.
     */
    @Override
    public void executeCommand(MessageChannel channel, Message msg) {

        //Patters des Commands
        String[] patterns = {
                "!user data",
                "!user search [userID]",
                "!user update address [new value]",
                "!user update interests [new value1, new value2, etc.]",
                "!user update competencies [new value1, new value2, etc.]"};

        //Speichert sich User der Nachricht
        User user = msg.getAuthor();
        Object[] receivedData;  // contains all user User data
        String data;    // Ausgabe aller UserData

        UserManagement userManager = UserManagement.getINSTANCE();

        //Prüft, ob User in Datenbank exisitert
        if (!userManager.userIsRegistered(msg.getAuthor().getId())) {
            channel.sendMessage("Please use `!register` first to execute this command.").queue();
            return;
        }

        //Prüft, ob nur der Command an sich geschrieben wurde
        if (!msg.getContentRaw().contains(" ")) {
            channel.sendMessage("Use one of the following patterns:\n" +
                    "```" + patterns[0] + "\n" + patterns[1] + "\n" + patterns[2] + "\n" + patterns[3] + "\n" + patterns[4] + "```").queue();
            return;
        }

        String[] args = msg.getContentRaw().split(" ", 4);

        switch (args[1].toLowerCase()) {
            case "data":
                //Speichert sich Return-Array ab
                receivedData = userManager.search(msg.getAuthor().getId());

                //Wenn UserData nicht zurückgegeben werden konnte
                if (receivedData == null) {
                    channel.sendMessage("Unfortunately we could not load your data.").queue();
                    return;
                }

                //Ruft Daten des Users aus seiner Instanz auf
                data = "Nickname: " + msg.getAuthor().getName()
                        + "\nAddress: " + receivedData[0]
                        + "\nInterests: " + receivedData[1]
                        + "\nCompetencies: " + receivedData[2]
                        + "\nGoogle Calendar Link: " + userManager.googleCalendarLink(Objects.toString(receivedData[3]));
                channel.sendMessage(data).queue();
                break;
            case "search":

                /**
                 * abfrage ob ein dritter Parameter, die UserID mitgegeben wurde.
                 */
                if (args.length == 2) {
                    channel.sendMessage("Please add the userID [@userName]!").queue();
                    return;
                }

                /**
                 * speichert aus der eingabe @user den kompletten String und filtert
                 * die UserID heraus
                 */
                if (!args[2].matches("<@!\\d{18}>")) {
                    channel.sendMessage("User is not valid. Please use ` @UserName `!").queue();
                    return;
                }

                String searchID = args[2].substring(3, 21);

                /**
                 * prüft ob die userID überhaupt in der Datenbank vorhanden ist
                 */
                if(!userManager.userIsRegistered(searchID)) {
                    channel.sendMessage("User doesn´t exist").queue();
                    return;
                }

                receivedData = userManager.search(searchID);

                if (receivedData == null) {
                    channel.sendMessage("Unfortunately we could not load the data.").queue();
                    return;
                }

                //Ruft Daten des Users aus seiner Instanz auf

                String nickname = msg.getContentDisplay().split(" ")[2];

                data = "Nickname: " + nickname
                        + "\nAddress: " + receivedData[0]
                        + "\nInterests: " + receivedData[1]
                        + "\nCompetencies: " + receivedData[2];

                channel.sendMessage(data).queue();
                break;
            case "update":
                args[2] = args[2].toLowerCase();

                //Prüft, ob erster Zusatz-Parameter existiert
                if (!args[2].matches("address|interests|competencies")) {
                    channel.sendMessage(String.format("Unknown value to update: `%s` does not exist.", args[1])).queue();
                    return;
                }

                //Prüft, ob noch weitere Zusatz-Parameter mitgegeben wurden
                if (args.length == 3) {
                    channel.sendMessage("Please add the new values!").queue();
                    return;
                }

                //Versucht User zu updaten
                if (!userManager.update(user.getId(), args[2], args[3])) {
                    channel.sendMessage("Could not update your " + args[2]).queue();
                    return;
                }

                channel.sendMessage("Successfully updated your: " + args[2]).queue();
                break;
            default:
                channel.sendMessage(String.format("Unknown command: `%s` does not exist.", args[1])).queue();
                break;
        }
    }
}
