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

        //Wenn es die erste Nachricht vom anderen Bot ist
        if (args.length == 6) {

        } else {

        }

        MeetingData returnedData;

        String foreignUserID = args[2].substring(3, 21);

        String requestedUserID = args[6].substring(3, 21);

        String endtime = args[3].substring(0, 11) + args[4] + ":00" + args[3].substring(19);

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
        msg.getAuthor().openPrivateChannel().complete().sendMessage(String.format("!_meeting %s %s %s", args[1], isoFormat.format(epochStart) + "+00:00", timeOfEndtime)).queue();

        /**TODO
         * Bestätigung des Termins an unseren User
         */
    }
}
