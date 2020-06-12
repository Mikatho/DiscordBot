package com.discord.bot.commands;

import com.discord.bot.MeetingManagement;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class WaiterCommand implements CommandInterface {

    private static boolean flag = false;

    @Override
    public void executeCommand(MessageChannel channel, Message msg) {

        channel.sendMessage("!notify").queue();

        synchronized (this) {

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (flag) {
                System.out.println("worked");
            } else {
                System.out.println("Couldnt find user.");
            }

            flag = false;

            System.out.println(flag);
        }
    }

    public static void setFlag(boolean flag) {
        WaiterCommand.flag = flag;
    }

    public static boolean isFlag() {
        return flag;
    }
}

