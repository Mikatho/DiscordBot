package com.discord.bot.data;


/**
 * The <code>UserActivity</code> class is saved as an object in the database.
 * It contains the working start- and end-time.
 *
 * @author      L2G4
 * @version     %I%, %G%
 * @see         com.discord.bot.commands.ActivityCommand
 * @since       1.0
 */
public class UserActivity {

    private int activityID;

    private String starttime;
    private String endtime;

    public UserActivity(String starttime) {

        this.starttime = starttime;
    }

    public int getActivityID() {
        return activityID;
    }

    public final String getStarttime() {
        return starttime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public final String getEndtime() {
        return endtime;
    }
}
