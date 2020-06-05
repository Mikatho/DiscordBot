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

        UserManagement userManager = UserManagement.getINSTANCE();

        //Versucht den User zu registrieren
        if (!userManager.register(user.getId())) {
            channel.sendMessage(user.getAsMention() + " could not be added to the Database!").queue();
            return;
        }

        channel.sendMessage("Welcome kindly sir.\nCreating your Google-Calendar...\n").queue();

        String calendarLink = userManager.googleCalendarLink(user.getId(), user.getName());

        if (calendarLink == null) {
            channel.sendMessage("Unfortunately we could not create a Google Calendar for you.").queue();
        } else {
            userManager.update(user.getId(), "gCalendarLink", calendarLink);
            channel.sendMessage("Here is your Link to your Google-Calendar:\n" + calendarLink).queue();
        }
    }
}
