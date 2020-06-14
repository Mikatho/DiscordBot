package com.discord.bot;

import com.discord.bot.data.BotMeetingMessageData;
import com.discord.bot.data.MeetingData;
import net.dv8tion.jda.api.EmbedBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * This Class is the adapter of the <code>DatabaseManaging</code> and <code>MeetingCommand</code>.
 * It handles the interactions between them.
 *
 * @author L2G4
 * @version %I%, %G%
 * @see com.discord.bot.MeetingManagement
 * @see com.discord.bot.DatabaseManagement
 * @see com.discord.bot.data.MeetingData
 * @since 1.0
 */
public class MeetingManagement {

    private static final Logger LOGGER = LogManager.getLogger(MeetingManagement.class.getName());

    private static final MeetingManagement INSTANCE = new MeetingManagement();

    private DatabaseManagement dbManager = DatabaseManagement.getINSTANCE();

    private GoogleCalendarManagement calendarManager = GoogleCalendarManagement.getInstance();

    private ConcurrentHashMap<String, BotMeetingMessageData> messageDataHolder = new ConcurrentHashMap<>();

    /**
     * The <code>#getINSTANCE()</code> method return the instance of the <code>MeetingManagement</code> object.
     *
     * @return INSTANCE    instance of the MeetingManagement object
     */
    public static MeetingManagement getINSTANCE() {
        return INSTANCE;
    }

    /**
     * @return messageDataHolder
     */
    public ConcurrentMap<String, BotMeetingMessageData> getBotMessageHolder() {
        return messageDataHolder;
    }


    /**
     * The <code>#insert()</code> method creates a new instance of <code>MeetingData</code>
     * and insert it in the database.
     *
     * @param hostID        userID[@user] of host.
     * @param participantID userID[@user] of participate.
     * @param starttime     meeting start.
     * @param endtime       meeting end.
     * @param message       meeting message.
     * @return <code>true</code> Successfully insert the meeting;
     * <code>false</code> unable to insert the meeting.
     */
    public int insert(String hostID, String participantID, long starttime, long endtime, String message) {

        MeetingData tempMeeting;

        // Instantiate MeetingData.
        if (message.equals("N/a")) {
            tempMeeting = new MeetingData(hostID, participantID, starttime, endtime);
        } else {
            // Overloaded MeetingData constructor[message]
            tempMeeting = new MeetingData(hostID, participantID, starttime, endtime, message);
        }

        try {
            return dbManager.insertMeeting(tempMeeting);
        } catch (SQLException e) {
            LOGGER.fatal(String.format("Unable to insert a meeting[meeting_data].%n%s", e));
            return 0;
        }
    }

    /**
     * Delete meeting in database.
     * Call the <code>#deleteMeeting(int meetingID)</code> method in <code>DatabaseManagement</code>.
     * This method is called from the <code>MeetingCommand</code> class.
     *
     * @param meetingID unique generated meeting ID.
     * @return <code>true</code> Successfully deleted the meeting;
     * <code>false</code> meeting doesn´t exists.
     */
    public boolean delete(Integer meetingID) {

        try {
            return dbManager.deleteMeeting(meetingID);
        } catch (SQLException e) {
            LOGGER.fatal(String.format("Unable to delete meeting[meeting_data].%n%s", e));
            return false;
        }
    }

    /**
     * This method updates the manipulated MeetingData in the database.
     *
     * @param meetingID unique generated meeting ID.
     * @param column    previous entry.
     * @param newValue  new entry for the database.db
     * @return <code>true</code> Successfully update the MeetingData;
     * <code>false</code> could not be updated.
     */
    public boolean update(Integer meetingID, String hostID, String column, Object newValue) {

        // Update meeting.
        try {
            dbManager.updateMeeting(meetingID, column, newValue, hostID);
            return true;
        } catch (SQLException e) {
            LOGGER.fatal(String.format("Unable to update a meeting[meeting_data].%n%s", e));
            return false;
        }
    }

    public boolean userIsRegistered(String userID) {

        try {
            return DatabaseManagement.getINSTANCE().registeredCheck(userID);
        } catch (SQLException e) {
            LOGGER.fatal(String.format("Unable to register a user[user_data].%n%s", e));
            return false;
        }
    }

    public boolean authorizationCheck(int meetingID, String userID) {

        try {
            return dbManager.authorizationCheck(meetingID, userID);
        } catch (SQLException e) {
            LOGGER.fatal(String.format("Unable to check authorization[meeting_data].%n%s", e));
            return false;
        }
    }

    public long[] earliestPossibleMeeting(String userID, long starttime, long endtime, int duration) throws SQLException {

        return dbManager.findEarliestPossibleMeetingtimes(userID, starttime, endtime, duration);
    }

    public String googleCalendarEvent(String userID, String eventName, String eventLocation, String eventDescription, long starttime, long endtime) {

        try {
            String calendarID = (String) dbManager.returnDataUser(userID)[3];
            return calendarManager.createNewEvent(calendarID, eventName, eventLocation, eventDescription, starttime, endtime);
        } catch (SQLException | IOException e) {
            LOGGER.fatal(String.format("Unable to return a meeting[meeting_data].%n%s", e));
            return null;
        }
    }

    public Object[] search(int meetingID) {

        try {
            return dbManager.returnDataMeeting(meetingID);
        } catch (SQLException e) {
            LOGGER.fatal(String.format("Unable to search a user[user_data].%n%s", e));
            return new Object[0];
        }
    }

    public boolean deleteGoogleCalendarEvent(String userID, int meetingID) {

        try {
            String calendarID = (String) dbManager.returnDataUser(userID)[3];
            long epochStart = (long) dbManager.returnDataMeeting(meetingID)[2];
            return calendarManager.deleteEvent(calendarID, epochStart);
        } catch (IOException | SQLException e) {
            LOGGER.fatal(String.format("Unable to delete a meeting[meeting_data].%n%s", e));
            return false;
        }
    }

    public EmbedBuilder buildEmbed(int meetingID, String host, String participant, String starttime, String endtime, String message) {

        return new EmbedBuilder()
                .setColor(new Color(140, 158, 255))
                .setAuthor("Meeting", null, BotMain.getJda().getSelfUser().getAvatarUrl())
                .addField("Meeting ID", Integer.toString(meetingID), false)
                .addField("Host", host, true)
                .addField("Participant", participant, true)
                .addBlankField(true)
                .addField("Starttime", starttime, true)
                .addField("Endtime", endtime, true)
                .addBlankField(true)
                .addField("Message", message, true);
    }
}
