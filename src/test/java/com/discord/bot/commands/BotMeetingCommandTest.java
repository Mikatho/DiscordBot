package com.discord.bot.commands;

import com.discord.bot.commands.BotMeetingCommand;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.junit.Ignore;
import org.junit.Test;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;


public class BotMeetingCommandTest {

    /**
     * Because we are testing a Single Method that doesn't return anything,
     * we have to rely on code coverage (Line Coverage) when writing
     * the tests for BotMeetingCommandTest
     */
    @Ignore
    public void simpleCheck() {
        try {
            MessageAction vMockMessageAction = mock(MessageAction.class);

            MessageChannel vMockMessageChannel = mock(MessageChannel.class);
            when(vMockMessageChannel.sendMessage("TestManage")).thenReturn(vMockMessageAction);

            String rawCommand = "!create meeting";
            Message vMessage = mock(Message.class);


            when(vMessage.getAuthor()).thenReturn(mock(User.class));
            when(vMessage.getAuthor().isBot()).thenReturn(false);
            when(vMessage.getContentRaw()).thenReturn(rawCommand);

            BotMeetingCommand botMeeCom = new BotMeetingCommand();

        /*
        Test if Commands get skipped if there is a bot that writes commands
         */
            botMeeCom.executeCommand(vMockMessageChannel, vMessage);

        /*
        Change the Bot status of the message to true so it won't get skipped
         */
            when(vMessage.getAuthor().isBot()).thenReturn(true);
            botMeeCom.executeCommand(vMockMessageChannel, vMessage);

            //TODO: fertigstellen wenn BotMeetingCommand fertig
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

}
