package com.discord.bot.data;


/**
 * The <code>MeetingData</code> class contains all attributes for a meeting.
 * Every arranged meeting is saved as an instance of this object in the database.
 *
 * @author      L2G4
 * @version     %I%, %G%
 * @see         com.discord.bot.commands.MeetingCommand
 * @see         com.discord.bot.MeetingManagement
 * @since       1.0
 */
public class MeetingData {

    private String hostID;

    private String participantID;

    private long startTime;

    private long endTime;

    private String message;

    public MeetingData(String hostID, String participantID, long startTime, long endTime) {

        this.hostID = hostID;
        this.participantID = participantID;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * Overloaded class constructor. Extra variable "message".
     */
    public MeetingData(String hostID, String participantID, long startTime, long endTime, String message) {

        this.hostID = hostID;
        this.participantID = participantID;
        this.startTime = startTime;
        this.endTime = endTime;
        this.message = message;
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

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public final long getStartTime() {
        return startTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public final long getEndTime() {
        return endTime;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public final String getMessage() {
        return message;
    }
}
