package com.discord.bot.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.concurrent.RejectedExecutionException;

import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

public class HelpCommandTest {

    @Test
    public void helpCommandCheck() {


        MessageChannel vMessageChannel = mock(MessageChannel.class);
        Message vMessage = mock(Message.class);

        User vUser = mock(User.class);
        when(vMessage.getAuthor()).thenReturn(vUser);

        RestAction<PrivateChannel> vPrivateChannel = mock(RestAction.class);
        when(vUser.openPrivateChannel()).thenReturn(vPrivateChannel);

        PrivateChannel vPrivateChannel1 = mock(PrivateChannel.class);
        when(vPrivateChannel.complete()).thenReturn(vPrivateChannel1);

        MessageAction vMessageAction = mock(MessageAction.class);
        when(vPrivateChannel1.sendMessage("test")).thenReturn(vMessageAction);

        doNothing().when(vMessageAction).queue();

        HelpCommand helpCommand = new HelpCommand();
        helpCommand.executeCommand(vMessageChannel, vMessage);

    }

}
