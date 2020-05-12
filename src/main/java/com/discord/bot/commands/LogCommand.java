package com.discord.bot.commands;

import com.discord.bot.LoggingManagement;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class LogCommand implements CommandInterface {

    //!log (save)

    @Override
    public void executeCommand(MessageChannel channel, Message msg) {

        //Prüft, ob nur der Command an sich geschrieben wurde
        if (!msg.getContentRaw().contains(" ")) {
            channel.sendMessage(LoggingManagement.getINSTANCE().logToConsole() + "\nUse `!log save` to save it to a logfile.").queue();
            return;
        }

        String[] args = msg.getContentRaw().split(" ");

        //Prüft ersten Zusatz-Parameter des Commandsaufrufs
        if (args[1].toLowerCase().equals("save")) {
            LoggingManagement.getINSTANCE().saveToFile();
            channel.sendMessage("Successfully saved the log.").queue();
        } else {
            channel.sendMessage(String.format("Unknown command: `%s` does not exist.", args[1])).queue();
        }
    }
}
