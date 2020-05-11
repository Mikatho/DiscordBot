package com.discord.bot.commands;

import com.discord.bot.LoggingManagement;
<<<<<<< HEAD
=======
import net.dv8tion.jda.api.entities.Member;
>>>>>>> fa80302b304130282fe7f33c3855d11f3576c846
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class LogCommand implements CommandInterface {

    //!log (save)

    @Override
<<<<<<< HEAD
    public void executeCommand(MessageChannel channel, Message msg) {

        //Prüft, ob nur der Command an sich geschrieben wurde
        if (!msg.getContentRaw().contains(" ")) {
            channel.sendMessage(LoggingManagement.getINSTANCE().logToConsole() + "\nUse `!log save` to save it to a logfile.").queue();
            return;
        }

        String[] args = msg.getContentRaw().split(" ");

        //Prüft ersten Zusatz-Parameter des Commandsaufrufs
=======
    public void executeCommand(Member m, MessageChannel channel, Message msg) {

        String[] args = msg.getContentRaw().split(" ");

        if(args.length == 1){
            LoggingManagement.getINSTANCE().logToConsole();
            channel.sendMessage("Use '!log save' to save it to a logfile.").queue();
        }

>>>>>>> fa80302b304130282fe7f33c3855d11f3576c846
        if (args[1].toLowerCase().equals("save")) {
            LoggingManagement.getINSTANCE().saveToFile();
            channel.sendMessage("Successfully saved the log.").queue();
        } else {
<<<<<<< HEAD
            channel.sendMessage(String.format("Unknown command: `%s` does not exist.", args[1])).queue();
=======
            channel.sendMessage(String.format("Unknown command: '%s' does not exist.", args[1])).queue();
>>>>>>> fa80302b304130282fe7f33c3855d11f3576c846
        }
    }
}
