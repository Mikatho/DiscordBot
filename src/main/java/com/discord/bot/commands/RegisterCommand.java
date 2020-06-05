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

        String calendarID = userManager.googleCalendarID(user.getName());

        if (calendarID == null) {
            channel.sendMessage("Unfortunately we could not create a Google Calendar for you.").queue();
        } else {
            userManager.update(user.getId(), "gCalendarLink", calendarID);
            channel.sendMessage("Here is your Link to your Google-Calendar:\n" + userManager.googleCalendarLink(calendarID)).queue();
        }
    }
}
