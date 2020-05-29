package com.discord.bot.data;

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

    public String getUserID() {
        return hostID;
    }

    public void setParticipantID(String participantID) {
        this.participantID = participantID;
    }

    public String getParticipantID() {
        return participantID;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
