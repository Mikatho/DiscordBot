package com.discord.bot.commands;

import com.discord.bot.MeetingManagement;
<<<<<<< HEAD
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
=======
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
>>>>>>> fa80302b304130282fe7f33c3855d11f3576c846

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class MeetingCommand implements CommandInterface {

    /*
    !meeting create [starttime] [endtime] [message]
    !meeting delete [meetingID]
    !meeting update [meetingID] [value to change] [new value]
     */

    @Override
<<<<<<< HEAD
    public void executeCommand(MessageChannel channel, Message msg) {

        //Speichert sich User der Nachricht
        User user = msg.getAuthor();

        MeetingManagement meetingMng = MeetingManagement.getINSTANCE();

        //Setzt Datumsformat vorraus und stellt sicher, dass das Datum existiert
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        format.setLenient(false);

        //Patterns des Commands
=======
    public void executeCommand(Member m, MessageChannel channel, Message msg) {

        MeetingManagement meetingMng = MeetingManagement.getINSTANCE();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        format.setLenient(false);

>>>>>>> fa80302b304130282fe7f33c3855d11f3576c846
        String[] patterns = {"!meeting create [starttime] [endtime] [message]",
                "!meeting delete [meetingID]",
                "!meeting update [meetingID] [value to change] [new value]"};

<<<<<<< HEAD
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
=======
        String[] args = msg.getContentRaw().split(" ", 3);

        if(args.length == 1) {
            channel.sendMessage("Use one of the following patterns:\n" +
                    patterns[0] + "\n" + patterns[1] + "\n" + patterns[2]).queue();
            return;
        }

        switch (args[1].toLowerCase()) {
            case "create":
                if (args.length == 2) {
                    channel.sendMessage("Use the following pattern:\n" + patterns[0]).queue();
>>>>>>> fa80302b304130282fe7f33c3855d11f3576c846
                    return;
                }

                String[] createArgs = args[2].split(" ", 5);

<<<<<<< HEAD
                //Wenn nicht alle Zusatz-Parameter eingegeben wurden
                if (createArgs.length != 5) {
                    channel.sendMessage(String.format("Please add the values like this:\n`%s`", patterns[0])).queue();
                    return;
                }

                //Stellt Zeiten aus den Zusatz-Parametern her
                String starttime = createArgs[0] + " " + createArgs[1];
                String endtime = createArgs[2] + " " + createArgs[3];

                //Versucht Zeiten richtig zu formatieren und überprüft, ob Datum & Uhrzeit existieren
=======
                if (createArgs.length != 5) {
                    channel.sendMessage("Please add all values like the given pattern:\n" + patterns[0]).queue();
                    return;
                }

                String starttime = createArgs[0] + " " + createArgs[1];
                String endtime = createArgs[2] + " " + createArgs[3];

>>>>>>> fa80302b304130282fe7f33c3855d11f3576c846
                try {
                    format.parse(starttime);
                    format.parse(endtime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

<<<<<<< HEAD
                //Versucht Meeting hinzuzufügen
                if (!meetingMng.insert(user.getId(), starttime, endtime, createArgs[4])) {
=======
                if (!meetingMng.insert(m.getId(), starttime, endtime, createArgs[4])) {
>>>>>>> fa80302b304130282fe7f33c3855d11f3576c846
                    channel.sendMessage("Could not create the Meeting.").queue();
                    return;
                }

                channel.sendMessage("Successfully added the Meeting.").queue();
                break;
            case "delete":
<<<<<<< HEAD

                //Wenn nur "!mmeting delete" eingegeben wurde
                if (args.length == 2) {
                    channel.sendMessage(String.format("Use `%s` to delete a meeting!", patterns[1])).queue();
=======
                if (args.length == 2) {
                    channel.sendMessage("Use the following pattern:\n" + patterns[1]).queue();
>>>>>>> fa80302b304130282fe7f33c3855d11f3576c846
                    return;
                }

                try {
<<<<<<< HEAD
                    //Versucht MeetingID in Zahl zu konvertieren
                    int meetingID = Integer.parseInt(args[2]);

                    //Prüft, ob MeetingID existiert
=======
                    int meetingID = Integer.parseInt(args[2]);

>>>>>>> fa80302b304130282fe7f33c3855d11f3576c846
                    if (!meetingMng.getMeetings().containsKey(meetingID)) {
                        channel.sendMessage("This Meeting does not exist.").queue();
                        return;
                    }

<<<<<<< HEAD
                    //Versucht Meeting zu löschen
=======
>>>>>>> fa80302b304130282fe7f33c3855d11f3576c846
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
<<<<<<< HEAD

                //Wenn nur "!meeting update" eingegeben wurde
                if (args.length == 2) {
                    channel.sendMessage(String.format("Use `%s` to update a meeting!", patterns[2])).queue();
=======
                if (args.length == 2) {
                    channel.sendMessage("Use the following pattern:\n" + patterns[2]).queue();
>>>>>>> fa80302b304130282fe7f33c3855d11f3576c846
                    return;
                }

                String[] updateArgs = args[2].split(" ", 3);

<<<<<<< HEAD
                //Wenn nicht alle Zusatz-Parameter eingegeben wurden
                if (updateArgs.length != 3) {
                    channel.sendMessage(String.format("Please add the values like this:\n`%s`", patterns[2])).queue();
=======
                if (updateArgs.length != 3) {
                    channel.sendMessage("Please add all values like the given pattern:\n" + patterns[2]).queue();
>>>>>>> fa80302b304130282fe7f33c3855d11f3576c846
                    return;
                }

                updateArgs[1] = updateArgs[1].toLowerCase();

                try {
<<<<<<< HEAD
                    //Versucht MeetingID in Zahl zu konvertieren
                    int meetingID = Integer.parseInt(updateArgs[0]);

                    //Prüft, ob MeetingID existiert
=======
                    int meetingID = Integer.parseInt(updateArgs[0]);

>>>>>>> fa80302b304130282fe7f33c3855d11f3576c846
                    if (!meetingMng.getMeetings().containsKey(meetingID)) {
                        channel.sendMessage("This Meeting does not exist.").queue();
                        return;
                    }

<<<<<<< HEAD
                    //Prüft, ob richtiger Wert zum updaten angegeben wurde
                    if (!updateArgs[1].matches("starttime|endtime|message")) {
                        channel.sendMessage(String.format("Unknown value to update: `%s` does not exist.", updateArgs[1])).queue();
                        return;
                    }

                    //Wenn der Wert ein Datum ist wird geprüft, ob das richtige Format eingegeben wurde und das Datum existiert
                    if (updateArgs[1].contains("time")) {
=======
                    if (!updateArgs[1].matches("starttime|endtime|message")) {
                        channel.sendMessage(String.format("Unknown value to update: '%s' does not exist.", updateArgs[1])).queue();
                        return;
                    }

                    if (updateArgs[1].contains("time")) {

>>>>>>> fa80302b304130282fe7f33c3855d11f3576c846
                        try {
                            format.parse(updateArgs[2]);
                        } catch (ParseException e) {
                            channel.sendMessage("Date is not valid according to " + format.toPattern() + " pattern.").queue();
                            return;
                        }
                    }

<<<<<<< HEAD
                    //Versucht Meeting zu updaten
=======
>>>>>>> fa80302b304130282fe7f33c3855d11f3576c846
                    if (!meetingMng.update(meetingID, updateArgs[1], updateArgs[2])) {
                        channel.sendMessage("Could not update the Meeting.").queue();
                        return;
                    }
<<<<<<< HEAD

=======
>>>>>>> fa80302b304130282fe7f33c3855d11f3576c846
                    channel.sendMessage("Successfully updated the Meeting.").queue();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                break;
            default:
<<<<<<< HEAD
                channel.sendMessage(String.format("Unknown command: `%s` does not exist.", args[1])).queue();
=======
                channel.sendMessage(String.format("Unknown command: '%s' does not exist.", args[1])).queue();
>>>>>>> fa80302b304130282fe7f33c3855d11f3576c846
                break;
        }
    }
}
