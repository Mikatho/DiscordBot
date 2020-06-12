package com.discord.bot.commands;

import com.discord.bot.MeetingManagement;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

/**
 * The <code>NotifyCommand</code> Class implements the <code>CommandInterface</code>
 * to get the variables in the @Override <code>#executeCommand(MessageChannel channel, Message msg)</code>
 * method.
 *
 * @author      L2G4
 * @version     %I%, %G%
 * @see         com.discord.bot.DatabaseManagement
 * @see         UserManagement#getINSTANCE()
 * @see         com.discord.bot.UserManagement
 * @see         com.discord.bot.UserManagement#register(userID)
 * @see         LoggingManagement#getINSTANCE()
 * @see         com.discord.bot.commands.CommandInterface
 * @see         com.discord.bot.commands.CommandInterface#executeCommand(MessageChannel, Message)
 * @since       1.0
 */
public class NotifyCommand implements CommandInterface {

    /*
    !notify
    */
    /**
     * This method is called whenever the <code>CommandManager#execute(String, MessageChannel, Message)</code>
     * method is executed, because the Discord input [!notify] was made.
     * The class will interact with the <code>WaiterCommand</code>.
     * 
     * @param   channel   Discord channel.
     * @param   msg       the Discord input.
     */
    @Override
    public void executeCommand(MessageChannel channel, Message msg) {

        /**
         * Creates an instance of the [WaiterCommand].
         */
        WaiterCommand command = new WaiterCommand();

        /**
         * Synchronizes the [WaiterCommand] with the variable [flag].
         */
        synchronized (new WaiterCommand()) {

            System.out.println(WaiterCommand.isFlag());

            WaiterCommand.setFlag(true);
        }
    }
}
