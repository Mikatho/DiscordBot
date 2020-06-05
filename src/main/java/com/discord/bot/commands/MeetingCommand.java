package com.discord.bot.commands;

import com.discord.bot.MeetingManagement;
import com.discord.bot.data.MeetingData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MeetingCommand implements CommandInterface {

    /*
    !meeting create [@Participant] [starttime] [endtime] [duration in minutes] [optional message]
    !meeting delete [meetingID]
    !meeting update [meetingID] [value to change] [new value]
     */

    @Override
    public void executeCommand(MessageChannel channel, Message msg) {

        //Patterns des Commands
        String[] patterns = {
                "!meeting create [@Participant] [starttime] [endtime] [duration in minutes] [optional message]",
                "!meeting delete [meetingID]",
                "!meeting update [meetingID] [value to change] [new value]"};

        //Grundgerüst des Embeds zur Ausgabe von Meetings
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(new Color(140, 158, 255))
                .setAuthor("Meeting", null, channel.getJDA().getSelfUser().getAvatarUrl());


        MeetingData returnedData;

        //Regex für UserID
        String userIdRegex = "<@!\\d{18}>";

        //Speichert sich User der Nachricht
        User user = msg.getAuthor();

        MeetingManagement meetingManager = MeetingManagement.getINSTANCE();

        //Setzt Datumsformat vorraus und stellt sicher, dass das Datum existiert
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        format.setLenient(false);

        //Prüft, ob User in Datenbank exisitert
        if (!meetingManager.userIsRegistered(msg.getAuthor().getId())) {
            return;
        }

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

                long epochStart;

                long epochEnd;

                int duration;

                String messageValue;

                Date foundStarttime;

                Date foundEndtime;

                //Wenn nur "!meeting create" eingegeben wurde
                if (args.length == 2) {
                    channel.sendMessage(String.format("Use `%s` to create a meeting!", patterns[0])).queue();
                    return;
                }

                String[] createArgs = args[2].split(" ", 7);

                //Wenn nicht alle Zusatz-Parameter eingegeben wurden
                if (createArgs.length != 6 && createArgs.length != 7) {
                    channel.sendMessage(String.format("Please add the values like this:\n`%s`", patterns[0])).queue();
                    return;
                }

                //Prüft, ob Participant im richtigen Format übergeben wurde
                if (!createArgs[0].matches(userIdRegex)) {
                    channel.sendMessage("User is not valid. Please use ´@UserName´!").queue();
                    return;
                }
                String participantID = createArgs[0].substring(3, 21);

                //Stellt Zeiten aus den Zusatz-Parametern her
                String starttime = createArgs[1] + " " + createArgs[2];
                String endtime = createArgs[3] + " " + createArgs[4];

                //Versucht Zeiten richtig zu formatieren und überprüft, ob Datum & Uhrzeit existieren
                try {
                    //Parsed String in Datum
                    Date dateStart = format.parse(starttime);
                    //Speichert sich Epoch vom Datum (ohne Millisekunden)
                    epochStart = dateStart.getTime() / 1000;

                    Date dateEnd = format.parse(endtime);
                    epochEnd = dateEnd.getTime() / 1000;

                    //Prüft, ob Endzeit des Zeitraums nach Startzeit liegt
                    if (!(epochStart < epochEnd)) {

                        channel.sendMessage("The endtime has to be later than the starttime. ").queue();
                        return;
                    }

                    //Dauer des Meetings in Sekunden
                    duration = Integer.parseInt(createArgs[5]) * 60;
                } catch (ParseException e) {
                    channel.sendMessage("Date is not valid according to `" + format.toPattern().toUpperCase() + "` pattern.").queue();
                    return;
                } catch (NumberFormatException e) {
                    channel.sendMessage("Please add a duration of the meeting!\n Note: Duration has to be in minutes (only the number).").queue();
                    return;
                }

                //Wenn die Länge des Meeting größer ist, als der angefragte Zeitraum
                if (duration > (epochEnd - epochStart)) {
                    channel.sendMessage("The duration of the meeting cannot be longer than the requested period.").queue();
                    return;
                }

                //Prüft, ob Message eingegeben wurde
                if (createArgs.length == 6) {
                    messageValue = "N/a";
                } else {
                    messageValue = createArgs[6];
                }

                //Wenn kein Meeting eingefügt werden konnte
                if ((returnedData = meetingManager.insert(user.getId(), participantID, epochStart, epochEnd, duration, messageValue)) == null) {
                    channel.sendMessage("Could not create the meeting.").queue();
                    return;
                }

                //Konvertiert die finalen Zeiten des Meetings von Epoch in Daten
                foundStarttime = new Date(returnedData.getStarttime() * 1000);
                foundEndtime = new Date(returnedData.getEndtime() * 1000);

                //Embed wird mit restlichen Parametern befüllt
                embedBuilder
                        .addField("Meeting ID", Integer.toString(returnedData.getMeetingID()), false)
                        .addField("Host", user.getAsMention(), true)
                        .addField("Participant", createArgs[0], true)
                        .addBlankField(true)
                        .addField("Starttime", format.format(foundStarttime), true)
                        .addField("Endtime", format.format(foundEndtime), true)
                        .addBlankField(true)
                        .addField("Message", messageValue, true);

                channel.sendMessage("Successfully created the meeting!").queue();
                channel.sendMessage(embedBuilder.build()).queue();

                String eventLink = meetingManager.googleCalendarEvent(user.getId(), "Meeting with " + participantID, "N/a", messageValue, returnedData.getStarttime() * 1000, returnedData.getEndtime() * 1000);

                if (eventLink == null) {
                    channel.sendMessage("Could not add meeting to your Google Calendar.").queue();
                } else {
                    channel.sendMessage("Here is the Google Calendar-Link to your event:\n" + eventLink).queue();
                }
                break;
            case "delete":

                //Wenn nur "!meeting delete" eingegeben wurde
                if (args.length == 2) {
                    channel.sendMessage(String.format("Use `%s` to delete a meeting!", patterns[1])).queue();
                    return;
                }

                try {
                    //Versucht MeetingID in Zahl zu konvertieren
                    int meetingID = Integer.parseInt(args[2]);

                    //Versucht Meeting zu löschen
                    if (!meetingManager.delete(meetingID, user.getId())) {
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

                //Variable zum Speichern des neuen Wertes
                Object newValue;

                updateArgs[1] = updateArgs[1].toLowerCase();

                try {
                    //Versucht MeetingID in Zahl zu konvertieren
                    int meetingID = Integer.parseInt(updateArgs[0]);

                    //Prüft, ob richtiger Wert zum updaten angegeben wurde
                    if (!updateArgs[1].matches("participant|starttime|endtime|message")) {
                        channel.sendMessage(String.format("Unknown value to update: `%s` does not exist.", updateArgs[1])).queue();
                        return;
                    }

                    //Wenn der Wert ein Datum ist wird geprüft, ob das richtige Format eingegeben wurde und das Datum existiert
                    if (updateArgs[1].contains("time")) {

                        try {
                            Date time = format.parse(updateArgs[2]);
                            //Epoch (ohne Millisekunden) wird in Variable zum Übergeben gespeichert
                            newValue = time.getTime() / 1000;
                        } catch (ParseException e) {
                            channel.sendMessage("Date is not valid according to `" + format.toPattern().toUpperCase() + "` pattern.").queue();
                            return;
                        }
                    } else if (updateArgs[1].equals("participant")) {

                        //Prüft, ob Participant im richtigen Format übergeben wurde
                        if (!updateArgs[2].matches(userIdRegex)) {
                            channel.sendMessage("User is not valid. Please use ´@UserName´!").queue();
                            return;
                        }

                        newValue = updateArgs[2].substring(3, 21);
                    } else {

                        newValue = updateArgs[2];
                    }

                    //Versucht Meeting zu updaten
                    if (!meetingManager.update(meetingID, user.getId(), updateArgs[1], newValue)) {
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
