package com.discord.bot.commands;

import com.discord.bot.DatabaseManagement;
<<<<<<< HEAD
import com.discord.bot.LoggingManagement;
=======
import net.dv8tion.jda.api.entities.Member;
>>>>>>> fa80302b304130282fe7f33c3855d11f3576c846
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class ClearCommand implements CommandInterface {

    @Override
<<<<<<< HEAD
    public void executeCommand(MessageChannel channel, Message msg) {

        //Löscht alle Daten der Datenbank und in der Logging-File
        DatabaseManagement.getINSTANCE().clear();
        LoggingManagement.getINSTANCE().clear();
=======
    public void executeCommand(Member m, MessageChannel channel, Message msg) {

        DatabaseManagement.getINSTANCE().clear();
>>>>>>> fa80302b304130282fe7f33c3855d11f3576c846
    }
}
