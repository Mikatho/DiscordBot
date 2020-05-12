package com.discord.bot.commands;

import com.discord.bot.UserManagement;
import com.discord.bot.data.UserData;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.Arrays;

public class DataCommand implements CommandInterface {

    @Override
    public void executeCommand(MessageChannel channel, Message msg) {

        /*
        //Speichert den User, der den Command eingegeben hat
        UserData user = UserManagement.getINSTANCE().getUser().get(msg.getAuthor().getId());

        //Ruft Daten des Users aus seiner Instanz auf
        String data = "Nickname: " + user.getServerNickname()
                + "\nAddress: " + user.getAddress()
                + "\nInterests: " + Arrays.toString(user.getInterests())
                + "\nCompetencies: " + Arrays.toString(user.getCompetencies());
        channel.sendMessage(data).queue();
    */
    }
}
