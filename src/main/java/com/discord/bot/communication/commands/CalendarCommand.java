package com.discord.bot.communication.commands;

import com.discord.bot.communication.CommandInterface;
import com.discord.bot.communication.CommandManager;
import com.discord.bot.meeting.MeetingManagement;
import com.discord.bot.user.UserManagement;
import com.discord.bot.meeting.MeetingData;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * Contain all commands and the function to send them to a users private channel.
 *
 * @author L2G4
 * @version %I%, %G%
 * @see CommandManager
 * @see CommandInterface
 * @see CommandInterface#executeCommand(MessageChannel, Message)
 * @since 1.0
 */
public class CalendarCommand implements CommandInterface {

    /*
    !cal(endar) all
    !cal(endar) [length] hours
    !cal(endar) [length] days
     */

    private static final Logger LOGGER = LogManager.getLogger(CalendarCommand.class.getName());

    /*
     * Establishes the standard of the format of the input-date.
     */
    SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");

    /**
     *This method is called whenever the <code>CommandManager#execute(String, MessageChannel, Message)</code>
     * method is executed, because the Discord input [!cal] or [!calendar] was made. It returns all command patterns to the
     * users private discord channel.
     *
     * @param channel Discord channel.
     * @param msg     the Discord input.
     */
    @Override
    public void executeCommand(MessageChannel channel, Message msg) {

        // Command pattern of the usable commands.
        String[] patterns = {
                "!cal(endar) all",
                "!cal(endar) [length] hours",
                "!cal(endar) [length] days"};

        MeetingManagement meetingManager = MeetingManagement.getINSTANCE();

        User user = msg.getAuthor();

        List<MeetingData> meetingList;

        long searchParameter;

        String thumbsUp = "U+1F44D";
        String worriedFace = "U+1F61F";

        // Checks if the author is a registered user and exists in the database.
        if (!UserManagement.getINSTANCE().userIsRegistered(msg.getAuthor().getId())) {
            msg.getAuthor().openPrivateChannel().complete().sendMessage("Please use `!register` first to execute this command.").queue();
            msg.addReaction("U+274C").queue();
            return;
        }

        // Check if the command is typed correctly.
        if (!msg.getContentRaw().contains(" ")) {
            channel.sendMessage("Use one of the following patterns:\n"
                    + "```" + patterns[0] + "\n" + patterns[1] + "\n" + patterns[2] + "```").queue();
            msg.addReaction("U+1F4AF").queue();
            return;
        }

        String[] searchArgs = msg.getContentRaw().split(" ");

        // If user is searching for all meetings
        if (searchArgs[1].equalsIgnoreCase("all")) {

            meetingList = meetingManager.searchAllMeetings(user.getId(), 0);

            // If no meetings were found
            if (meetingList.isEmpty()) {
                channel.sendMessage(user.getAsMention() + " You do not have any meetings.").queue();
                msg.addReaction(worriedFace).queue();
                return;
            }

            channel.sendMessage("**These are all your meetings:**\n\n").queue();

            for (String message : searchMultipleMeetings(meetingList)) {
                channel.sendMessage(message).queue();
            }
        } else {
            //If second parameter is unknown
            if (!searchArgs[2].toLowerCase().matches("hours|days")) {

                // If the parameters of the message were wrong
                channel.sendMessage(user.getAsMention() + " Unknown value to search for. Please use `hours` or `days`.").queue();
                msg.addReaction("U+2753").queue();
                return;
            }

            //Parses either meetingID or duration of time for the search into long
            try {
                searchParameter = Long.parseLong(searchArgs[1]);
            } catch (NumberFormatException e) {
                LOGGER.fatal(String.format("Unable to parse  data.%n%s", e));
                channel.sendMessage(String.format("%s %s is neither a number nor a valid parameter to search for.", user.getAsMention(), searchArgs[1])).queue();
                return;
            }

            // Casts hours into milliseconds
            searchParameter = searchParameter * 60 * 60 * 1000;

            // Casts days into milliseconds
            if (searchArgs[2].equalsIgnoreCase("days")) {
                searchParameter = searchParameter * 24;
            }

            meetingList = meetingManager.searchAllMeetings(user.getId(), searchParameter);

            // If user does not have any meetings during this period
            if (meetingList.isEmpty()) {
                channel.sendMessage(user.getAsMention() + " You do not have any meetings during this period.").queue();
                msg.addReaction(worriedFace).queue();
                return;
            }

            channel.sendMessage(String.format("**These are all your meetings in the next %s %s:**%n%n", searchArgs[1], searchArgs[2])).queue();

            for (String message : searchMultipleMeetings(meetingList)) {
                channel.sendMessage(message).queue();
            }

        }
        msg.addReaction(thumbsUp).queue();
    }

    // Splits list of meetings into seperate meetings and puts them into packages of 5 to send to user
    public List<String> searchMultipleMeetings(List<MeetingData> meetings) {

        StringBuilder builder = new StringBuilder();

        List<String> returnMessages = new ArrayList<>();

        // Iterates through all meetings
        for (MeetingData data : meetings) {
            String message = data.getMessage();

            if (message == null) {
                message = "N/a";
            }

            String start = format.format(data.getStarttime());
            String end = format.format(data.getEndtime());

            // If the date of start and end is the same it cuts the date of the endtime
            if (start.split(" ")[0].equals(end.split(" ")[0])) {
                end = end.split(" ")[1];
            }

            builder.append(String.format("*MeetingID:*  %s%n```Message: %s%n%s - %s```%n", data.getMeetingID(), message, start, end));

            // Saves every 5th string in array
            if ((builder.length() % 5) == 0) {
                returnMessages.add(builder.toString());
                builder.setLength(0);
            }
        }
        returnMessages.add(builder.toString());
        return returnMessages;
    }
}
