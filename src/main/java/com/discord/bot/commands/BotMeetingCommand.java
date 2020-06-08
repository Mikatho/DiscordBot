package com.discord.bot.commands;

import com.discord.bot.MeetingManagement;
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

        int returnedMeetingID;

        long[] earliestMeetingTimes;

        String commandAnswer;

        String foreignUserID;
        String ourUserID;

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

            foreignUserID = args[2].substring(3, 21);

            ourUserID = args[6].substring(3, 21);

            if (!meetingManager.userIsRegistered(ourUserID)) {
                return;
            }

            meetingManager.getBotMessageHolder().put(args[1], msg.getContentRaw());

            duration = Integer.parseInt(args[5])*60*1000;

            starttime = args[3];

            timezone = starttime.substring(19);

            endtime = args[3].substring(0, 11) + args[4] + ":00" + timezone;

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

            meetingManager.getBotValueHolder().put(args[1], new Object[]{foreignUserID, ourUserID, duration, epochEnd});

            try {
                earliestMeetingTimes = meetingManager.earliestPossibleMeeting(ourUserID, epochStart, epochEnd, duration);
            } catch (SQLException e) {
                channel.sendMessage("!_meeting " + args[1] + " noTime").queue();
                return;
            }

            if (earliestMeetingTimes[0] == 0) {

                channel.sendMessage("!_meeting " + args[1] + " noTime").queue();
                return;
            }

            timeOfEndtime = format.format(earliestMeetingTimes[1]).split(" ")[1];

            commandAnswer = "!_meeting "
                    + args[1] + " "
                    + isoFormat.format(earliestMeetingTimes[0]) + timezone + " "
                    + timeOfEndtime;

            //Gibt Bestätigung mit Daten an den Bot zurück
            msg.getAuthor().openPrivateChannel().complete().sendMessage(commandAnswer).queue();
        } else {
            foreignUserID = (String) meetingManager.getBotValueHolder().get(args[1])[0];

            ourUserID = (String) meetingManager.getBotValueHolder().get(args[1])[1];

            duration = (int) meetingManager.getBotValueHolder().get(args[1])[2];

            epochEnd = (long) meetingManager.getBotValueHolder().get(args[1])[3];

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

            try {
                if ((earliestMeetingTimes = meetingManager.earliestPossibleMeeting(ourUserID, epochStart, epochEnd, duration))[0] == 0) {

                    channel.sendMessage("!_meeting " + args[1] + " noTime").queue();
                    return;
                }
            } catch (SQLException e) {
                channel.sendMessage("!_meeting " + args[1] + " noTime").queue();
                return;
            }

            commandAnswer = "!_meeting "
                    + args[1] + " "
                    + isoFormat.format(earliestMeetingTimes[0]) + timezone;

            channel.sendMessage(commandAnswer).queue();

            if (commandAnswer.equals(meetingManager.getBotMessageHolder().get(args[1]))) {
                if ((returnedMeetingID = meetingManager.insert(foreignUserID, ourUserID, earliestMeetingTimes[0], earliestMeetingTimes[1], "N/a")) == 0) {

                    channel.sendMessage("Could not create the meeting.").queue();
                    return;
                }
                /**TODO
                 * Bestätigung des Termins an unseren User
                 */
            }
        }
    }
}
