package com.discord.bot.commands;

import com.discord.bot.UserManagement;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class UnregisterCommand implements CommandInterface {

    /*
    !unregister
    */
    /**
     * This method is called whenever the user types !unregister to unregister himself
     * from the database.
     * 
     * @param   channel   Discord channel.
     * @param   msg       the Discord input.
     */
    @Override
    public void executeCommand(MessageChannel channel, Message msg) {

        /**
         * Checks if the user is erasable. If not, replies with feedback message.
         */
        if (!UserManagement.getINSTANCE().delete(msg.getAuthor().getId())) {
            channel.sendMessage("Unfortunately we could not unregister you.").queue();
            return;
        }

        /**
         * If the unregister was successfull, replies with feedback message.
         */
        channel.sendMessage("The unregister was successfull!").queue();
    }
}
