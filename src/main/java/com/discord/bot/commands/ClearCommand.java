package com.discord.bot.commands;

import com.discord.bot.DatabaseManagement;
import com.discord.bot.LoggingManagement;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;


/**
 * The <code>ClearCommand</code> Class implements the <code>CommandInterface</code>
 * to get the variables in the @Override <code>#executeCommand(MessageChannel channel, Message msg)</code>
 * method. It controls syntax and interprets the commands to call the right methods in
 * <code>DatabaseManagement</code> and <code>LoggingManagement</code>.
 *
 * @author L2G4
 * @version %I%, %G%
 * @see com.discord.bot.DatabaseManagement
 * @see DatabaseManagement#getINSTANCE()
 * @see com.discord.bot.LoggingManagement
 * @see LoggingManagement#getINSTANCE()
 * @see com.discord.bot.commands.CommandInterface
 * @see com.discord.bot.commands.CommandInterface#executeCommand(MessageChannel, Message)
 * @since 1.0
 */
public class ClearCommand implements CommandInterface {


    final static Logger logger = LogManager.getLogger(ClearCommand.class.getName());

    /**
     * This method is called whenever the <code>CommandManager#execute(String, MessageChannel, Message)</code>
     * method is executed, because a Discord input with [!clear] command was made.
     *
     * @param channel Discord channel.
     * @param msg     the Discord input.
     */
    @Override
    public void executeCommand(MessageChannel channel, Message msg) {

        /**
         * Delete entries in database and log-file.
         */
        try {
            DatabaseManagement.getINSTANCE().clear();
        } catch (SQLException e) {
            logger.fatal("Unable to get instance of database.\n" + e);
        }
        LoggingManagement.getINSTANCE().clear();
    }
}
