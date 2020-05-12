package com.discord.bot.commands;

import com.discord.bot.UserManagement;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class DeleteCommand implements CommandInterface {

    //!delete [userID]

    @Override
    public void executeCommand(MessageChannel channel, Message msg) {

        //Prüft, ob nur der Command an sich geschrieben wurde
        if (!msg.getContentRaw().contains(" ")) {
            channel.sendMessage("Use `!delete [userID]` to delete!").queue();
            return;
        }

        String[] args = msg.getContentRaw().split(" ");

        //Prüft, ob der User existiert
        if (!UserManagement.getINSTANCE().getUser().containsKey(args[1])) {
            channel.sendMessage("This User does not exist.").queue();
            return;
        }

        //Versucht den User zu löschen
        if (!UserManagement.getINSTANCE().delete(args[1])) {
            channel.sendMessage("Could not delete the User.").queue();
            return;
        }

        channel.sendMessage("Successfully deleted the User.").queue();
    }
}
