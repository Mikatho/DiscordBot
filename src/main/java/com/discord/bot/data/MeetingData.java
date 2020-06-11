package com.discord.bot.data;


/**
 * The <code>MeetingData</code> class contains all attributes for a meeting.
 * Every arranged meeting is saved as an instance of this object in the database.
 *
 * @author      L2G4
 * @version     %I%, %G%
 * @see         com.discord.bot.MeetingManagement
 * @since       1.0
 */
public class MeetingData {

    private int meetingID;

    private String hostID;

    private String participantID;

    private long starttime;

    private long endtime;

    private String message;

    public MeetingData(String hostID, String participantID, long starttime, long endtime) {

        this.hostID = hostID;
        this.participantID = participantID;
        this.starttime = starttime;
        this.endtime = endtime;
    }

    /**
     * Overloaded class constructor. Extra variable "message".
     */
    public MeetingData(String hostID, String participantID, long starttime, long endtime, String message) {
        this.hostID = hostID;
        this.participantID = participantID;
        this.starttime = starttime;
        this.endtime = endtime;
        this.message = message;
    }

    public void setMeetingID(int meetingID) {
        this.meetingID = meetingID;
    }

    public int getMeetingID() {
        return meetingID;
    }

    public void setUserID(String userID) {
        this.hostID = userID;
    }

    public final String getUserID() {
        return hostID;
    }

    public void setParticipantID(String participantID) {
        this.participantID = participantID;
    }

    public final String getParticipantID() {
        return participantID;
    }

    public void setStarttime(long starttime) {
        this.starttime = starttime;
    }

    public final long getStarttime() {
        return starttime;
    }

    public void setEndtime(long endtime) {
        this.endtime = endtime;
    }

    public final long getEndtime() {
        return endtime;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public final String getMessage() {
        return message;
    }
}
