package com.discord.bot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;

public class TestCommand implements CommandInterface {
    @Override
    public void executeCommand(MessageChannel channel, Message msg) {

        Guild guild = msg.getAuthor().getJDA().getGuildById(694185735628128338L);

        guild.getMemberById(245620283493449738L).getUser().openPrivateChannel().complete().sendMessage("Hi OwO").queue();

        //text.sendMessage("Worked.").queue();

        //channel.sendMessage(test).queue();
    }
}
