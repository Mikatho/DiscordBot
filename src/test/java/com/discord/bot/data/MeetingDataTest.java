package com.discord.bot.data;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;


public class MeetingDataTest {


    private static MeetingData testMeeting;
    private static MeetingData testMeeting2;
    private static MeetingData testMeetingOverloaded;
    private static MeetingData testMeetingOverloaded2;

    @BeforeClass
    public static void createObject()
    {
        /**
         * Create unequal objects
         */
        testMeeting = new MeetingData("hostID", "participantID", 123, 456);
        testMeeting2 = new MeetingData("hostID", "participantID", 456, 789);

        testMeetingOverloaded = new MeetingData("hostID", "participantID", 12, 34, "message");  //Overloaded constructor
        testMeetingOverloaded2 = new MeetingData("hostID", "participantID", 34, 56, "message2"); //Overloaded constructor
    }

    @Test
    public void meetingSetterGetterTest() {

        Assert.assertEquals(testMeeting.getParticipantID(),"participantID");
        Assert.assertEquals(testMeeting.getStarttime(),123);
        Assert.assertEquals(testMeeting.getEndtime(),456);

        testMeeting.setParticipantID("test");
        testMeeting.setStarttime(456);
        testMeeting.setEndtime(789);
        testMeeting.setMeetingID(1);
        testMeeting.setHostID("22");

        Assert.assertEquals(testMeeting.getParticipantID(),"test");
        Assert.assertEquals(testMeeting.getStarttime(),456);
        Assert.assertEquals(testMeeting.getEndtime(),789);
        Assert.assertEquals(testMeeting.getMeetingID(),1);
        Assert.assertEquals(testMeeting.getHostID(),"22");

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

    /**
     * Check assertion
     */
    @Test
    public void equalsForDiffObjectsTest(){
        Assert.assertNotEquals(testMeeting, testMeeting2);
        Assert.assertNotEquals(testMeetingOverloaded, testMeetingOverloaded2);  // Overloaded
    }
}