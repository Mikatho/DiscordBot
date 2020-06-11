package com.discord.bot.commands;

import com.discord.bot.MeetingManagement;
import com.discord.bot.data.BotMeetingMessageData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Arrays;
import java.util.Date;

public class MeetingCommand implements CommandInterface {

    /*
    !meeting create [@Participant] [starttime] [endtime] [duration in minutes] [optional message]
    !meeting delete [meetingID]
    !meeting update [meetingID] [value to change] [new value]
     */
    /**
     * This method is called whenever the <code>CommandManager#execute(String, MessageChannel, Message)</code>
     * method is executed, because the Discord input [!meeting create | !meeting delete | !meeting update] 
     * was made.
     *
     * @param channel   Discord channel
     * @param msg       the Discord inputs.
     */

    private static boolean flag = false;

    @Override
    public void executeCommand(MessageChannel channel, Message msg) {

        /**
         * Command pattern in string array of meeting commands.
         */
        String[] patterns = {
                "!meeting create [@Participant] [starttime] [endtime] [duration in minutes] [optional message]",
                "!meeting delete [meetingID]",
                "!meeting update [meetingID] [value to change] [new value]"};

        /**
         * Regex for the UserID
         */
        String userIdRegex = "<@!\\d{18}>";

        /**
         * Saves the author of the message.
         */
        User user = msg.getAuthor();

        int returnedMeetingID;

        long[] earliestMeetingTimes;

        /**
         * Date objects of the input.
         */
        Date dateStart;
        Date dateEnd;

        MeetingManagement meetingManager = MeetingManagement.getINSTANCE();

        /**
         * Establishes the standart of the format of the input-date.
         * Makes sure that the input is an existing date.
         */
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        format.setLenient(false);

        /**
         * Checks if you author is a registered user and exists in the database.
         */
        if (!meetingManager.userIsRegistered(msg.getAuthor().getId())) {
            return;
        }

        //Prüft, ob nur der Command an sich geschrieben wurde
        /**
         * Checks if the typed command was the basic command [!meeting] without any specifications.
         * In case of YES the bot suggests the possible patterns in chat.
         */
        if (!msg.getContentRaw().contains(" ")) {
            channel.sendMessage("Use one of the following patterns:\n"
                    + "```" + patterns[0] + "\n" + patterns[1] + "\n" + patterns[2] + "\nNote: Dateformat " + format.toPattern().toUpperCase() + "```").queue();
            return;
        }
        
        /**
         * Splits the message in the parts of the command. Saves the command parts in the arguments.
         */
        String[] args = msg.getContentRaw().replaceAll(" +", " ").split(" ", 3);

        int meetingID;

        /**
         * Checks the first parameter in the arguments array of the command call.
         */
        switch (args[1].toLowerCase()) {

            /**
             * If second argument is [create].
             */
            case "create":

                long epochStart;

                long epochEnd;

                int duration;

                String meetingMessage;

                /**
                 * If the command is just [!meeting create] without all required parameters.
                 */
                if (args.length == 2) {
                    channel.sendMessage(String.format("Use `%s` to create a meeting!", patterns[0])).queue();
                    return;
                }
                
                /**
                 * Splits the arguments of the required parameter for [!meeting create].
                 */
                String[] createArgs = args[2].split(" ", 7);

                /**
                 * If not all required parameters were entered the user gets a discription of the command pattern.
                 */
                if (createArgs.length != 6 && createArgs.length != 7) {
                    channel.sendMessage(String.format("Please add the values like this:\n`%s`", patterns[0])).queue();
                    return;
                }

                /**
                 * Checks if the participants were entered in the correct format.
                 */
                if (!createArgs[0].matches(userIdRegex)) {
                    channel.sendMessage("User is not valid. Please use ´ @UserName ´!").queue();
                    return;
                }

                String participantID = createArgs[0].substring(3, 21);

                String participantName = msg.getContentDisplay().split(" ")[2].substring(1);

                /**
                 * Creates the start and endtime out of the additional parameters.
                 */
                String starttime = createArgs[1] + " " + createArgs[2];
                String endtime = createArgs[3] + " " + createArgs[4];

                /**
                 * Tries to format the times correctly and checks the existency of date and time.
                 */
                try {
                    //Parsed string into date.
                    dateStart = format.parse(starttime);
                    //Saves the epoch from the time.
                    epochStart = dateStart.getTime();

                    dateEnd = format.parse(endtime);
                    epochEnd = dateEnd.getTime();

                    //The duration of the meeting in milliseconds.
                    duration = Integer.parseInt(createArgs[5]) * 60 * 1000;
                } catch (ParseException e) {
                    //If the date was out of range of the existing dates or does not follow the input pattern.
                    channel.sendMessage("Date is not valid according to `" + format.toPattern().toUpperCase() + "` pattern or date does not exist.").queue();
                    return;
                } catch (NumberFormatException e) {
                    //If no duration of the meeting was added to the command.
                    channel.sendMessage("Please add a duration of the meeting!\n Note: Duration has to be in minutes (only the number).").queue();
                    return;
                }

                /**
                 * Checks if the endtime is after the starttime. Informs user if the input is invalid.
                 */
                if (!(epochStart < epochEnd)) {

                    channel.sendMessage("The endtime has to be later than the starttime. ").queue();
                    return;
                }

                /**
                 * Checks if duration of the meeting is longer than the requested period. Informs user if the unput is invalid.
                 */
                if (duration > (epochEnd - epochStart)) {
                    channel.sendMessage("The duration of the meeting cannot be longer than the requested period.").queue();
                    return;
                }

                /**
                 * Checks if the user is free in the requested period. Informs user if the requested period of the meeting is blocked.
                 */
                try {
                    if (meetingManager.earliestPossibleMeeting(user.getId(), epochStart, epochEnd, duration)[0] == 0) {
                        channel.sendMessage("You do not have free time during this period.").queue();
                        return;
                    }
                } catch (SQLException e) {
                    channel.sendMessage("Could not receive meeting data.").queue();
                    return;
                }

                /**
                 * Checks if message was entered.
                 */
                if (createArgs.length == 6) {
                    meetingMessage = "N/a";
                } else {
                    meetingMessage = createArgs[6];
                }

                /**
                 * Checks if the request of a new meeting came from a registered user.
                 */
                if (!meetingManager.userIsRegistered(participantID)) {

                    SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

                    String uniqueID = "exampleUniqueID";

                    String answerCommand = "!_meeting "
                            + uniqueID + " "
                            + user.getAsMention() + " "
                            + isoFormat.format(dateStart) + "+00:00 "
                            + createArgs[4] + " "
                            + createArgs[5] + " "
                            + createArgs[0];

                    meetingManager.getBotMessageHolder().put(args[1], new BotMeetingMessageData(msg.getContentRaw(), user.getId(), participantID, participantName, duration, epochEnd, meetingMessage, true));

                    channel.sendMessage(answerCommand).queue();

                    /**
                     * Defines how long the while loop will run
                     */
                    long timeout = System.currentTimeMillis() + 2000;

                    while (System.currentTimeMillis() < timeout) {
                        //If user belongs to a bot
                        if (flag) {
                            flag = false;
                            user.openPrivateChannel().complete().sendMessage("Trying to arrange a meeting...").queue();
                            return;
                        }

                        //Pause between bot answer checks
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    user.openPrivateChannel().complete().sendMessage("The user " + createArgs[0] + " does not belong to any bot!").queue();
                    return;
                }

                /**
                 * Checks if the participant is free in the requested period. Informs user if the requested period of the meeting is blocked.
                 */
                try {
                    earliestMeetingTimes = meetingManager.earliestPossibleMeeting(participantID, epochStart, epochEnd, duration);

                    if (earliestMeetingTimes[0] == 0) {
                        channel.sendMessage("The participant does not have free time during this period.").queue();
                        return;
                    }
                } catch (SQLException e) {
                    channel.sendMessage("Could not receive meeting data.").queue();
                    return;
                }

                /**
                 * Informs the user if the meeting could not be created.
                 */
                if ((returnedMeetingID = meetingManager.insert(user.getId(), participantID, earliestMeetingTimes[0], earliestMeetingTimes[1], meetingMessage)) == 0) {
                    channel.sendMessage("Could not create the meeting.").queue();
                    return;
                }

                EmbedBuilder embed = meetingManager.buildEmbed(channel.getJDA().getSelfUser().getAvatarUrl(), returnedMeetingID, user.getAsMention(), createArgs[0], format.format(earliestMeetingTimes[0]), format.format(earliestMeetingTimes[1]), meetingMessage);

                /**
                 * If the user has assigned himself to meeting he created he wont be mentioned twice.
                 */
                if (user.getName().equals(participantName)) {
                    channel.sendMessage(String.format("%s Successfully created the meeting!", user.getAsMention())).queue();
                } else {
                    channel.sendMessage(String.format("%s %s Successfully created the meeting!", user.getAsMention(), createArgs[0])).queue();
                }
                channel.sendMessage(embed.build()).queue();

                /**
                 * Creating the Google Calender link to the event for both users
                 */
                String hostEventLink = meetingManager.googleCalendarEvent(user.getId(), "Meeting with " + participantName, "N/a", meetingMessage, earliestMeetingTimes[0], earliestMeetingTimes[1]);

                meetingManager.googleCalendarEvent(participantID, String.format("Meeting with %s [%s]", user.getName(), returnedMeetingID), "N/a", meetingMessage, earliestMeetingTimes[0], earliestMeetingTimes[1]);
                
                /**
                 * Informs the user if the creation of the link to the event was a sucess or a failure.
                 */
                if (hostEventLink == null) {
                    channel.sendMessage("Could not add meeting to your Google Calendar.").queue();
                } else {
                    channel.sendMessage("Here is the Google Calendar-Link to your event:\n" + hostEventLink).queue();
                }
                break;


                /**
                * If second argument is [delete].
                */
            case "delete":

                /**
                 * If the command is just [!meeting delete] without all required parameters.
                 */
                if (args.length == 2) {
                    channel.sendMessage(String.format("Use `%s` to delete a meeting!", patterns[1])).queue();
                    return;
                }

                /**
                 * Tries to convert the MeetingID into a number. If it failes the user will be informed, that is input was invalid.
                 */
                try {
                    meetingID = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    channel.sendMessage("Your meetingID is not a valid number.").queue();
                    return;
                }

                /**
                 * Checks if the user has the authorization to delete this meeting.
                 */
                if (!meetingManager.authorizationCheck(meetingID, user.getId())) {
                    channel.sendMessage("You are not the host of the meeting! Therefore you are not allowed to delete it!").queue();
                    return;
                }

                /**
                 * Tries to delete the meeting out of the private Google Calender of the user.
                 */
                if (!meetingManager.deleteGoogleCalendarEvent(user.getId(), meetingID)) {
                    channel.sendMessage("Could not delete the meeting out of your Google Calendar.").queue();
                    return;
                }

                /**
                 * Search for the MeetingID and checks if the user is a participant the meeting he wants to delete.
                 */
                participantID = (String) meetingManager.search(meetingID)[1];

                if (meetingManager.userIsRegistered(participantID)) {
                    meetingManager.deleteGoogleCalendarEvent(participantID, meetingID);
                }

                /**
                 * Tries to delete the meeting from the database.
                 */
                if (!meetingManager.delete(meetingID)) {
                    channel.sendMessage("Could not delete the meeting.").queue();
                    return;
                }

                /**
                 * If the meeting was sucessfully deleted the user gets informed.
                 */
                channel.sendMessage("Successfully deleted the meeting.").queue();
                break;


                /**
                * If second argument is [update].
                */
            case "update":

                /**
                 * If the command is just [!meeting delete] without all required parameters.
                 */
                if (args.length == 2) {
                    channel.sendMessage(String.format("Use `%s` to update a meeting!", patterns[2])).queue();
                    return;
                }

                /**
                 * Splits the arguments of the required parameter for [!meeting update].
                 */
                String[] updateArgs = args[2].split(" ", 3);

                /**
                 * If not all additional parameters were entered the user gets informed.
                 */
                if (updateArgs.length != 3) {
                    channel.sendMessage(String.format("Please add the values like this:\n`%s`", patterns[2])).queue();
                    return;
                }

                /**
                 * Variable to save the new value.
                 */
                Object newValue;

                updateArgs[1] = updateArgs[1].toLowerCase();

                /**
                 * Tries to convert the MeetingID into a number. If it was not sucessfull the user will be informed that his input
                 * was not valid.
                 */
                try {
                    meetingID = Integer.parseInt(updateArgs[0]);
                } catch (NumberFormatException e) {
                    channel.sendMessage("Your meetingID is not a valid number.").queue();
                    return;
                }

                /**
                 * Checks if the user has the authorization to update this meeting. 
                 */
                if (!meetingManager.authorizationCheck(meetingID, user.getId())) {
                    channel.sendMessage("You are not the host of the meeting! Therefore you are not allowed to update it!").queue();
                    return;
                }

                /**
                 * Checks if the input is valid to update the meeting. If not the user will be informed.
                 */
                if (!updateArgs[1].matches("participant|starttime|endtime|message")) {
                    channel.sendMessage(String.format("Unknown value to update: `%s` does not exist.", updateArgs[1])).queue();
                    return;
                }

                /**
                 * If the entry is a date the input will be checked if it is in the correct format and if the date exists.
                 */
                if (updateArgs[1].contains("time")) {

                    try {
                        Date time = format.parse(updateArgs[2]);
                        /**
                         * Epoch is getting saved in variable (not in milliseconds) for the transmission.
                         */
                        newValue = time.getTime() / 1000;
                    } catch (ParseException e) {
                        channel.sendMessage("Date is not valid according to `" + format.toPattern().toUpperCase() + "` pattern.").queue();
                        return;
                    }
                
                /**
                 * If the entry is a participant the input will be checked if it is in the correct format. If it is invalid the
                 * user will be informed.
                 */
                } else if (updateArgs[1].equals("participant")) {

                    if (!updateArgs[2].matches(userIdRegex)) {
                        channel.sendMessage("User is not valid. Please use ´ @UserName ´!").queue();
                        return;
                    }

                    newValue = updateArgs[2].substring(3, 21);
                } else {

                    newValue = updateArgs[2];
                }

                //Versucht Meeting zu updaten
                /**
                 * Tries to update the meeting in the database. If it was not sucessfull, the user will be informed.
                 */
                if (!meetingManager.update(meetingID, user.getId(), updateArgs[1], newValue)) {
                    channel.sendMessage("Could not update the Meeting.").queue();
                    return;
                }

                /**
                 * Informs the user that his request to update the meeting was sucessfull.
                 */
                channel.sendMessage("Successfully updated the Meeting.").queue();
                break;

                /**
                * If second argument is unknown.
                */
            default:
                
                /**
                 * Informs the user that is input is an unknown command.
                 */
                channel.sendMessage(String.format("Unknown command: `%s` does not exist.", args[1])).queue();
                break;
        }
    }

    public void setFlag(boolean flag) {
        MeetingCommand.flag = flag;
    }
}
