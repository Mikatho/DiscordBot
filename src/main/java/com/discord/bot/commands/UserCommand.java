package com.discord.bot.commands;

import com.discord.bot.DatabaseManagement;
import com.discord.bot.UserManagement;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.Arrays;

public class UserCommand implements CommandInterface {

    /*
    !user data
    !user update [value to change] [new value]
     */

    @Override
    public void executeCommand(MessageChannel channel, Message msg) {

        //Prüft, ob User in Datenbank exisitert
        if (!DatabaseManagement.getINSTANCE().registeredCheck(msg.getAuthor().getId())) {
            channel.sendMessage("Please use `!register` first to execute this command.").queue();
            return;
        }

        //Speichert sich User der Nachricht
        User user = msg.getAuthor();

        //Patters des Commands
        String[] patterns = {"!user data", "!user update address [new value]", "!user update interests [new value1, new value2, etc.]", "!user update competencies [new value1, new value2, etc.]"};

        //Prüft, ob nur der Command an sich geschrieben wurde
        if (!msg.getContentRaw().contains(" ")) {
            channel.sendMessage("Use one of the following patterns:\n" +
                    "```" + patterns[0] + "\n" + patterns[1] + "\n" + patterns[2] + "\n" + patterns[3] + "```").queue();
            return;
        }

        String[] args = msg.getContentRaw().split(" ", 4);

        switch (args[1].toLowerCase()) {
            case "data":
                //Speichert sich Return-Array ab
                Object[] receivedData = DatabaseManagement.getINSTANCE().returnData(msg.getAuthor().getId());

                //Ruft Daten des Users aus seiner Instanz auf
                String data = "Nickname: " + msg.getAuthor().getName()
                        + "\nAddress: " + receivedData[0]
                        + "\nInterests: " + receivedData[1].toString()
                        + "\nCompetencies: " + receivedData[2].toString();
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
                if (!UserManagement.getINSTANCE().update(user.getId(), args[2], args[3])) {
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
