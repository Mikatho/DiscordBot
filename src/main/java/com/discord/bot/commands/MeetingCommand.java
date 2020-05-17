package com.discord.bot.commands;

import com.discord.bot.DatabaseManagement;
import com.discord.bot.MeetingManagement;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class MeetingCommand implements CommandInterface {

    /*
    !meeting create [starttime] [endtime] [message]
    !meeting delete [meetingID]
    !meeting update [meetingID] [value to change] [new value]
     */

    @Override
    public void executeCommand(MessageChannel channel, Message msg) {

        if (!DatabaseManagement.getINSTANCE().registeredCheck(msg.getAuthor().getId())) {
            channel.sendMessage("Please use `!register` first to execute this command.").queue();
            return;
        }

        //Speichert sich User der Nachricht
        User user = msg.getAuthor();

        MeetingManagement meetingMng = MeetingManagement.getINSTANCE();

        //Setzt Datumsformat vorraus und stellt sicher, dass das Datum existiert
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        format.setLenient(false);

        //Patterns des Commands
        String[] patterns = {"!meeting create [starttime] [endtime] [message]",
                "!meeting delete [meetingID]",
                "!meeting update [meetingID] [value to change] [new value]"};

        //Prüft, ob nur der Command an sich geschrieben wurde
        if (!msg.getContentRaw().contains(" ")) {
            channel.sendMessage("Use one of the following patterns:\n"
                    + "```" + patterns[0] + "\n" + patterns[1] + "\n" + patterns[2] + "\nNote: Dateformat " + format.toPattern().toUpperCase() + "```").queue();
            return;
        }

        String[] args = msg.getContentRaw().split(" ", 3);

        //Prüft ersten Zusatz-Parameter des Commandaufrufs
        switch (args[1].toLowerCase()) {

            case "create":

                //Wenn nur "!meeting create" eingegeben wurde
                if (args.length == 2) {
                    channel.sendMessage(String.format("Use `%s` to create a meeting!", patterns[0])).queue();
                    return;
                }

                String[] createArgs = args[2].split(" ", 5);

                //Wenn nicht alle Zusatz-Parameter eingegeben wurden
                if (createArgs.length != 5) {
                    channel.sendMessage(String.format("Please add the values like this:\n`%s`", patterns[0])).queue();
                    return;
                }

                //Stellt Zeiten aus den Zusatz-Parametern her
                String starttime = createArgs[0] + " " + createArgs[1];
                String endtime = createArgs[2] + " " + createArgs[3];

                //Versucht Zeiten richtig zu formatieren und überprüft, ob Datum & Uhrzeit existieren
                try {
                    format.parse(starttime);
                    format.parse(endtime);
                } catch (ParseException e) {
                    channel.sendMessage("Date is not valid according to `" + format.toPattern().toUpperCase() + "` pattern.").queue();
                    return;
                }

                //Versucht Meeting hinzuzufügen
                if (!meetingMng.insert(user.getId(), starttime, endtime, createArgs[4])) {
                    channel.sendMessage("Could not create the Meeting.").queue();
                    return;
                }

                channel.sendMessage("Successfully added the Meeting.").queue();
                break;
            case "delete":

                //Wenn nur "!mmeting delete" eingegeben wurde
                if (args.length == 2) {
                    channel.sendMessage(String.format("Use `%s` to delete a meeting!", patterns[1])).queue();
                    return;
                }

                try {
                    //Versucht MeetingID in Zahl zu konvertieren
                    int meetingID = Integer.parseInt(args[2]);

                    //Versucht Meeting zu löschen
                    if (!meetingMng.delete(meetingID)) {
                        channel.sendMessage("Could not delete the Meeting.").queue();
                        return;
                    }

                    channel.sendMessage("Successfully deleted the Meeting.").queue();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                break;
            case "update":

                //Wenn nur "!meeting update" eingegeben wurde
                if (args.length == 2) {
                    channel.sendMessage(String.format("Use `%s` to update a meeting!", patterns[2])).queue();
                    return;
                }

                String[] updateArgs = args[2].split(" ", 3);

                //Wenn nicht alle Zusatz-Parameter eingegeben wurden
                if (updateArgs.length != 3) {
                    channel.sendMessage(String.format("Please add the values like this:\n`%s`", patterns[2])).queue();
                    return;
                }

                updateArgs[1] = updateArgs[1].toLowerCase();

                try {
                    //Versucht MeetingID in Zahl zu konvertieren
                    int meetingID = Integer.parseInt(updateArgs[0]);

                    //Prüft, ob richtiger Wert zum updaten angegeben wurde
                    if (!updateArgs[1].matches("starttime|endtime|message")) {
                        channel.sendMessage(String.format("Unknown value to update: `%s` does not exist.", updateArgs[1])).queue();
                        return;
                    }

                    //Wenn der Wert ein Datum ist wird geprüft, ob das richtige Format eingegeben wurde und das Datum existiert
                    if (updateArgs[1].contains("time")) {
                        try {
                            format.parse(updateArgs[2]);
                        } catch (ParseException e) {
                            channel.sendMessage("Date is not valid according to `" + format.toPattern().toUpperCase() + "` pattern.").queue();
                            return;
                        }
                    }

                    //Versucht Meeting zu updaten
                    if (!meetingMng.update(meetingID, updateArgs[1], updateArgs[2])) {
                        channel.sendMessage("Could not update the Meeting.").queue();
                        return;
                    }

                    channel.sendMessage("Successfully updated the Meeting.").queue();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                break;
            default:
                channel.sendMessage(String.format("Unknown command: `%s` does not exist.", args[1])).queue();
                break;
        }
    }
}
