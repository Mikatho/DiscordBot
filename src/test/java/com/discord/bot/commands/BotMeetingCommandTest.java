package com.discord.bot.commands;

import com.discord.bot.BotMain;
import com.discord.bot.commands.BotMeetingCommand;
import jdk.nashorn.internal.scripts.JD;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.internal.entities.UserImpl;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;


public class BotMeetingCommandTest {

    static BotMeetingCommand botMeetingCommand = new BotMeetingCommand();


    static MessageChannel vMessageChannel = mock(MessageChannel.class);
    static Message vMessage = mock(Message.class);
    static User vUser = mock(UserImpl.class);
    static RestAction<PrivateChannel> vPrivateChannel = mock(RestAction.class);
    static PrivateChannel vPrivateChannel1 = mock(PrivateChannel.class);
    static MessageAction vMessageAction = mock(MessageAction.class);
    static RestAction vRestActionVoid = mock(RestAction.class);
    //Matcher als Parameter übergeben


    static {
        when(vMessage.getAuthor()).thenReturn(vUser);
        when(vUser.openPrivateChannel()).thenReturn(vPrivateChannel);
        when(vPrivateChannel.complete()).thenReturn(vPrivateChannel1);
        when(vPrivateChannel1.sendMessage(anyString())).thenReturn(vMessageAction);
        doNothing().when(vMessageAction).queue();
        when(vMessage.addReaction(anyString())).thenReturn(vRestActionVoid);
    }

    /**
     * Because we are testing a Single Method that doesn't return anything,
     * we have to rely on code coverage (Line Coverage) when writing
     * the tests for BotMeetingCommandTest
     *
     * TODO CANT TEST BC OF ACCES TO BOTMAIN.GETJDA().GETGUILD IN LINE 45
     */
    @Ignore
    public void noValidUserCheck() {

        String command = "!meeting create @G4 - Christian 18-06-2020 10 18-06-2020 18:00 30 Meeting with Benz";

        when(vPrivateChannel1.sendMessage(command)).thenReturn(vMessageAction);

        JDA vJDA = mock(JDA.class);
        Guild vGuild = mock(Guild.class);

        when(BotMain.getJda()).thenReturn(vJDA);
        when(vJDA.getGuildById(anyString())).thenReturn(vGuild);



        botMeetingCommand.executeCommand(vMessageChannel, vMessage);

        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        verify(vPrivateChannel1).sendMessage(argument.capture());
        assertThat(argument.getValue()).contains("Unable to parse data.");

    }

}
