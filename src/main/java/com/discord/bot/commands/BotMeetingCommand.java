package com.discord.bot.commands;

import com.discord.bot.MeetingManagement;
import com.discord.bot.data.MeetingData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;

public class BotMeetingCommand implements CommandInterface {

    @Override
    public void executeCommand(MessageChannel channel, Message msg) {

        MeetingManagement meetingManager = MeetingManagement.getINSTANCE();

        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        String[] args = msg.getContentRaw().split(" ");

        MeetingData returnedData;

        String foreignUserID = args[2].substring(3, 21);

        String requestedUserID = args[6].substring(3, 21);

        String endtime = args[3].substring(0, 11) + args[4] + ":00+00:00";

        long epochStart;
        long epochEnd;

        //Versucht Zeiten in Epoch zu parsen
        try {
            Date dateStart = isoFormat.parse(args[3]);
            epochStart = dateStart.getTime();

            Date dateEnd = isoFormat.parse(endtime);
            epochEnd = dateEnd.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }

        //Dauer des Meeting in Millisekunden
        int duration = Integer.parseInt(args[5]) * 60 * 1000;

        //Skipt alles, wenn Nachricht nicht von einem Bot kommt oder angefragter User nicht in unserer Datenbank ist
        if (!msg.getAuthor().isBot() || !meetingManager.userIsRegistered(requestedUserID)) {
            return;
        }

        if ((returnedData = meetingManager.insert(foreignUserID, requestedUserID, epochStart, epochEnd, duration, "N/a")) == null) {
            //Wenn kein passender Terming gefunden wurde
            channel.sendMessage(String.format("!_meeting %s noTime", args[1])).queue();
            return;
        }

        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");

        Date foundStarttime = new Date(returnedData.getStarttime());

        Date foundEndtime = new Date(returnedData.getEndtime());

        String timeOfEndtime = format.format(foundEndtime).split(" ")[1];

        //Gibt Bestätigung mit Daten an den Bot zurück
        channel.sendMessage(String.format("!_meeting %s %s %s", args[1], isoFormat.format(epochStart) + "+00:00", timeOfEndtime)).queue();

        //Embed für den User als Rückgabe
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(new Color(140, 158, 255))
                .setAuthor("Meeting", null, channel.getJDA().getSelfUser().getAvatarUrl())
                .addField("Meeting ID", Integer.toString(returnedData.getMeetingID()), false)
                .addField("Host", args[2], true)
                .addField("Participant", args[6], true)
                .addBlankField(true)
                .addField("Starttime", format.format(foundStarttime), true)
                .addField("Endtime", format.format(foundEndtime), true)
                .addBlankField(true)
                .addField("Message", "N/a", true);

        channel.sendMessage(embedBuilder.build()).queue();

        String participantName = msg.getContentDisplay().split(" ")[2].substring(1);

        //Link zum Google Calendar Event wird erstellt
        String eventLink = meetingManager.googleCalendarEvent(requestedUserID, "Meeting with " + participantName, "N/a", "N/a", returnedData.getStarttime(), returnedData.getEndtime());

        if (eventLink == null) {
            channel.sendMessage("Could not add meeting to your Google Calendar.").queue();
        } else {
            channel.sendMessage("Here is the Google Calendar-Link to your event:\n" + eventLink).queue();
        }
    }
}
