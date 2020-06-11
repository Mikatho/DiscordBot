package com.discord.bot.commands;

import com.discord.bot.MeetingManagement;
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
        String ourUserID;

        String starttime;
        String endtime;
        String timezone;

        int duration;

        long epochStart;
        long epochEnd;

        String message;

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

            MeetingCommand command = new MeetingCommand();

            //Notifies MeetingCommand that user belongs to bot
            command.setFlag(true);

            meetingManager.getBotMessageHolder().put(args[1], msg.getContentRaw());

            foreignUserID = args[2].substring(3, 21);

            ourUserID = args[6].substring(3, 21);

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

            meetingManager.getBotValueHolder().put(args[1], new Object[]{foreignUserID, ourUserID, duration, epochEnd, "N/a", false});

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

            String hostID = (String) meetingManager.getBotValueHolder().get(args[1])[0];
            String participantID = (String) meetingManager.getBotValueHolder().get(args[1])[1];

            String hostTag = "<@!" + hostID + ">";
            String participantTag = "<@!" + participantID + ">";

            if ((Boolean) meetingManager.getBotValueHolder().get(args[1])[5]) {

                ourUserID = hostID;
                foreignUserID = participantID;
            } else {
                foreignUserID = hostID;
                ourUserID = participantID;
            }

            message = (String) meetingManager.getBotValueHolder().get(args[1])[4];

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
                /**
                 * TODO
                 * get Name of Participant
                 */
                String hostEventLink = meetingManager.googleCalendarEvent(ourUserID, "Meeting with ", "N/a", message, earliestMeetingTimes[0], earliestMeetingTimes[1]);

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
