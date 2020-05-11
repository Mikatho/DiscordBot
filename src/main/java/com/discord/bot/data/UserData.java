package com.discord.bot.data;

<<<<<<< HEAD
import java.util.ArrayList;

=======
>>>>>>> fa80302b304130282fe7f33c3855d11f3576c846
public class UserData {

    private String userID;

    private String serverNickname;

    private String address;

    private String[] interests;

    private String[] competencies;

<<<<<<< HEAD
    private ArrayList<MeetingData> meetings;

    private ArrayList<UserActivity> activities;
=======
    private MeetingData[] meetings;

    private UserActivity[] activities;
>>>>>>> fa80302b304130282fe7f33c3855d11f3576c846

    private String gCalendarLink;

    public UserData(String userID, String nickname) {

<<<<<<< HEAD
        meetings = new ArrayList<>();
        activities = new ArrayList<>();

=======
>>>>>>> fa80302b304130282fe7f33c3855d11f3576c846
        this.userID = userID;
        this.serverNickname = nickname;
    }

    public String getUserID() {
        return userID;
    }

    public void setServerNickname(String serverNickname) {
        this.serverNickname = serverNickname;
    }

    public String getServerNickname() {
        return serverNickname;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setInterests(String[] interests) {
        this.interests = interests;
    }

    public String[] getInterests() {
        return interests;
    }

    public void setCompetencies(String[] competencies) {
        this.competencies = competencies;
    }

    public String[] getCompetencies() {
        return competencies;
    }

<<<<<<< HEAD
    public ArrayList<MeetingData> getMeetings() {
        return meetings;
    }

    public ArrayList<UserActivity> getActivities() {
=======
    public void setMeetings(MeetingData[] meetings) {
        this.meetings = meetings;
    }

    public MeetingData[] getMeetings() {
        return meetings;
    }

    public void setActivities(UserActivity[] activities) {
        this.activities = activities;
    }

    public UserActivity[] getActivities() {
>>>>>>> fa80302b304130282fe7f33c3855d11f3576c846
        return activities;
    }

    public void setgCalendarLink(String gCalendarLink) {
        this.gCalendarLink = gCalendarLink;
    }

    public String getgCalendarLink() {
        return gCalendarLink;
    }
}
