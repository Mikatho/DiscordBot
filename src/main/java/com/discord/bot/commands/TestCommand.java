package com.discord.bot.commands;

import com.discord.bot.BotMain;
import net.dv8tion.jda.api.entities.*;

/**
 * The <code>TestCommand</code> Class implements the <code>CommandInterface</code>
 * to get the variables in the @Override <code>#executeCommand(MessageChannel channel, Message msg)</code>
 * method. It outputs all the registered members in the database.
 *
 * @author L2G4
 * @version %I%, %G%
 * @see com.discord.bot.CommandManager
 * @see com.discord.bot.BotMain#getJda()
 * @since 1.0
 */
public class TestCommand implements CommandInterface {

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

        /**
         * Gets all registered members and safes them in [guild].
         */
        Guild guild = BotMain.getJda().getGuildById(123456789);

        /**
         * Prints out all registered members.
         */
        assert guild != null;
        System.out.println(guild.getMembers());


        //channel.sendMessage(test).queue();
    }
}
