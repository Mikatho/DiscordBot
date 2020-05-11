package com.discord.bot.commands;

import com.discord.bot.UserManagement;
<<<<<<< HEAD
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
=======
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
>>>>>>> fa80302b304130282fe7f33c3855d11f3576c846

public class RegisterCommand implements CommandInterface {

    @Override
<<<<<<< HEAD
    public void executeCommand(MessageChannel channel, Message msg) {

        //Speichert sich User der Nachricht
        User user = msg.getAuthor();

        //Versucht den User zu registrieren
        if (!UserManagement.getINSTANCE().register(user.getId(), user.getName())) {
            channel.sendMessage(user.getAsMention() + " could not be added to the Database!").queue();
            return;
        }

        msg.getAuthor().openPrivateChannel().queue((userChannel) -> userChannel.sendMessage("Welcome kindly sir.").queue());
=======
    public void executeCommand(Member m, MessageChannel channel, Message msg) {

        if (!UserManagement.getINSTANCE().register(m.getId(), m.getEffectiveName())) {
            channel.sendMessage(m.getAsMention() + " could not be added to the Database!").queue();
            return;
        }

        m.getUser().openPrivateChannel().queue((userChannel) -> userChannel.sendMessage("Welcome kindly sir.").queue());
>>>>>>> fa80302b304130282fe7f33c3855d11f3576c846
    }
}
