package com.discord.bot.commands;

import com.discord.bot.DatabaseManagement;
import com.discord.bot.LoggingManagement;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class ClearCommand implements CommandInterface {

    @Override
    public void executeCommand(MessageChannel channel, Message msg) {

        //Löscht alle Daten der Datenbank und in der Logging-File
        DatabaseManagement.getINSTANCE().clear();
        LoggingManagement.getINSTANCE().clear();
    }
}
