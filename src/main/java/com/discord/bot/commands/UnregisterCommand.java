package com.discord.bot.commands;

import com.discord.bot.UserManagement;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class UnregisterCommand implements CommandInterface {

    //!unregister

    @Override
    public void executeCommand(MessageChannel channel, Message msg) {

        //Versucht den User zu löschen
        if (!UserManagement.getINSTANCE().delete(msg.getAuthor().getId())) {
            channel.sendMessage("Unfortunately we could not unregister you.").queue();
            return;
        }

        channel.sendMessage("The unregister was successfull!").queue();
    }
}
