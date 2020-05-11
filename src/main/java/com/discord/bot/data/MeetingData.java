package com.discord.bot.data;

public class MeetingData {

    private int meetingID;

    private String userID;

    private String startTime;

    private String endTime;

    private String message;

    public MeetingData(int meetingID, String userID, String startTime, String endTime) {

        this.meetingID = meetingID;
        this.userID = userID;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public MeetingData(int meetingID, String userID, String startTime, String endTime, String message) {

        this.meetingID = meetingID;
        this.userID = userID;
        this.startTime = startTime;
        this.endTime = endTime;
        this.message = message;
    }

    public int getMeetingID() {
        return meetingID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserID() {
        return userID;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
