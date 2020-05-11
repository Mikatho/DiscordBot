package com.discord.bot.commands;

<<<<<<< HEAD
=======
import net.dv8tion.jda.api.entities.Member;
>>>>>>> fa80302b304130282fe7f33c3855d11f3576c846
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class HelpCommand implements CommandInterface {

    @Override
<<<<<<< HEAD
    public void executeCommand(MessageChannel channel, Message msg) {
=======
    public void executeCommand(Member m, MessageChannel channel, Message msg) {
>>>>>>> fa80302b304130282fe7f33c3855d11f3576c846

        channel.sendMessage("Whatever. ^_^").queue();

        //m.getUser().openPrivateChannel().complete().sendMessage("Hallo v2.").queue();
    }
}
