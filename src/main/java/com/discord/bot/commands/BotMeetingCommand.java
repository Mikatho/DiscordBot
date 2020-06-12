package com.discord.bot.commands;

import com.discord.bot.MeetingManagement;
import com.discord.bot.data.BotMeetingMessageData;
import net.dv8tion.jda.api.EmbedBuilder;
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
        String foreignUserName;
        String ourUserID;

        String starttime;
        String endtime;
        String timezone;

        int duration;

        long epochStart;
        long epochPeriodEnd;

        String message;

        //Skipt alles, wenn Nachricht nicht von einem Bot kommt oder angefragter User nicht in unserer Datenbank ist
        if (!msg.getAuthor().isBot()) {
            return;
        }

        //Wenn es die erste Nachricht vom anderen Bot ist
        if (args.length == 7) {

            ourUserID = args[6].substring(3, 21);

            if (!meetingManager.userIsRegistered(ourUserID)) {
                return;
            }

            MeetingCommand command = new MeetingCommand();

            //Notifies MeetingCommand that user belongs to bot
            command.setFlag(true);

            foreignUserID = args[2].substring(3, 21);

            foreignUserName = msg.getContentDisplay().split(" ")[2].substring(1);

            duration = Integer.parseInt(args[5]) * 60 * 1000;

            starttime = args[3];

            timezone = starttime.substring(19);

            endtime = args[3].substring(0, 11) + args[4] + ":00" + timezone;

            String timeOfEndtime;

            //Versucht Zeiten in Epoch zu parsen
            try {
                Date dateStart = isoFormat.parse(starttime);
                epochStart = dateStart.getTime();

                Date dateEnd = isoFormat.parse(endtime);
                epochPeriodEnd = dateEnd.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
                return;
            }

            meetingManager.getBotMessageHolder().put(args[1], new BotMeetingMessageData(msg.getContentRaw(), foreignUserID, ourUserID, foreignUserName, duration, epochPeriodEnd, "N/a", false));

            try {
                earliestMeetingTimes = meetingManager.earliestPossibleMeeting(ourUserID, epochStart, epochPeriodEnd, duration);
            } catch (SQLException e) {
                channel.sendMessage("!_meeting " + args[1] + " noTime").queue();
                return;
            }

            if (earliestMeetingTimes[0] == 0) {

                channel.sendMessage("!_meeting " + args[1] + " noTime").queue();
                return;
            }

            commandAnswer = "!_meeting "
                    + args[1] + " "
                    + isoFormat.format(earliestMeetingTimes[0]) + timezone + " ";

            //Gibt Bestätigung mit Daten an den Bot zurück
            channel.sendMessage(commandAnswer).queue();
        } else {

            String noTime = "!_meeting " + args[1] + " noTime";

            if (msg.getContentRaw().equals(noTime)) {
                channel.sendMessage("I could not arrange a meeting with the other person.").queue();
                return;
            }

            String hostID = meetingManager.getBotMessageHolder().get(args[1]).getHostID();
            String participantID = meetingManager.getBotMessageHolder().get(args[1]).getParticipantID();

            String hostTag = "<@!" + hostID + ">";
            String participantTag = "<@!" + participantID + ">";

            if (meetingManager.getBotMessageHolder().get(args[1]).isFirstStep()) {

                ourUserID = hostID;
            } else {
                ourUserID = participantID;
            }

            message = meetingManager.getBotMessageHolder().get(args[1]).getMeetingMessage();

            duration = meetingManager.getBotMessageHolder().get(args[1]).getDuration();

            epochPeriodEnd = meetingManager.getBotMessageHolder().get(args[1]).getEpochPeriodEnd();

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

            if (msg.getContentRaw().equals(meetingManager.getBotMessageHolder().get(args[1]).getMessage())) {

                if ((returnedMeetingID = meetingManager.insert(hostID, participantID, epochStart, epochStart + duration, "N/a")) == 0) {

                    channel.sendMessage("Could not create the meeting.").queue();
                    return;
                }

                EmbedBuilder embed = meetingManager.buildEmbed(channel.getJDA().getSelfUser().getAvatarUrl(), returnedMeetingID, hostTag, participantTag, format.format(epochStart), format.format(epochStart + duration), message);

                channel.sendMessage("Successfully created the meeting!").queue();

                channel.sendMessage(embed.build()).queue();

                /**
                 * Creating the Google Calender link to the event.
                 */
                foreignUserName = meetingManager.getBotMessageHolder().get(args[1]).getForeignUserName();
                String hostEventLink = meetingManager.googleCalendarEvent(ourUserID, "Meeting with " + foreignUserName, "N/a", message, epochStart, epochStart + duration);

                /**
                 * Informs the user if the creation of the link to the event was a sucess or a failure.
                 */
                if (hostEventLink == null) {
                    channel.sendMessage("Could not add meeting to your Google Calendar.").queue();
                } else {
                    channel.sendMessage("Here is the Google Calendar-Link to your event:\n" + hostEventLink).queue();
                }

                meetingManager.getBotMessageHolder().remove(args[1]);
                return;
            }

            try {
                if ((earliestMeetingTimes = meetingManager.earliestPossibleMeeting(ourUserID, epochStart, epochPeriodEnd, duration))[0] == 0) {

                    channel.sendMessage(noTime).queue();
                    channel.sendMessage("I could not arrange a meeting with the other person.").queue();
                    return;
                }
            } catch (SQLException e) {
                channel.sendMessage(noTime).queue();
                return;
            }

            meetingManager.getBotMessageHolder().get(args[1]).setMessage(msg.getContentRaw());

            commandAnswer = "!_meeting "
                    + args[1] + " "
                    + isoFormat.format(earliestMeetingTimes[0]) + timezone;

            channel.sendMessage(commandAnswer).queue();

            if (commandAnswer.equals(msg.getContentRaw())) {

                if ((returnedMeetingID = meetingManager.insert(hostID, participantID, earliestMeetingTimes[0], earliestMeetingTimes[1], "N/a")) == 0) {

                    channel.sendMessage("Could not create the meeting.").queue();
                    return;
                }

                EmbedBuilder embed = meetingManager.buildEmbed(channel.getJDA().getSelfUser().getAvatarUrl(), returnedMeetingID, hostTag, participantTag, format.format(earliestMeetingTimes[0]), format.format(earliestMeetingTimes[1]), message);

                channel.sendMessage("Successfully created the meeting!").queue();

                channel.sendMessage(embed.build()).queue();

                /**
                 * Creating the Google Calender link to the event.
                 */
                foreignUserName = meetingManager.getBotMessageHolder().get(args[1]).getForeignUserName();
                String hostEventLink = meetingManager.googleCalendarEvent(ourUserID, "Meeting with " + foreignUserName, "N/a", message, earliestMeetingTimes[0], earliestMeetingTimes[1]);

                /**
                 * Informs the user if the creation of the link to the event was a sucess or a failure.
                 */
                if (hostEventLink == null) {
                    channel.sendMessage("Could not add meeting to your Google Calendar.").queue();
                } else {
                    channel.sendMessage("Here is the Google Calendar-Link to your event:\n" + hostEventLink).queue();
                }
            }
        }
    }
}
