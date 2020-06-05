package com.discord.bot;

import com.discord.bot.data.MeetingData;

import java.util.concurrent.ConcurrentHashMap;


/**
 * This Class is the adapter of the <code>DatabaseManaging</code> and <code>MeetingCommand</code>.
 * It handles the interactions between them.
 *
 * @author      L2G4
 * @version     %I%, %G%
 * @see         com.discord.bot.MeetingManagement
 * @see         com.discord.bot.DatabaseManagement
 * @see         com.discord.bot.data.MeetingData
 * @since       1.0
 */
public class MeetingManagement {

    private static final MeetingManagement INSTANCE = new MeetingManagement();

    private DatabaseManagement dbManager = DatabaseManagement.getINSTANCE();

    /**
     * The getINSTANCE() method return the instance of the UserManagement object.
     *
     * @return  INSTANCE    instance of the UserManagement object
     */
    public static MeetingManagement getINSTANCE() {
        return INSTANCE;
    }

    /**
     * The <code>#insert()</code> method creates a new instance of <code>MeetingData</code>
     * and insert it in the database.
     *
     * @param hostID        userID[@user] of host.
     * @param participantID userID[@user] of participate.
     * @param startTime     meeting start.
     * @param endTime       meeting end.
     * @param message       meeting message.
     * @return  <code>true</code> Add meeting to the database;
     *                       <code>false</code> Could not add meeting to the database.
     */
    public boolean insert(String hostID, String participantID, long startTime, long endTime, String message) {

        /**
         * Create new <code>MeetingData</code> instance.
         */
        MeetingData tempMeeting = new MeetingData(hostID, participantID, startTime, endTime, message);

        /**
         * Insert meeting in database.
         */
        return dbManager.insert(tempMeeting);
    }

    /**
     * Delete meeting in database.
     * Call the <code>#deleteMeeting(int meetingID)</code> method in <code>DatabaseManagement</code>.
     * This method is called from the <code>MeetingCommand</code> class.
     *
     * @param meetingID     unique generated meeting ID.
     * @return  <code>true</code> Successfully deleted the meeting;
     *                  <code>false</code> meeting doesn´t exists.
     */
    public boolean delete(Integer meetingID) {

        return dbManager.deleteMeeting(meetingID);
    }

    /**
     * This method updates the manipulated MeetingData in the database.
     *
     * @param meetingID     unique generated meeting ID.
     * @param column        previous entry.
     * @param newValue      new entry for the database.db
     * @return  <code>true</code> Successfully update the MeetingData;
     *                  <code>false</code> could not be updated.
     */
    public boolean update(Integer meetingID, String column, Object newValue) {

        return dbManager.updateMeeting(meetingID, column, newValue);
    }
}
