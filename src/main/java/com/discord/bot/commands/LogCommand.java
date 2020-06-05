package com.discord.bot.commands;

import com.discord.bot.LoggingManagement;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class LogCommand implements CommandInterface {

    /*
    !log show
    !log save
     */

    @Override
    public void executeCommand(MessageChannel channel, Message msg) {

        String[] patterns = {
                "!log show",
                "!log save"};

        //Prüft, ob nur der Command an sich geschrieben wurde
        if (!msg.getContentRaw().contains(" ")) {
            channel.sendMessage("Use one of the following patterns:\n"
                    + "```" + patterns[0] + "\n" + patterns[1] + "```").queue();
            return;
        }

        String[] args = msg.getContentRaw().split(" ");

        //Prüft ersten Zusatz-Parameter des Commandsaufrufs
        switch (args[1].toLowerCase()) {
            case "show":
                channel.sendMessage(LoggingManagement.getINSTANCE().logToConsole()).queue();
                break;
            case "save":
                LoggingManagement.getINSTANCE().saveToFile();
                channel.sendMessage("Successfully saved the log.").queue();
                break;
            default:
                channel.sendMessage(String.format("Unknown command: `%s` does not exist.", args[1])).queue();
        }
    }
}
