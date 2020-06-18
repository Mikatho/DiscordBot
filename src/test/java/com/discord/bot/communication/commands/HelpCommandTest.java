package com.discord.bot.communication.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.internal.entities.UserImpl;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

public class HelpCommandTest {

    /**
     * kann nur code coverage testen da die methoden nix zurückgeben....
     */
    @Test
    public void helpCommandCheck() {



        MessageChannel vMessageChannel = mock(MessageChannel.class);
        Message vMessage = mock(Message.class);

        User vUser = mock(UserImpl.class);
        when(vMessage.getAuthor()).thenReturn(vUser);

        RestAction<PrivateChannel> vPrivateChannel = mock(RestAction.class);
        when(vUser.openPrivateChannel()).thenReturn(vPrivateChannel);

        PrivateChannel vPrivateChannel1 = mock(PrivateChannel.class);
        when(vPrivateChannel.complete()).thenReturn(vPrivateChannel1);

        MessageAction vMessageAction = mock(MessageAction.class);
        //Matcher als Parameter übergeben
        when(vPrivateChannel1.sendMessage(anyString())).thenReturn(vMessageAction);

        doNothing().when(vMessageAction).queue();

        HelpCommand helpCommand = new HelpCommand();
        helpCommand.executeCommand(vMessageChannel, vMessage);

        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        verify(vPrivateChannel1).sendMessage(argument.capture());
        assertThat(argument.getValue()).contains(helpCommand.logPatterns)
                .contains(helpCommand.meetingPatterns)
                .contains(helpCommand.userPatterns);
    }

}
