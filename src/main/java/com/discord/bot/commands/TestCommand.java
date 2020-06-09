package com.discord.bot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;

public class TestCommand implements CommandInterface {
    @Override
    public void executeCommand(MessageChannel channel, Message msg) {

        Guild guild = msg.getAuthor().getJDA().getGuildById(685137660226043904L);

        guild.getMemberById(247092436021542912L).getUser().openPrivateChannel().complete().sendMessage("Hi OwO").queue();

        TextChannel text = msg.getAuthor().getJDA().getTextChannelById(694185736110604373L);

        //text.sendMessage("Worked.").queue();

        //channel.sendMessage(test).queue();
    }
}
