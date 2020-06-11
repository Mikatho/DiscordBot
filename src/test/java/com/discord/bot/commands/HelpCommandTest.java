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

    /**
     * kann nur code coverage testen da die methoden nix zurückgeben....
     */
    @Test
    public void helpCommandCheck() {
        MessageChannel vMessageChannel = mock(MessageChannel.class);
        Message vMessage = mock(Message.class);
        

        HelpCommand tHelpCommand = new HelpCommand();
        tHelpCommand.executeCommand(vMessageChannel, vMessage);
    }


}
