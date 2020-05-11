package com.discord.bot.listeners;

import com.discord.bot.CommandManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class PMListener extends ListenerAdapter {

    @Override
    public void onPrivateMessageReceived(@Nonnull PrivateMessageReceivedEvent event) {

        String message = event.getMessage().getContentRaw();
        TextChannel channel = (TextChannel) event.getChannel();
        Member member = (Member) event.getAuthor();

        if(message.startsWith("!")) {
            String[] args = message.split(" ");

            if(!CommandManager.getInstance().execute(args[0], member, channel, event.getMessage())) {
                channel.sendMessage("Unknown Command.").queue();
            }
        }
    }
}
