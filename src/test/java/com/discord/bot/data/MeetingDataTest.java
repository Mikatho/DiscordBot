package com.discord.bot.data;

import org.junit.Assert;
import org.junit.Test;


public class MeetingDataTest {

    MeetingData testMeeting = new MeetingData("hostID", "participantID", 123, 456);
    MeetingData testMeetingOverloaded = new MeetingData("hostID", "participantID", 12, 34, "message"); //Overloaded constructor

    @Test
    public void meetingSetterGetterTest() {

        Assert.assertEquals(testMeeting.getParticipantID(),"participantID");
        Assert.assertEquals(testMeeting.getStarttime(),123);
        Assert.assertEquals(testMeeting.getEndtime(),456);

        testMeeting.setParticipantID("test");
        testMeeting.setStarttime(456);
        testMeeting.setEndtime(789);

        Assert.assertEquals(testMeeting.getParticipantID(),"test");
        Assert.assertEquals(testMeeting.getStarttime(),456);
        Assert.assertEquals(testMeeting.getEndtime(),789);

        /* Overloaded constructor */

        Assert.assertEquals(testMeetingOverloaded.getParticipantID(),"participantID");
        Assert.assertEquals(testMeetingOverloaded.getStarttime(),12);
        Assert.assertEquals(testMeetingOverloaded.getEndtime(),34);
        Assert.assertEquals(testMeetingOverloaded.getMessage(), "message");

        testMeetingOverloaded.setParticipantID("newParticipantID");
        testMeetingOverloaded.setStarttime(34);
        testMeetingOverloaded.setEndtime(56);
        testMeetingOverloaded.setMessage("newMessage");

        Assert.assertEquals(testMeetingOverloaded.getParticipantID(),"newParticipantID");
        Assert.assertEquals(testMeetingOverloaded.getStarttime(),34);
        Assert.assertEquals(testMeetingOverloaded.getEndtime(),56);
        Assert.assertEquals(testMeetingOverloaded.getMessage(), "newMessage");
    }

    @Test
    public void equalsForDiffObjectsTest(){
        MeetingData testMeeting_1 = new MeetingData("hostID", "participantID", 123, 456);
        MeetingData testMeeting_2 = new MeetingData("hostID", "participantID", 456, 789);

        Assert.assertNotEquals(testMeeting_1, testMeeting_2);

        MeetingData testMeetingOverloaded_1 = new MeetingData("hostID", "participantID", 12, 34, "message1"); //Overloaded constructor
        MeetingData testMeetingOverloaded_2 = new MeetingData("hostID", "participantID", 34, 56, "message2"); //Overloaded constructor

        Assert.assertNotEquals(testMeetingOverloaded_1, testMeetingOverloaded_2);
    }
}