package com.discord.bot.commands;

import com.discord.bot.UserManagement;
<<<<<<< HEAD
=======
import net.dv8tion.jda.api.entities.Member;
>>>>>>> fa80302b304130282fe7f33c3855d11f3576c846
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ActivityCommand implements CommandInterface {

    //!activity (start/stop)

    @Override
<<<<<<< HEAD
    public void executeCommand(MessageChannel channel, Message msg) {

        //Prüft, ob nur der Command an sich geschrieben wurde
        if (!msg.getContentRaw().contains(" ")) {
            channel.sendMessage("No activity running atm.").queue();
            return;
        }

        String[] args = msg.getContentRaw().split(" ");

        //Holt sich Uhrzeit des Commandaufrufs und wandelt sie in richtiges Pattern um
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        LocalDateTime localTime = LocalDateTime.now();

        //Prüft ersten Zusatz-Parameter des Commandaufrufs
=======
    public void executeCommand(Member m, MessageChannel channel, Message msg) {

        String[] args = msg.getContentRaw().split(" ");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        LocalDateTime localTime = LocalDateTime.now();

        if(args.length == 1){
            channel.sendMessage("No activity running atm.").queue();
        }

>>>>>>> fa80302b304130282fe7f33c3855d11f3576c846
        switch (args[1].toLowerCase()) {
            case "start":
                UserManagement.getINSTANCE().startActivity(formatter.format(localTime));
                break;
            case "stop":
                UserManagement.getINSTANCE().stopActivity(formatter.format(localTime));
                break;
            default:
<<<<<<< HEAD
                channel.sendMessage(String.format("Unknown command: `%s` does not exist.", args[1])).queue();
=======
                channel.sendMessage(String.format("Unknown command: '%s' does not exist.", args[1])).queue();
>>>>>>> fa80302b304130282fe7f33c3855d11f3576c846
                break;
        }
    }
}
