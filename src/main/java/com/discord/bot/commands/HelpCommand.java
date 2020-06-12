package com.discord.bot.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.MessageAction;


/**
 * Contain all commands and the function to send them to a users private channel.
 *
 * @author      L2G4
 * @version     %I%, %G%
 * @see         com.discord.bot.CommandManager
 * @see         com.discord.bot.commands.CommandInterface
 * @see         com.discord.bot.commands.CommandInterface#executeCommand(MessageChannel, Message)
 * @since       1.0
 */
public class HelpCommand implements CommandInterface {

    /**
     * String arrays with all command categories.
     */
    String[] activityPatterns = {"!activity running", "!activity start", "!activity stop"};
    String[] deletePatterns = {"!delete [userID]"};
    String[] logPatterns = {"!log show", "!log save"};
    String[] meetingPatterns = {"!meeting create [starttime] [endtime] [message]",
            "!meeting delete [meetingID]",
            "!meeting update [meetingID] [value to change] [new value]"};
    String[] userPatterns = {"!user data", "!user update address [new value]",
            "!user update interests [new value1, new value2, etc.]",
            "!user update competencies [new value1, new value2, etc.]",
            "!user search [userId]"};

    /**
     * This method is called whenever the <code>CommandManager#execute(String, MessageChannel, Message)</code>
     * method is executed, because the Discord input [!help] was made. It returns all command patterns to the
     * users private discord channel.
     *
     * @param channel   Discord channel
     * @param msg       the Discord inputs.
     */
    @Override
    public void executeCommand(MessageChannel channel, Message msg) {

        /**
         * Send a private message with all commands.
         */
        User u = msg.getAuthor();
        RestAction<PrivateChannel> pC = u.openPrivateChannel();
        PrivateChannel p = pC.complete();
        MessageAction a = p.sendMessage("Commands: "
                + "\nprivate chat:\n"
                + "\nactivity commands: " + "```" + activityPatterns[0] + "\n" + activityPatterns[1] + "\n" + activityPatterns[2] + "```"
                + "\nuser commands: " + "```" + userPatterns[0] + "\n" + userPatterns[1] + "\n" + userPatterns[2] + "\n"
                + userPatterns[3] + "\n" + userPatterns[4] + "```"
                + "\npublic chat:\n"
                + "\ndelete commands: " + "```" + deletePatterns[0] + "```"
                + "\nlog commands: " + "```" + logPatterns[0] + "\n" + logPatterns[1] + "```"
                + "\nmeeting commands: " + "```" + meetingPatterns[0] + "\n" + meetingPatterns[1] + "\n" + meetingPatterns[2] + "```"
                );
        a.queue();
    }
}
