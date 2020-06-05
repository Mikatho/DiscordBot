package com.discord.bot.commands;

import com.discord.bot.DatabaseManagement;
import com.discord.bot.MeetingManagement;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * The <code>MeetingCommand</code> Class implements the <code>CommandInterface</code>
 * to get the variables in the @Override <code>#executeCommand(MessageChannel channel, Message msg)</code>
 * method. It controls syntax and interprets the commands to call the right method in the
 * <code>MeetingManagement</code> class.
 *
 * @author      L2G4
 * @version     %I%, %G%
 * @see         com.discord.bot.MeetingManagement
 * @see         com.discord.bot.data.MeetingData
 * @see         com.discord.bot.commands.CommandInterface
 * @since       1.0
 */
public class MeetingCommand implements CommandInterface {

    /*
    !meeting create [@Participant] [starttime] [endtime] [message]
    !meeting delete [meetingID]
    !meeting update [meetingID] [value to change] [new value]
     */
    /**
     * This method is called whenever the <code>CommandManager#execute(String, MessageChannel, Message)</code>
     * method is executed, because a Discord input with [!meeting] command was made.
     *
     * @param channel   Discord channel
     * @param msg       the Discord inputs.
     */
    @Override
    public void executeCommand(MessageChannel channel, Message msg) {

        if (!DatabaseManagement.getINSTANCE().registeredCheck(msg.getAuthor().getId())) {
            channel.sendMessage("Please use `!register` first to execute this command.").queue();
            return;
        }

        User user = msg.getAuthor();
        MeetingManagement meetingMng = MeetingManagement.getINSTANCE();

        /**
         * Check date-format.
         */
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        format.setLenient(false);

        /**
         * Command pattern.
         */
        String[] patterns = {"!meeting create [@Participant] [starttime] [endtime] [message]",
                "!meeting delete [meetingID]",
                "!meeting update [meetingID] [value to change] [new value]"};

        /**
         * Check if the command [meeting] is written without other parameter.
         */
        if (!msg.getContentRaw().contains(" ")) {
            channel.sendMessage("Use one of the following patterns:\n"
                    + "```" + patterns[0] + "\n" + patterns[1] + "\n" + patterns[2] + "\nNote: Dateformat " + format.toPattern().toUpperCase() + "```").queue();
            return;
        }

        String[] args = msg.getContentRaw().split(" ", 3);

        /**
         * Check for first additional parameter: [create} / [delete] / [update].
         */
        switch (args[1].toLowerCase()) {

            case "create":

                /**
                 * Check if the command [!meeting create] is written without other parameter.
                 */
                if (args.length == 2) {
                    channel.sendMessage(String.format("Use `%s` to create a meeting!", patterns[0])).queue();
                    return;
                }

                String[] createArgs = args[2].split(" ", 6);

                /**
                 * Check if all additional parameter exists: [@Participant] [starttime] [endtime] [message].
                 */
                if (createArgs.length != 6) {
                    channel.sendMessage(String.format("Please add the values like this:\n`%s`", patterns[0])).queue();
                    return;
                }

                if (!createArgs[0].matches("<@!\\d{18}>")) {
                    channel.sendMessage("User is not valid. Please use ´@UserName´!").queue();
                    return;
                }
                String participantID = createArgs[0].substring(3, 21);

                /**
                 * Create time with the additional parameter.
                 */
                String starttime = createArgs[1] + " " + createArgs[2];
                String endtime = createArgs[3] + " " + createArgs[4];

                try {
                    Date dateStart = format.parse(starttime);

                    /**
                     * Saved date in Epoch.
                     */
                    long epochStart = dateStart.getTime() / 1000;

                    Date dateEnd = format.parse(endtime);
                    long epochEnd = dateEnd.getTime() / 1000;

                    /**
                     * Add meeting.
                     */
                    if (!meetingMng.insert(user.getId(), participantID, epochStart, epochEnd, createArgs[5])) {
                        channel.sendMessage("Could not create the Meeting.").queue();
                        return;
                    }
                } catch (ParseException e) {
                    channel.sendMessage("Date is not valid according to `"
                            + format.toPattern().toUpperCase()
                            + "` pattern.").queue();
                    return;
                }

                channel.sendMessage("Successfully added the Meeting.").queue();
                break;
            case "delete":

                /**
                 * Check if the command [!meeting delete] is written without other parameter.
                 */
                if (args.length == 2) {
                    channel.sendMessage(String.format("Use `%s` to delete a meeting!", patterns[1])).queue();
                    return;
                }

                try {
                    /**
                     * Parse meetingID to int.
                     */
                    int meetingID = Integer.parseInt(args[2]);

                    /**
                     * Delete meeting
                     */
                    if (!meetingMng.delete(meetingID)) {
                        channel.sendMessage("Could not delete the Meeting.").queue();
                        return;
                    }

                    channel.sendMessage("Successfully deleted the Meeting.").queue();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                break;
            case "update":

                /**
                 * Check if the command [!meeting update] is written without other parameter.
                 */
                if (args.length == 2) {
                    channel.sendMessage(String.format("Use `%s` to update a meeting!", patterns[2])).queue();
                    return;
                }

                String[] updateArgs = args[2].split(" ", 3);

                /**
                 * Check if all additional parameter exists: [meetingID] [value to change] [new value].
                 */
                if (updateArgs.length != 3) {
                    channel.sendMessage(String.format("Please add the values like this:\n`%s`", patterns[2])).queue();
                    return;
                }

                /**
                 * Var for new Value to transfer.
                 */
                Object newValue;

                updateArgs[1] = updateArgs[1].toLowerCase();

                try {
                    /**
                     * Parse meetingID to int.
                     */
                    int meetingID = Integer.parseInt(updateArgs[0]);

                    /**
                     * Check if value is correct format.
                     */
                    if (!updateArgs[1].matches("starttime|endtime|message")) {
                        channel.sendMessage(String.format("Unknown value to update: `%s` does not exist.", updateArgs[1])).queue();
                        return;
                    }

                    /**
                     * If value is a date, check format and if date is correct.
                     */
                    if (updateArgs[1].contains("time")) {
                        try {
                            Date time = format.parse(updateArgs[2]);
                            /**
                             * Epoch (without Millisec) are saved in variable to transfer.
                             */
                            newValue = time.getTime() / 1000;
                        } catch (ParseException e) {
                            channel.sendMessage("Date is not valid according to `"
                                    + format.toPattern().toUpperCase()
                                    + "` pattern.").queue();
                            return;
                        }
                    /**
                    * If time should not change.
                    */
                    } else {
                        newValue = updateArgs[2];
                    }

                    /**
                     * Update meeting.
                     */
                    if (!meetingMng.update(meetingID, updateArgs[1], newValue)) {
                        channel.sendMessage("Could not update the Meeting.").queue();
                        return;
                    }

                    channel.sendMessage("Successfully updated the Meeting.").queue();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                break;
            default:
                channel.sendMessage(String.format("Unknown command: `%s` does not exist.", args[1])).queue();
                break;
        }
    }
}
