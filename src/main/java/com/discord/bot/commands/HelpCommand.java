package com.discord.bot.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;


/**
 * Contain all commands and the function to send them to a users private channel.
 *
 * @author L2G4
 * @version %I%, %G%
 * @see com.discord.bot.CommandManager
 * @see com.discord.bot.commands.CommandInterface
 * @see com.discord.bot.commands.CommandInterface#executeCommand(MessageChannel, Message)
 * @since 1.0
 */
public class HelpCommand implements CommandInterface {

    /**
     * String arrays with all command categories.
     */
    String[] registerPatterns = {"!register", "!unregister"};
    String[] logPatterns = {"!log show", "!log save"};
    String[] meetingPatterns = {"!meeting create [@Participant] [starttime] [endtime] [duration in minutes] [optional message]",
            "!meeting delete [meetingID]",
            "!meeting update [meetingID] [value to change] [new value]",
            "!meeting search [meetingID]"};
    String[] userPatterns = {"!user data", "!user update address [new value]",
            "!user update interests [new value1, new value2, etc.]",
            "!user update competencies [new value1, new value2, etc.]",
            "!user search [userId]"};
    String[] calendarPatterns = {
            "!cal(endar) all",
            "!cal(endar) [length] hours",
            "!cal(endar) [length] days"};

    /**
     * This method is called whenever the <code>CommandManager#execute(String, MessageChannel, Message)</code>
     * method is executed, because the Discord input [!help] was made. It returns all command patterns to the
     * users private discord channel.
     *
     * @param channel Discord channel
     * @param msg     the Discord inputs.
     */
    @Override
    public void executeCommand(MessageChannel channel, Message msg) {

        // Sends a private message with all commands.
        msg.getAuthor().openPrivateChannel().complete().sendMessage(
                "\n**Private Chat:**"
                + "\n*Register Commands:*" + "```" + registerPatterns[0] + "\n" + registerPatterns[1] + "```"
                + "\n**All Chat-Types:**"
                + "\n*Meeting Commands:*" + "```" + meetingPatterns[0] + "\n" + meetingPatterns[1] + "\n" + meetingPatterns[2] + "\n\n"
                + meetingPatterns[3] + "\nNote: Dateformat DD-MM-YYYY HH:MM" + "```"
                + "\n*User Commands:*" + "```" + userPatterns[0] + "\n" + userPatterns[1] + "\n" + userPatterns[2] + "\n"
                + userPatterns[3] + "\n" + userPatterns[4] + "```"
                + "\n*Log Commands:*" + "```" + logPatterns[0] + "\n" + logPatterns[1] + "```"
                + "\n*Calendar Commands:*" + "```" + calendarPatterns[0] + "\n" + calendarPatterns[1] + "\n" + calendarPatterns[2] + "```"
        ).queue();
        msg.addReaction("U+1F4AC").queue();
    }
}
