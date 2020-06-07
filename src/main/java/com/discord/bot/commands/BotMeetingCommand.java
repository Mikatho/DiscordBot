package com.discord.bot.commands;

import com.discord.bot.MeetingManagement;
import com.discord.bot.data.MeetingData;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;

public class BotMeetingCommand implements CommandInterface {

    @Override
    public void executeCommand(MessageChannel channel, Message msg) {

        MeetingManagement meetingManager = MeetingManagement.getINSTANCE();

        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");

        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        String[] args = msg.getContentRaw().split(" ");

        long[] returnedData;

        String starttime;
        String endtime;
        String timezone;

        int duration;

        long epochStart;
        long epochEnd;

        //Skipt alles, wenn Nachricht nicht von einem Bot kommt oder angefragter User nicht in unserer Datenbank ist
        if (!msg.getAuthor().isBot()) {
            return;
        }

        if (msg.getContentRaw().equals(meetingManager.getBotMessageHolder().get(args[1]))) {
            //Match and create meeting
            return;
        }

        //Wenn es die erste Nachricht vom anderen Bot ist
        if (args.length == 7) {

            String requestedUserID = args[6].substring(3, 21);

            if (!meetingManager.userIsRegistered(requestedUserID)) {
                return;
            }

            duration = Integer.parseInt(args[5])*60*1000;

            meetingManager.getBotMessageHolder().put(args[1], msg.getContentRaw());

            meetingManager.getBotDurationHolder().put(args[1], duration);

            String foreignUserID = args[2].substring(3, 21);

            starttime = args[3];

            timezone = starttime.substring(19);

            endtime = args[3].substring(0, 11) + args[4] + ":00" + timezone;

            Date foundStarttime;

            Date foundEndtime;

            String timeOfEndtime;

            //Versucht Zeiten in Epoch zu parsen
            try {
                Date dateStart = isoFormat.parse(starttime);
                epochStart = dateStart.getTime();

                Date dateEnd = isoFormat.parse(endtime);
                epochEnd = dateEnd.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
                return;
            }

            try {
                if ((returnedData = meetingManager.earliestPossibleMeeting(requestedUserID, epochStart, epochEnd, duration))[0] == 0) {

                    channel.sendMessage("!_meeting " + args[1] + " noTime").queue();
                    return;
                }
            } catch (SQLException e) {
                channel.sendMessage("!_meeting " + args[1] + " noTime").queue();
                return;
            }

            timeOfEndtime = format.format(epochEnd).split(" ")[1];

            String commandAnswer = "!_meeting "
                    + args[1] + " "
                    + isoFormat.format(epochStart) + timezone + " "
                    + timeOfEndtime;

            //Gibt Bestätigung mit Daten an den Bot zurück
            msg.getAuthor().openPrivateChannel().complete().sendMessage(commandAnswer).queue();
        } else {
            duration = meetingManager.getBotDurationHolder().get(args[1]);

            meetingManager.getBotMessageHolder().put(args[1], msg.getContentRaw());

            starttime = args[2];

            timezone = starttime.substring(19);

            //Versucht Startzeit in Epoch zu parsen
            try {
                Date dateStart = isoFormat.parse(starttime);
                epochStart = dateStart.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
                return;
            }

            epochEnd = epochStart + duration;


        }

        Date foundStarttime = new Date(returnedData.getStarttime());

        Date foundEndtime = new Date(returnedData.getEndtime());

        /**TODO
         * Bestätigung des Termins an unseren User
         */
    }
}
