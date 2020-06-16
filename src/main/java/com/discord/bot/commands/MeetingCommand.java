package com.discord.bot.commands;

import com.discord.bot.BotMain;
import com.discord.bot.MeetingManagement;
import com.discord.bot.data.BotMeetingMessageData;
import com.discord.bot.data.MeetingData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.*;


/**
 * The <code>MeetingCommand</code> Class implements the <code>CommandInterface</code>
 * to get the variables in the @Override <code>#executeCommand(MessageChannel channel, Message msg)</code>
 * method. It controls syntax and interprets the commands to call the right method in the
 * <code>MeetingManagement</code> class.
 *
 * @author L2G4
 * @version %I%, %G%
 * @see com.discord.bot.MeetingManagement
 * @see com.discord.bot.data.MeetingData
 * @see com.discord.bot.commands.CommandInterface
 * @since 1.0
 */
public class MeetingCommand implements CommandInterface {

    /*
    !meeting create [@Participant] [starttime] [endtime] [duration in minutes] [optional message]
    !meeting delete [meetingID]
    !meeting update [meetingID] [value to change] [new value]
    !meeting search [meetingID]
     */

    private static boolean flag = false;

    private static final Logger LOGGER = LogManager.getLogger(MeetingCommand.class.getName());

    /*
     * Establishes the standard of the format of the input-date.
     */
    SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");

