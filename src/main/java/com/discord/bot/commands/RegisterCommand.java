package com.discord.bot.commands;

import com.discord.bot.UserManagement;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class RegisterCommand implements CommandInterface {

    @Override
    public void executeCommand(MessageChannel channel, Message msg) {

        //Speichert sich User der Nachricht
        User user = msg.getAuthor();

        //Versucht den User zu registrieren
        if (!UserManagement.getINSTANCE().register(user.getId(), user.getName())) {
            channel.sendMessage(user.getAsMention() + " could not be added to the Database!").queue();
            return;
        }

        msg.getAuthor().openPrivateChannel().queue((userChannel) -> userChannel.sendMessage("Welcome kindly sir.").queue());
    }
}
