package com.discord.bot.commands;

import com.discord.bot.BotMain;
import com.discord.bot.LoggingManagement;
import com.discord.bot.MeetingManagement;
import com.discord.bot.data.BotMeetingMessageData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;


/**
 *  Todo
 *
 * @author      L2G4
 * @version     %I%, %G%
 * @see         LoggingManagement#getINSTANCE()
 * @see         com.discord.bot.commands.CommandInterface
 * @see         com.discord.bot.commands.CommandInterface#executeCommand(MessageChannel, Message)
 * @since       1.0
 */
public class BotMeetingCommand implements CommandInterface {

    final static Logger logger = LogManager.getLogger(BotMeetingCommand.class.getName());

    /**
     * TODO
     *
     * @param   channel   Discord channel.
     * @param   msg       the Discord input.
     */
    @Override
    public void executeCommand(MessageChannel channel, Message msg) {

        MeetingManagement meetingManager = MeetingManagement.getINSTANCE();

        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");

        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        Guild guild = BotMain.getJda().getGuildById(694185735628128338L);

        String[] args = msg.getContentRaw().split(" ", 7);

        int returnedMeetingID;

        long[] earliestMeetingTimes;

        String commandAnswer;

        String foreignUserTag;
        String ourUserID = null;
        String ourUserTag = null;

        String starttime;
        String endtime;

        String startDateISO;

        int duration;

        long epochStart;
        long epochPeriodEnd;

        String message;

        String noTime;

        //Skipt alles, wenn Nachricht nicht von einem Bot kommt oder angefragter User nicht in unserer Datenbank ist
        if (!msg.getAuthor().isBot()) {
            return;
        }

        //Wenn es die erste Nachricht vom anderen Bot ist
        if (args.length == 7) {

            String[] userTags = args[6].split(" ");

            for (String item : userTags) {
                if (meetingManager.userIsRegistered(item.substring(3, 21))) {
                    ourUserTag = item;
                    ourUserID = ourUserTag.substring(3, 21);
                    break;
                }
            }

            if (ourUserTag == null) {
                return;
            }

            String foreignUserID = args[2].substring(2, 20);

            foreignUserTag = msg.getContentDisplay().split(" ")[2];

            duration = Integer.parseInt(args[5]) * 60 * 1000;

            starttime = args[3];

            endtime = starttime.substring(0, 11) + args[4] + ":00";

            noTime = "!_meeting " + args[1] + " " + ourUserTag + " noTime";

            //Versucht Zeiten in Epoch zu parsen
            try {
                Date dateStart = isoFormat.parse(starttime);
                epochStart = dateStart.getTime();

                Date dateEnd = isoFormat.parse(endtime);
                epochPeriodEnd = dateEnd.getTime();
            } catch (ParseException e) {
                logger.fatal("Unable to parse data.\n" + e);
                return;
            }

            try {
                earliestMeetingTimes = meetingManager.earliestPossibleMeeting(ourUserID, epochStart, epochPeriodEnd, duration);
            } catch (SQLException e) {
                logger.fatal("Unable to call method.\n" + e);
                channel.sendMessage(noTime).queue();
                return;
            }

            if (earliestMeetingTimes[0] == 0) {

                channel.sendMessage(noTime).queue();
                return;
            }

            startDateISO = isoFormat.format(earliestMeetingTimes[0]);

            commandAnswer = "!_meeting "
                    + args[1] + " "
                    + ourUserTag + " "
                    + startDateISO;

            meetingManager.getBotMessageHolder().put(args[1], new BotMeetingMessageData("!_meeting " + args[1], foreignUserID, ourUserID, foreignUserTag, duration, startDateISO, epochPeriodEnd, "N/a", false));

            //Gibt Bestätigung mit Daten an den Bot zurück
            channel.sendMessage(commandAnswer).queue();
        } else {

            if (!meetingManager.getBotMessageHolder().containsKey(args[1])) {
                return;
            }

            String savedMessage = meetingManager.getBotMessageHolder().get(args[1]).getMessage();

            if ((savedMessage.length() >= 8)) {

                synchronized (new MeetingCommand()) {
                    MeetingCommand.setFlag(true);
                }
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

            ourUserTag = "<@!" + ourUserID + ">";

            noTime = "!_meeting " + args[1] + " " + ourUserTag + " noTime";

            PrivateChannel ourUserPM;

            try {
                ourUserPM = guild.getMemberById(ourUserID).getUser().openPrivateChannel().complete();
            } catch (NullPointerException e) {
                channel.sendMessage(noTime).queue();
                return;
            }

            if (msg.getContentRaw().equals("!_meeting " + args[1] + ".* noTime")) {
                ourUserPM.sendMessage("I could not arrange a meeting with the other person.").queue();
                return;
            }

            message = meetingManager.getBotMessageHolder().get(args[1]).getMeetingMessage();

            duration = meetingManager.getBotMessageHolder().get(args[1]).getDuration();

            startDateISO = meetingManager.getBotMessageHolder().get(args[1]).getStartDateISO();

            epochPeriodEnd = meetingManager.getBotMessageHolder().get(args[1]).getEpochPeriodEnd();

            starttime = args[3];

            //Versucht Startzeit in Epoch zu parsen
            try {
                Date dateStart = isoFormat.parse(starttime);
                epochStart = dateStart.getTime();
            } catch (ParseException e) {
                logger.fatal("Unable to parse data.\n" + e);
                return;
            }

            if (msg.getContentRaw().matches(savedMessage + ".*" + startDateISO)) {

                if ((returnedMeetingID = meetingManager.insert(hostID, participantID, epochStart, epochStart + duration, "N/a")) == 0) {

                    ourUserPM.sendMessage("Could not create the meeting.").queue();
                    return;
                }

                EmbedBuilder embed = meetingManager.buildEmbed(returnedMeetingID, hostTag, participantTag, format.format(epochStart), format.format(epochStart + duration), message);

                ourUserPM.sendMessage("Successfully created the meeting!\n" + embed.build()).queue();

                /**
                 * Creating the Google Calender link to the event.
                 */
                foreignUserTag = meetingManager.getBotMessageHolder().get(args[1]).getForeignUserTag();
                String hostEventLink = meetingManager.googleCalendarEvent(ourUserID, "Meeting with " + foreignUserTag.substring(1), "N/a", message, epochStart, epochStart + duration);

                /**
                 * Informs the user if the creation of the link to the event was a sucess or a failure.
                 */
                if (hostEventLink == null) {
                    ourUserPM.sendMessage("Could not add meeting to your Google Calendar.").queue();
                } else {
                    ourUserPM.sendMessage("Here is the Google Calendar-Link to your event:\n" + hostEventLink).queue();
                }

                meetingManager.getBotMessageHolder().remove(args[1]);
                return;
            }

            try {
                if ((earliestMeetingTimes = meetingManager.earliestPossibleMeeting(ourUserID, epochStart, epochPeriodEnd, duration))[0] == 0) {

                    channel.sendMessage(noTime).queue();
                    ourUserPM.sendMessage("I could not arrange a meeting with the other person.").queue();
                    return;
                }
            } catch (SQLException e) {
                logger.fatal("SQLException.\n" + e);
                channel.sendMessage(noTime).queue();
                return;
            }

            startDateISO = isoFormat.format(earliestMeetingTimes[0]);

            meetingManager.getBotMessageHolder().get(args[1]).setStartDateISO(startDateISO);

            commandAnswer = "!_meeting "
                    + args[1] + " "
                    + ourUserTag + " "
                    + startDateISO;

            channel.sendMessage(commandAnswer).queue();

            if (commandAnswer.matches(savedMessage + ".*" + startDateISO)) {

                if ((returnedMeetingID = meetingManager.insert(hostID, participantID, earliestMeetingTimes[0], earliestMeetingTimes[1], "N/a")) == 0) {

                    ourUserPM.sendMessage("Could not create the meeting.").queue();
                    return;
                }

                EmbedBuilder embed = meetingManager.buildEmbed(returnedMeetingID, hostTag, participantTag, format.format(earliestMeetingTimes[0]), format.format(earliestMeetingTimes[1]), message);

                ourUserPM.sendMessage("Successfully created the meeting!\n" + embed.build()).queue();

                /**
                 * Creating the Google Calender link to the event.
                 */
                foreignUserTag = meetingManager.getBotMessageHolder().get(args[1]).getForeignUserTag();
                String hostEventLink = meetingManager.googleCalendarEvent(ourUserID, "Meeting with " + foreignUserTag.substring(1), "N/a", message, earliestMeetingTimes[0], earliestMeetingTimes[1]);

                /**
                 * Informs the user if the creation of the link to the event was a sucess or a failure.
                 */
                if (hostEventLink == null) {
                    ourUserPM.sendMessage("Could not add meeting to your Google Calendar.").queue();
                } else {
                    ourUserPM.sendMessage("Here is the Google Calendar-Link to your event:\n" + hostEventLink).queue();
                }
            }
            meetingManager.getBotMessageHolder().get(args[1]).setMessage(commandAnswer);
        }
    }
}
