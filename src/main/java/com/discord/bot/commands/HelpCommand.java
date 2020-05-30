package com.discord.bot.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class HelpCommand implements CommandInterface {

    @Override
    public void executeCommand(MessageChannel channel, Message msg) {

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

        msg.getAuthor().openPrivateChannel().complete().sendMessage("Commands: "
                + "\nprivate chat:\n"
                + "\nactivity commands: " + "```" + activityPatterns[0] + "\n" + activityPatterns[1] + "\n" + activityPatterns[2] + "```"
                + "\nuser commands: " + "```" + userPatterns[0] + "\n" + userPatterns[1] + "\n" + userPatterns[2] + "\n"
                + userPatterns[3] + "\n" + userPatterns[4] + "```"
                + "\npublic chat:\n"
                + "\ndelete commands: " + "```" + deletePatterns[0] + "```"
                + "\nlog commands: " + "```" + logPatterns[0] + "\n" + logPatterns[1] + "```"
                + "\nmeeting commands: " + "```" + meetingPatterns[0] + "\n" + meetingPatterns[1] + "\n" + meetingPatterns[2] + "```"
                ).queue();
    }
}
