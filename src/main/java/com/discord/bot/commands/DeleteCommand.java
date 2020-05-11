package com.discord.bot.commands;

import com.discord.bot.UserManagement;
<<<<<<< HEAD
=======
import net.dv8tion.jda.api.entities.Member;
>>>>>>> fa80302b304130282fe7f33c3855d11f3576c846
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class DeleteCommand implements CommandInterface {

    //!delete [userID]

    @Override
<<<<<<< HEAD
    public void executeCommand(MessageChannel channel, Message msg) {

        //Prüft, ob nur der Command an sich geschrieben wurde
        if (!msg.getContentRaw().contains(" ")) {
            channel.sendMessage("Use `!delete [userID]` to delete!").queue();
            return;
        }

        String[] args = msg.getContentRaw().split(" ");

        //Prüft, ob der User existiert
=======
    public void executeCommand(Member m, MessageChannel channel, Message msg) {

        String[] args = msg.getContentRaw().split(" ");

        if (args.length == 1) {
            channel.sendMessage("Use the following pattern:\n!delete [userID]").queue();
            return;
        }

>>>>>>> fa80302b304130282fe7f33c3855d11f3576c846
        if (!UserManagement.getINSTANCE().getUser().containsKey(args[1])) {
            channel.sendMessage("This User does not exist.").queue();
            return;
        }

<<<<<<< HEAD
        //Versucht den User zu löschen
=======
>>>>>>> fa80302b304130282fe7f33c3855d11f3576c846
        if (!UserManagement.getINSTANCE().delete(args[1])) {
            channel.sendMessage("Could not delete the User.").queue();
            return;
        }

        channel.sendMessage("Successfully deleted the User.").queue();
    }
}
