package com.discord.bot.data;

import org.junit.Assert;
import org.junit.Test;


public class MeetingDataTest {

    MeetingData testMeeting = new MeetingData("hostID", "participantID", 123, 456);
    MeetingData testMeetingOverloaded = new MeetingData("hostID", "participantID", 12, 34, "message"); //Overloaded constructor

    @Test
    public void meetingSetterGetterTest() {

        Assert.assertEquals(testMeeting.getParticipantID(),"participantID");
        Assert.assertEquals(testMeeting.getStartTime(),123);
        Assert.assertEquals(testMeeting.getEndTime(),456);

        testMeeting.setParticipantID("test");
        testMeeting.setStartTime(456);
        testMeeting.setEndTime(789);

        Assert.assertEquals(testMeeting.getParticipantID(),"test");
        Assert.assertEquals(testMeeting.getStartTime(),456);
        Assert.assertEquals(testMeeting.getEndTime(),789);

        /* Overloaded constructor */

        Assert.assertEquals(testMeetingOverloaded.getParticipantID(),"participantID");
        Assert.assertEquals(testMeetingOverloaded.getStartTime(),12);
        Assert.assertEquals(testMeetingOverloaded.getEndTime(),34);
        Assert.assertEquals(testMeetingOverloaded.getMessage(), "message");

        testMeetingOverloaded.setParticipantID("newParticipantID");
        testMeetingOverloaded.setStartTime(34);
        testMeetingOverloaded.setEndTime(56);
        testMeetingOverloaded.setMessage("newMessage");

        Assert.assertEquals(testMeetingOverloaded.getParticipantID(),"newParticipantID");
        Assert.assertEquals(testMeetingOverloaded.getStartTime(),34);
        Assert.assertEquals(testMeetingOverloaded.getEndTime(),56);
        Assert.assertEquals(testMeetingOverloaded.getMessage(), "newMessage");
    }

}