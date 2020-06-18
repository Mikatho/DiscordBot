package com.discord.bot.data;

import com.discord.bot.meeting.BotMeetingMessageData;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;


public class BotMeetingMessageDataTest {

    private static BotMeetingMessageData botMeetingMessageData1;
    private static BotMeetingMessageData botMeetingMessageData2;

    @BeforeClass
    public static void createObject()
    {
        /**
         * Create unequal objects
         */
        botMeetingMessageData1 = new BotMeetingMessageData("message", "123", "456",
                "foreignUserTag", 10, "startDateISO",20,
                "meetingMessage", true);
        botMeetingMessageData2 = new BotMeetingMessageData("message", "456", "789",
                "foreignUserTag2", 30,"startDateISO", 40,
                "meetingMessage", true);
    }

    @Test
    public void getValueTest() {

        Assert.assertEquals(botMeetingMessageData1.getMessage(), "message");
        Assert.assertEquals(botMeetingMessageData1.getHostID(),"123");
        Assert.assertEquals(botMeetingMessageData1.getParticipantID(),"456");
        Assert.assertEquals(botMeetingMessageData1.getForeignUserName(),"foreignUserTag");

        botMeetingMessageData1.setMessage("test");

        Assert.assertEquals(botMeetingMessageData1.getMessage(),"test");

    }

    /**
     * Check assertion
     */
    @Test
    public void equalsForDiffObjectsTest(){
        Assert.assertNotEquals(botMeetingMessageData1, botMeetingMessageData2);
    }
}