package com.discord.bot.data;

public class UserData {

    private String userID;

    private String serverNickname;

    private String address;

    private String[] interests;

    private String[] competencies;

    private String gCalendarLink;

    public UserData(String userID, String nickname) {

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

    public void setgCalendarLink(String gCalendarLink) {
        this.gCalendarLink = gCalendarLink;
    }

    public String getgCalendarLink() {
        return gCalendarLink;
    }
}
