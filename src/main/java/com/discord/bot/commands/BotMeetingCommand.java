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
import java.util.HashMap;


/**
 * @author L2G4
 * @version %I%, %G%
 * @see LoggingManagement#getINSTANCE()
 * @see com.discord.bot.commands.CommandInterface
 * @see com.discord.bot.commands.CommandInterface#executeCommand(MessageChannel, Message)
 * @since 1.0
 */
public class BotMeetingCommand implements CommandInterface {

    private static final Logger LOGGER = LogManager.getLogger(BotMeetingCommand.class.getName());

    MeetingManagement meetingManager = MeetingManagement.getINSTANCE();

    /**
     * @param channel Discord channel.
     * @param msg     the Discord input.
     */
    @Override
    public void executeCommand(MessageChannel channel, Message msg) {

        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");

        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        Guild guild = BotMain.getJda().getGuildById(694185735628128338L);

        String[] args = msg.getContentRaw().split(" ", 7);

        final String COMMAND = "!_meeting" ;

        long[] earliestMeetingTimes;

        String commandAnswer;

        String ourUserID = null;
        String ourUserTag = null;

        String starttime;
        String startDateISO;

        int duration;

        long epochStart;
        long epochPeriodEnd;

        String noTime;

        if (!msg.getAuthor().isBot()) {
            return;
        }

        // If it is the first message of the other bot
        if (args.length == 7) {

            String[] userTags = args[6].split(" ");

            // Saves the first user which is ours
            for (String item : userTags) {
                if (meetingManager.userIsRegistered(item.substring(3, 21))) {
                    ourUserTag = item;
                    ourUserID = ourUserTag.substring(3, 21);
                    break;
                }
            }

            // Skips everything if our user is not mentioned in the meeting request
            if (ourUserTag == null) {
                return;
            }

            // Stores meeting data in variables
            String foreignUserID = args[2].substring(2, 20);

            duration = Integer.parseInt(args[5]) * 60 * 1000;

            starttime = args[3];

            String endtime = starttime.substring(0, 11) + args[4] + ":00";

            noTime = COMMAND + args[1] + " " + ourUserTag + " noTime";

            try {
                Date dateStart = isoFormat.parse(starttime);
                epochStart = dateStart.getTime();

                Date dateEnd = isoFormat.parse(endtime);
                epochPeriodEnd = dateEnd.getTime();
            } catch (ParseException e) {
                LOGGER.fatal(String.format("Unable to parse data.%n%s", e));
                channel.sendMessage(noTime).queue();
                return;
            }

            /**
             * If our user has no free time during the period
             */
            try {
                if ((earliestMeetingTimes = meetingManager.earliestPossibleMeeting(ourUserID, epochStart, epochPeriodEnd, duration))[0] == 0) {
                    channel.sendMessage(noTime).queue();
                    return;
                }
            } catch (SQLException e) {
                LOGGER.fatal(String.format("Unable to call method.%n%s", e));
                channel.sendMessage(noTime).queue();
                return;
            }

            startDateISO = isoFormat.format(earliestMeetingTimes[0]);

            commandAnswer = COMMAND
                    + args[1] + " "
                    + ourUserTag + " "
                    + startDateISO;

            // Stores meeting data in HashMap
            meetingManager.getBotMessageHolder().put(args[1], new BotMeetingMessageData(COMMAND + args[1], foreignUserID, ourUserID, "<@!" + foreignUserID + ">", duration, startDateISO, epochPeriodEnd, "N/a", false));

            channel.sendMessage(commandAnswer).queue();
        } else {

            // If the message is not supposed to reach us
            if (!meetingManager.getBotMessageHolder().containsKey(args[1])) {
                return;
            }

            BotMeetingMessageData data = meetingManager.getBotMessageHolder().get(args[1]);

            String savedMessage = data.getMessage();

            // If we started the meeting and it is the first received message
            if ((savedMessage.length() >= 8)) {

                // Synchronize with MeetingCommand to set flag to true
                synchronized (new MeetingCommand()) {
                    MeetingCommand.setFlag(true);
                }
            }

            // Checks if we started the meeting
            boolean firstStep = data.getFirstStep();

            if (firstStep) {
                ourUserID = data.getHostID();
            } else {
                ourUserID = data.getParticipantID();
            }

            ourUserTag = "<@!" + ourUserID + ">";

            noTime = COMMAND + args[1] + " " + ourUserTag + " noTime";

            PrivateChannel ourUserPM;

            // Trying to open a private channel with our user
            try {
                ourUserPM = guild.getMemberById(ourUserID).getUser().openPrivateChannel().complete();
            } catch (NullPointerException e) {
                channel.sendMessage(noTime).queue();
                return;
            }

            // If user of other bot has no free time
            if (msg.getContentRaw().equals(COMMAND + args[1] + ".* noTime")) {
                ourUserPM.sendMessage("I could not arrange a meeting with the other person.").queue();
                return;
            }

            duration = data.getDuration();

            startDateISO = data.getStartDateISO();

            epochPeriodEnd = data.getEpochPeriodEnd();

            starttime = args[3];

            try {
                Date dateStart = isoFormat.parse(starttime);
                epochStart = dateStart.getTime();
            } catch (ParseException e) {
                LOGGER.fatal(String.format("Unable to parse data.%n%s", e));
                return;
            }

            // If agreement of meeting times has been reached
            if (msg.getContentRaw().matches(savedMessage + ".*" + startDateISO)) {

                meetingArrangement(data, epochStart, ourUserPM, format);

                // Removes meeting data from HashMap
                meetingManager.getBotMessageHolder().remove(args[1]);
                return;
            }

            // If our user has no free time during the period
            try {
                earliestMeetingTimes = meetingManager.earliestPossibleMeeting(ourUserID, epochStart, epochPeriodEnd, duration);
                if (earliestMeetingTimes[0] == 0) {

                    channel.sendMessage(noTime).queue();
                    ourUserPM.sendMessage("I could not arrange a meeting with the other person.").queue();
                    return;
                }
            } catch (SQLException e) {
                LOGGER.fatal(String.format("SQLException.%n%s", e));
                channel.sendMessage(noTime).queue();
                return;
            }

            String newStartDate = isoFormat.format(earliestMeetingTimes[0]);

            commandAnswer = COMMAND
                    + args[1] + " "
                    + ourUserTag + " "
                    + newStartDate;

            channel.sendMessage(commandAnswer).queue();

            // If agreement of meeting times has been reached
            if (msg.getContentRaw().matches("!_meeting " + args[1] + ".*" + newStartDate)) {

                meetingArrangement(data, earliestMeetingTimes[0], ourUserPM, format);

                // Removes meeting data from HashMap
                meetingManager.getBotMessageHolder().remove(args[1]);
            }

            // Update of the meeting data in HashMap
            data.setMessage("!_meeting " + args[1]);
            data.setStartDateISO(newStartDate);
        }
    }

