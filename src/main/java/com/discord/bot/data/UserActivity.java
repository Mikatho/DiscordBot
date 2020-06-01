package com.discord.bot.data;

public class UserActivity {

    private int activityID;

    private long starttime;
    private long endtime;

    public UserActivity(long starttime) {

        this.starttime = starttime;
    }

    public int getActivityID() {
        return activityID;
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
}