    /**
     * This method is called whenever the <code>CommandManager#execute(String, MessageChannel, Message)</code>
     * method is executed, because the Discord input [!meeting create | !meeting delete | !meeting update | !meeting search]
     * was made.
     *
     * @param channel Discord channel
     * @param msg     the Discord inputs.
     */
    @Override
    public void executeCommand(MessageChannel channel, Message msg) {

        // Command pattern in string array of meeting commands.
        String[] patterns = {
                "!meeting create [@Participant] [starttime] [endtime] [duration in minutes] [optional message]",
                "!meeting delete [meetingID]",
                "!meeting update [meetingID] [value to change] [new value]",
                "!meeting search [meetingID]"};

        // Regex for the UserID
        String userIdRegex = "<@!\\d{16,20}>";

        String thumbsUp = "U+1F44D";
        String crossedArms = "U+2753";

        User user = msg.getAuthor();

        int returnedMeetingID;

        long[] earliestMeetingTimes;

        Date dateStart;
        Date dateEnd;

        MeetingManagement meetingManager = MeetingManagement.getINSTANCE();

        MeetingData meeting;

        EmbedBuilder embed;

        /*
         * Makes sure that the input is an existing date.
         */
        format.setLenient(false);

        // Checks if the author is a registered user and exists in the database.
        if (!meetingManager.userIsRegistered(msg.getAuthor().getId())) {
            user.openPrivateChannel().complete().sendMessage("Please use `!register` first to execute this command.").queue();
            msg.addReaction("U+274C").queue();
            return;
        }

        /*
         * Checks if the typed command was the basic command [!meeting] without any specifications.
         * In case of YES the bot suggests the possible patterns in chat.
         */
        if (!msg.getContentRaw().contains(" ")) {
            channel.sendMessage(user.getAsMention() + " Use one of the following patterns:\n"
                    + "```" + patterns[0] + "\n" + patterns[1] + "\n" + patterns[2] + "\n" + patterns[3] + "\nNote: Dateformat " + format.toPattern().toUpperCase() + "```").queue();
            msg.addReaction("U+1F4AF").queue();
            return;
        }

        // Splits the message in the parts of the command. Saves the command parts in the arguments.
        String[] args = msg.getContentRaw().replaceAll(" +", " ").split(" ", 3);

        int meetingID;

        // Checks the first parameter in the arguments array of the command call.
        switch (args[1].toLowerCase()) {

            // If second argument is [create].
            case "create":

                long epochStart;

                long epochEnd;

                int duration;

                String meetingMessage;

                // If the command is just [!meeting create] without all required parameters.
                if (args.length == 2) {
                    channel.sendMessage(String.format("%s Use `%s` to create a meeting!", user.getAsMention(), patterns[0])).queue();
                    return;
                }

                String[] createArgs = args[2].split(" ", 7);

                // If not all required parameters were entered the user gets a discription of the command pattern.
                if (createArgs.length != 6 && createArgs.length != 7) {
                    channel.sendMessage(String.format("%s Please add the values like this:%n`%s`", user.getAsMention(), patterns[0])).queue();
                    return;
                }

                // Checks if the participants were entered in the correct format.
                if (!createArgs[0].matches(userIdRegex)) {
                    channel.sendMessage(user.getAsMention() + " User is not valid. Please use ´ @UserName ´!").queue();
                    return;
                }

                String participantID = createArgs[0].substring(3, createArgs[0].length() - 1);

                String participantName = msg.getMentionedUsers().get(0).getName();

                String starttime = createArgs[1] + " " + createArgs[2];
                String endtime = createArgs[3] + " " + createArgs[4];

                // Tries to format the times correctly and checks the existence of date and time.
                try {
                    dateStart = format.parse(starttime);
                    epochStart = dateStart.getTime();

                    dateEnd = format.parse(endtime);
                    epochEnd = dateEnd.getTime();

                    duration = Integer.parseInt(createArgs[5]) * 60 * 1000;
                } catch (ParseException e) {
                    LOGGER.fatal(String.format("Unable to parse the data.%n%s", e));
                    channel.sendMessage(String.format("%s Date is not valid according to `%s` pattern.", user.getAsMention(), format.toPattern().toUpperCase())).queue();
                    return;
                } catch (NumberFormatException e) {
                    LOGGER.fatal(String.format("Unable to parse the data.%n%s", e));
                    channel.sendMessage(user.getAsMention() + " Please add a duration of the meeting!\n Note: Duration has to be in minutes (only the number).").queue();
                    return;
                }

                // Checks if the endtime is after the starttime. Informs user if the input is invalid.
                if (epochStart >= epochEnd) {

                    channel.sendMessage(user.getAsMention() + " The endtime has to be later than the starttime. ").queue();
                    return;
                }

                // Checks if duration of the meeting is longer than the requested period. Informs user if the unput is invalid.
                if (duration > (epochEnd - epochStart)) {
                    channel.sendMessage(user.getAsMention() + " The duration of the meeting cannot be longer than the requested period.").queue();
                    return;
                }

                /*
                 * Checks if the user is free in the requested period. Informs user if the requested period of the meeting
                 * is blocked.
                 */
                try {
                    if (meetingManager.earliestPossibleMeeting(user.getId(), epochStart, epochEnd, duration)[0] == 0) {
                        channel.sendMessage(user.getAsMention() + " You do not have free time during this period.").queue();
                        msg.addReaction("U+1F553").queue();
                        return;
                    }
                } catch (SQLException e) {
                    LOGGER.fatal(String.format("SQLException. Could not receive meeting data.%n%s", e));
                    channel.sendMessage(user.getAsMention() + " Could not receive meeting data.").queue();
                    return;
                }

                // Checks if the user typed an additional message
                if (createArgs.length == 6) {
                    meetingMessage = "N/a";
                } else {
                    meetingMessage = createArgs[6];
                }

                // Checks if the request of a new meeting came from a registered user.
                if (!meetingManager.userIsRegistered(participantID)) {

                    SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

                    String uniqueID = Long.toString(System.currentTimeMillis());

                    String dateStartISO = isoFormat.format(dateStart);

                    Guild guild = BotMain.getJda().getGuildById(721814959704637551L);

                    // Message that the bot sends to the server
                    String answerCommand = "!_meeting "
                            + uniqueID + " "
                            + user.getAsMention() + " "
                            + dateStartISO + " "
                            + createArgs[4] + " "
                            + createArgs[5] + " "
                            + createArgs[0];

                    // Stores meeting data in HashMap
                    meetingManager.getBotMessageHolder().put(uniqueID, new BotMeetingMessageData(msg.getContentRaw(), user.getId(), participantID, participantName, duration, dateStartISO, epochEnd, meetingMessage, true));

                    // Trying to open a private channel with our user
                    try {
                        guild.getTextChannelById(721815086767014008L).sendMessage(answerCommand).queue();
                    } catch (NullPointerException e) {
                        channel.sendMessage(user.getAsMention() + " Unable to message into the right channel.").queue();
                        return;
                    }

                    msg.addReaction(thumbsUp).queue();

                    user.openPrivateChannel().complete().sendMessage("Trying to arrange a meeting...").queue();

                    // Synchronize with BotMeetingCommand to control flag state
                    synchronized (this) {

                        // Defines the length of the timeout
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            LOGGER.fatal(String.format("Unable to pause thread.%n%s", e));
                        }

                        if (!flag) {
                            user.openPrivateChannel().complete().sendMessage("The user " + createArgs[0] + " does not belong to any bot.").queue();
                        }

                        flag = false;
                        return;
                    }
                }

                /*
                 * Checks if the participant is free in the requested period. Informs user if the requested
                 * period of the meeting is blocked.
                 */
                try {
                    earliestMeetingTimes = meetingManager.earliestPossibleMeeting(participantID, epochStart, epochEnd, duration);

                    if (earliestMeetingTimes[0] == 0) {
                        channel.sendMessage(user.getAsMention() + " The participant does not have free time during this period.").queue();
                        msg.addReaction("U+11F625").queue();
                        return;
                    }
                } catch (SQLException e) {
                    LOGGER.fatal(String.format("Unable to get meetingData.%n%s", e));
                    channel.sendMessage(user.getAsMention() + " Could not receive meeting data.").queue();
                    return;
                }

                if ((returnedMeetingID = meetingManager.insert(user.getId(), participantID, earliestMeetingTimes[0], earliestMeetingTimes[1], meetingMessage)) == 0) {
                    channel.sendMessage(user.getAsMention() + " Could not create the meeting.").queue();
                    return;
                }

                embed = meetingManager.buildEmbed(returnedMeetingID, user.getAsMention(), createArgs[0], format.format(earliestMeetingTimes[0]), format.format(earliestMeetingTimes[1]), meetingMessage);

                // If the user has assigned himself to meeting he created he wont be mentioned twice.
                if (user.getName().equals(participantName)) {
                    channel.sendMessage(String.format("%s Successfully created the meeting!", user.getAsMention())).queue();
                } else {
                    channel.sendMessage(String.format("%s %s Successfully created the meeting!", user.getAsMention(), createArgs[0])).queue();
                }
                channel.sendMessage(embed.build()).queue();

                msg.addReaction(thumbsUp).queue();

                // Creating the Google Calender link to the event for both users
                String hostEventLink = meetingManager.googleCalendarEvent(user.getId(), "Meeting with " + participantName, "N/a", String.format("MeetingID: %s%n%s", returnedMeetingID, meetingMessage), earliestMeetingTimes[0], earliestMeetingTimes[1]);

                if (!user.getName().equals(participantName)) {
                    meetingManager.googleCalendarEvent(participantID, "Meeting with " + user.getName(), "N/a", String.format("MeetingID: %s%n%s", returnedMeetingID, meetingMessage), earliestMeetingTimes[0], earliestMeetingTimes[1]);

                }

                // Informs the user if the creation of the link to the event was successful
                if (hostEventLink == null) {
                    user.openPrivateChannel().complete().sendMessage("Could not add meeting to your Google Calendar.").queue();
                } else {
                    user.openPrivateChannel().complete().sendMessage(String.format("Here is the Google Calendar-Link to your event with the ID %s:%n%s", returnedMeetingID, hostEventLink)).queue();
                }
                break;


            // If second argument is [delete].
            case "delete":

                // If the command is just [!meeting delete] without all required parameters.
                if (args.length == 2) {
                    channel.sendMessage(String.format("%s Use `%s` to delete a meeting!", user.getAsMention(), patterns[1])).queue();
                    return;
                }

                try {
                    meetingID = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    LOGGER.fatal(String.format("Unable to parse data.%n%s", e));
                    channel.sendMessage(user.getAsMention() + " Your meetingID is not a number.").queue();
                    return;
                }

                if (!meetingManager.authorizationCheck(meetingID, user.getId())) {

                    channel.sendMessage(user.getAsMention() + " You are not the host of the meeting! Therefore you are not allowed to delete it!").queue();
                    msg.addReaction("U+1F625").queue();
                    return;
                }

                if (!meetingManager.deleteGoogleCalendarEvent(user.getId(), meetingID)) {

                    channel.sendMessage(user.getAsMention() + " Could not delete the meeting out of your Google Calendar.").queue();
                    return;
                }

                meeting = meetingManager.search(meetingID);

                if (meeting == null) {
                    channel.sendMessage(user.getAsMention() + " Could not search for the meeting.").queue();
                    return;
                }
                participantID = meeting.getParticipantID();

                if (meetingManager.userIsRegistered(participantID)) {

                    meetingManager.deleteGoogleCalendarEvent(participantID, meetingID);
                }

                if (!meetingManager.delete(meetingID)) {
                    channel.sendMessage(user.getAsMention() + " Could not delete the meeting.").queue();
                    return;
                }

                channel.sendMessage(user.getAsMention() + " Successfully deleted the meeting.").queue();
                msg.addReaction(thumbsUp).queue();
                break;


            // If second argument is [update].
            case "update":

                // If the command is just [!meeting delete] without all required parameters.
                if (args.length == 2) {
                    channel.sendMessage(String.format("%s Use `%s` to update a meeting!", user.getAsMention(), patterns[2])).queue();
                    return;
                }

                String[] updateArgs = args[2].split(" ", 3);

                // If not all additional parameters were entered the user gets informed.
                if (updateArgs.length != 3) {
                    channel.sendMessage(String.format("%s Please add the values like this:%n`%s`", user.getAsMention(), patterns[2])).queue();
                    return;
                }

                Object newValue;

                updateArgs[1] = updateArgs[1].toLowerCase();

                /*
                 * Tries to convert the MeetingID into a number. If it was not successful the user will be informed that his input
                 * was not valid.
                 */
                try {
                    meetingID = Integer.parseInt(updateArgs[0]);
                } catch (NumberFormatException e) {
                    LOGGER.fatal(String.format("Unable to parse  data.%n%s", e));
                    channel.sendMessage(user.getAsMention() + " Your meetingID is not a valid number.").queue();
                    return;
                }

                if (!meetingManager.authorizationCheck(meetingID, user.getId())) {

                    channel.sendMessage(user.getAsMention() + " You are not the host of the meeting! Therefore you are not allowed to update it!").queue();
                    msg.addReaction("U+1F625").queue();
                    return;
                }

                if (!updateArgs[1].matches("participant|message")) {

                    channel.sendMessage(String.format("%s Unknown value to update: `%s` does not exist.", user.getAsMention(), updateArgs[1])).queue();
                    msg.addReaction(crossedArms).queue();
                    return;
                }

                if (updateArgs[1].equals("participant")) {

                    if (!updateArgs[2].matches(userIdRegex)) {
                        channel.sendMessage(user.getAsMention() + " User is not valid. Please use [@UserName] !").queue();
                        return;
                    }

                    newValue = updateArgs[2].substring(3, updateArgs[2].length() - 1);
                } else {
                    newValue = updateArgs[2];
                }

                if (!meetingManager.update(meetingID, user.getId(), updateArgs[1], newValue)) {
                    channel.sendMessage(user.getAsMention() + " Could not update the Meeting.").queue();
                    return;
                }

                channel.sendMessage(user.getAsMention() + " Successfully updated the Meeting.").queue();
                msg.addReaction(thumbsUp).queue();
                break;

            // If second argument is [search].
            case "search":

                String[] searchArgs = args[2].split(" ");

                long searchParameter;

                //Parses either meetingID or duration of time for the search into long
                try {
                    searchParameter = Long.parseLong(searchArgs[0]);
                } catch (NumberFormatException e) {
                    LOGGER.fatal(String.format("Unable to parse  data.%n%s", e));
                    channel.sendMessage(String.format("%s %s is neither a number nor a valid parameter to search for.", user.getAsMention(), searchArgs[0])).queue();
                    return;
                }

                meeting = meetingManager.search((int) searchParameter);

                // If meeting does not exist
                if (meeting == null) {
                    channel.sendMessage(user.getAsMention() + " This meeting does not exist.").queue();
                    msg.addReaction("U+1F61F").queue();
                    return;
                }

                embed = meetingManager.buildEmbed(meeting.getMeetingID(), "<@!" + meeting.getHostID() + ">", "<@!" + meeting.getParticipantID() + ">", format.format(meeting.getStarttime()), format.format(meeting.getEndtime()), meeting.getMessage());

                channel.sendMessage(embed.build()).queue();
                msg.addReaction(thumbsUp).queue();
                break;

            // If second argument is unknown.
            default:

                channel.sendMessage(String.format("%s Unknown command: `%s` does not exist.", user.getAsMention(), args[1])).queue();
                msg.addReaction(crossedArms).queue();
                break;
        }
    }

    public static void setFlag(boolean flag) {

        MeetingCommand.flag = flag;
    }
}
