package com.discord.bot.data;

public class MeetingData {

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

    public MeetingData(String hostID, String participantID, long starttime, long endtime, String message) {

        this.hostID = hostID;
        this.participantID = participantID;
        this.starttime = starttime;
        this.endtime = endtime;
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

    public void setStarttime(long starttime) {
        this.starttime = starttime;
    }

    public long getStarttime() {
        return starttime;
    }

    public void setEndtime(long endtime) {
        this.endtime = endtime;
    }

    public long getEndtime() {
        return endtime;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
