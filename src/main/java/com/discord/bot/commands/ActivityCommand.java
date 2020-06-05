package com.discord.bot.commands;

import com.discord.bot.UserManagement;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ActivityCommand implements CommandInterface {

    /*
    !activity running
    !activity start
    !activity stop
     */

    @Override
    public void executeCommand(MessageChannel channel, Message msg) {

        String[] patterns = {
                "!activity running",
                "!activity start",
                "!activity stop"};

        //Prüft, ob nur der Command an sich geschrieben wurde
        if (!msg.getContentRaw().contains(" ")) {
            channel.sendMessage("Use one of the following patterns:\n"
                    + "```" + patterns[0] + "\n" + patterns[1] + "\n" + patterns[2] + "```").queue();
            return;
        }

        String[] args = msg.getContentRaw().split(" ");

        //Holt sich Uhrzeit des Commandaufrufs und wandelt sie in richtiges Pattern um
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        LocalDateTime localTime = LocalDateTime.now();

        switch (args[1].toLowerCase()) {
            case "running":
                //Check if activity is running
                break;
            case "start":
                UserManagement.getINSTANCE().startActivity(formatter.format(localTime));
                break;
            case "stop":
                UserManagement.getINSTANCE().stopActivity(formatter.format(localTime));
                break;
            default:
                channel.sendMessage(String.format("Unknown command: `%s` does not exist.", args[1])).queue();
                break;
        }
    }
}
