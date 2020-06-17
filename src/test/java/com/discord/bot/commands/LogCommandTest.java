package com.discord.bot.commands;

import com.discord.bot.UserManagement;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({UserManagement.class, LogCommand.class})
@PowerMockIgnore("javax.management.*")
public class LogCommandTest {

    @Mock MessageChannel vMessageChannel;
    @Mock Message vMessage;
    @Mock User vUser;
    @Mock RestAction<PrivateChannel> vPrivateChannel;
    @Mock PrivateChannel vPrivateChannel1;
    @Mock MessageAction vMessageAction;
    @Mock RestAction vRestActionVoid;
    @Mock UserManagement vUserManagement;


    @Before
    public void setup() {
        PowerMockito.mockStatic(UserManagement.class);
        PowerMockito.when(UserManagement.getINSTANCE()).thenReturn(vUserManagement);

        when(vMessage.getAuthor()).thenReturn(vUser);
        when(vUser.openPrivateChannel()).thenReturn(vPrivateChannel);
        when(vPrivateChannel.complete()).thenReturn(vPrivateChannel1);

        //Matcher als Parameter übergeben
        when(vPrivateChannel1.sendMessage(anyString())).thenReturn(vMessageAction);
        when(vMessage.addReaction(anyString())).thenReturn(vRestActionVoid);
        doNothing().when(vMessageAction).queue();
    }

    @Test
    public void UserIsNotRegistered() {
        when(vUserManagement.userIsRegistered(anyString())).thenReturn(false);

        LogCommand logCommand = new LogCommand();

        logCommand.executeCommand(vMessageChannel, vMessage);

        System.out.println(UserManagement.getINSTANCE());
        System.out.println(UserManagement.getINSTANCE().userIsRegistered(anyString()));



        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        verify(vPrivateChannel1).sendMessage(argument.capture());
        assertThat(argument.getValue()).contains("Please use `!register` first to execute this command.");
    }

}
