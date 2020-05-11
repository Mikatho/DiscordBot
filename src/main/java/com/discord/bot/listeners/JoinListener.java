package com.discord.bot.listeners;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class JoinListener extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {

        User user = event.getUser();
        user.openPrivateChannel().queue((channel) -> channel.sendMessage("Welcome kindly sir.").queue());
    }
}
