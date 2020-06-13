package com.discord.bot.commands;

import com.discord.bot.MeetingManagement;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * The <code>WaiterCommand</code> Class implements the <code>CommandInterface</code>
 * to get the variables in the @Override <code>#executeCommand(MessageChannel channel, Message msg)</code>
 * method. It handels the timecaps between messages to keep the threads synchronized. The variable [flag]
 * handels the action by calling her and breaking the method if needed.
 *
 * @author      L2G4
 * @version     %I%, %G%
 * @see         com.discord.bot.commands.CommandInterface
 * @see         com.discord.bot.commands.CommandInterface#executeCommand(MessageChannel, Message)
 * @since       1.0
 */
public class WaiterCommand implements CommandInterface {

    private static boolean flag = false;

    /*
    !wait
    */
    /**
     * This method is called whenever the <code>CommandManager#execute(String, MessageChannel, Message)</code>
     * method is executed, because the Discord input [!wait] was made.
     * 
     * @param   channel   Discord channel.
     * @param   msg       the Discord input.
     */
    @Override
    public void executeCommand(MessageChannel channel, Message msg) {

        /**
         * Notifies user by channel message.
         */
        channel.sendMessage("!notify").queue();

        /**
         * Manages the threadactivities in the [MeetingCommand].
         */
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

