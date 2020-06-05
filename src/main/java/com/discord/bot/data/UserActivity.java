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

    private long starttime;
    private long endtime;

    public UserActivity(long starttime) {

        this.starttime = starttime;
    }

    public int getActivityID() {
        return activityID;
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
}
