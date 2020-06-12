package com.discord.bot.commands;

import com.discord.bot.MeetingManagement;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class NotifyCommand implements CommandInterface {
    @Override
    public void executeCommand(MessageChannel channel, Message msg) {

        WaiterCommand command = new WaiterCommand();

        synchronized (new WaiterCommand()) {

            System.out.println(WaiterCommand.isFlag());

            WaiterCommand.setFlag(true);
        }
    }
}
