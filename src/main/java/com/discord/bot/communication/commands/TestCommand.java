package com.discord.bot.communication.commands;

import com.discord.bot.communication.CommandInterface;
import com.discord.bot.communication.CommandManager;
import net.dv8tion.jda.api.entities.*;

/**
 * The <code>TestCommand</code> Class implements the <code>CommandInterface</code>
 * to get the variables in the @Override <code>#executeCommand(MessageChannel channel, Message msg)</code>
 * method. It outputs all the registered members in the database.
 *
 * @author L2G4
 * @version %I%, %G%
 * @see CommandManager
 * @see com.discord.bot.BotMain#getJda()
 * @since 1.0
 */
public class TestCommand implements CommandInterface {

    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(TestCommand.class.getName());

    /*
    !test
    */

    /**
     * This method is called whenever the <code>CommandManager#execute(String, MessageChannel, Message)</code>
     * method is executed, because the Discord input [!test] was made.
     * The bot will reply in server chat.
     *
     * @param channel Discord channel.
     * @param msg     the Discord input.
     */
    @Override
    public void executeCommand(MessageChannel channel, Message msg) {


    }
}
