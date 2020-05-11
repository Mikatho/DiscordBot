package com.discord.bot.commands;

import com.discord.bot.UserManagement;
import com.discord.bot.data.UserData;
<<<<<<< HEAD
=======
import net.dv8tion.jda.api.entities.Member;
>>>>>>> fa80302b304130282fe7f33c3855d11f3576c846
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.Arrays;

public class DataCommand implements CommandInterface {

    @Override
<<<<<<< HEAD
    public void executeCommand(MessageChannel channel, Message msg) {

        //Speichert den User, der den Command eingegeben hat
        UserData user = UserManagement.getINSTANCE().getUser().get(msg.getAuthor().getId());

        //Ruft Daten des Users aus seiner Instanz auf
=======
    public void executeCommand(Member m, MessageChannel channel, Message msg) {

        UserData user = UserManagement.getINSTANCE().getUser().get(m.getId());

>>>>>>> fa80302b304130282fe7f33c3855d11f3576c846
        String data = "Nickname: " + user.getServerNickname()
                + "\nAddress: " + user.getAddress()
                + "\nInterests: " + Arrays.toString(user.getInterests())
                + "\nCompetencies: " + Arrays.toString(user.getCompetencies());
        channel.sendMessage(data).queue();
    }
}