    // If agreement of meeting times has been reached
    public void meetingArrangement(BotMeetingMessageData data, long epochStart, PrivateChannel privateChannel, SimpleDateFormat format) {

        int returnedMeetingID;

        String ourUserID;

        String hostID = data.getHostID();
        String hostTag = "<@!" + hostID + ">";

        String participantID = data.getParticipantID();
        String participantTag = "<@!" + participantID + ">";

        int duration = data.getDuration();

        String additionalMessage = data.getMeetingMessage();

        boolean firstStep = data.getFirstStep();

        if ((returnedMeetingID = meetingManager.insert(hostID, participantID, epochStart, epochStart + duration, additionalMessage)) == 0) {
            privateChannel.sendMessage("Could not create the meeting.").queue();
            return;
        }

        EmbedBuilder embed = meetingManager.buildEmbed(returnedMeetingID, hostTag, participantTag, format.format(epochStart), format.format(epochStart + duration), additionalMessage);

        // Sends specific message depending on the host of the meeting
        if (firstStep) {
            ourUserID = hostID;
            privateChannel.sendMessage("Successfully created the meeting!").queue();
        } else {
            ourUserID = participantID;
            privateChannel.sendMessage("Someone arranged a meeting with you.").queue();
        }

        privateChannel.sendMessage(embed.build()).queue();

        // Creating the Google Calender link to the event.
        String foreignUserTag = data.getForeignUserTag();
        String hostEventLink = meetingManager.googleCalendarEvent(ourUserID, "Meeting with " + foreignUserTag.substring(1), "N/a", additionalMessage, epochStart, epochStart + duration);

        // Informs the user if the creation of the link to the event was a sucess or a failure.
        if (hostEventLink == null) {
            privateChannel.sendMessage("Could not add meeting to your Google Calendar.").queue();
        } else {
            privateChannel.sendMessage("Here is the Google Calendar-Link to your event:\n" + hostEventLink).queue();
        }
    }
}
