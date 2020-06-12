package com.discord.bot.commands;

import com.discord.bot.BotMain;
import net.dv8tion.jda.api.entities.*;

public class TestCommand implements CommandInterface {
    @Override
    public void executeCommand(MessageChannel channel, Message msg) {

        Guild guild = BotMain.getJda().getGuildById(123456789);

        assert guild != null;
        System.out.println(guild.getMembers());

        //channel.sendMessage(test).queue();
    }
}
