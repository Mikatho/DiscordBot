package com.discord.bot.data;

public class UserActivity {

    private int activityID;

    private String starttime;
    private String endtime;

    public UserActivity(Integer activityID, String starttime) {

        this.activityID = activityID;
        this.starttime = starttime;
    }

    public int getActivityID() {
        return activityID;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public String getEndtime() {
        return endtime;
    }
}
