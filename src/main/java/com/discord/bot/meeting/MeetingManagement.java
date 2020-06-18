package com.discord.bot.meeting;

import com.discord.bot.BotMain;
import com.discord.bot.external_functions.DatabaseManagement;
import com.discord.bot.external_functions.GoogleCalendarManagement;
import net.dv8tion.jda.api.EmbedBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * This Class is the adapter of the <code>DatabaseManaging</code> and <code>MeetingCommand</code>.
 * It handles the interactions between them.
 *
 * @author L2G4
 * @version %I%, %G%
 * @see MeetingManagement
 * @see DatabaseManagement
 * @see MeetingData
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

    /**
     * Checks if a user is registered to database
     *
     * @param userID user to check if registered
     * @return if the user is registered or not
     */
    public boolean userIsRegistered(String userID) {

        try {
            return DatabaseManagement.getINSTANCE().registeredCheck(userID);
        } catch (SQLException e) {
            LOGGER.fatal(String.format("Unable to register a user[user_data].%n%s", e));
            return false;
        }
    }

    /**
     * Checks if a user is authorized to update/delete the meeting
     *
     * @param meetingID meeting to be checked
     * @param userID user to be checked
     * @return if user is authorized
     */
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

    /**
     * Creates google calendar event
     *
     * @param userID userID of event host
     * @param eventName name of event
     * @param eventLocation location of even
     * @param eventDescription description of event
     * @param starttime starttime of event
     * @param endtime endtime of event
     * @return the event
     */
    public String googleCalendarEvent(String userID, String eventName, String eventLocation, String eventDescription, long starttime, long endtime) {

        try {
            String calendarID = dbManager.returnUser(userID).getgCalendarLink();
            return calendarManager.createNewEvent(calendarID, eventName, eventLocation, eventDescription, starttime, endtime);
        } catch (SQLException | IOException e) {
            LOGGER.fatal(String.format("Unable to return a meeting[meeting_data].%n%s", e));
            return null;
        }
    }

    /**
     * Search a single meeting
     *
     * @param meetingID meeting to be searched
     * @return the meeting as MeetingData
     */
    public MeetingData search(int meetingID) {

        try {
            return dbManager.returnMeeting(meetingID);
        } catch (SQLException e) {
            LOGGER.fatal(String.format("Unable to search a meeting[meeting_data].%n%s", e));
            return null;
        }
    }

    /**
     * Search all meetings of a user
     *
     * @param userID user to be searched in
     * @param duration describes if meetings should only be searched until a certain date
     * @return list of all meetings
     */
    public List<MeetingData> searchAllMeetings(String userID, long duration) {

        try {
            if (duration != 0) {
                return dbManager.returnMultipleMeetings(userID, duration);
            } else {
                return dbManager.returnMultipleMeetings(userID);
            }
        } catch (SQLException e) {
            LOGGER.fatal(String.format("Unable to search meetings[meeting_data].%n%s", e));
            return new ArrayList<>();
        }
    }

    /**
     * Deletes google calendar event
     *
     * @param userID user of the google calendar to delete the meeting
     * @param meetingID meeting to be deleted
     * @return if delete was successful or not
     */
    public boolean deleteGoogleCalendarEvent(String userID, int meetingID) {

        try {
            String calendarID = dbManager.returnUser(userID).getgCalendarLink();
            long epochStart = dbManager.returnMeeting(meetingID).getStarttime();
            return calendarManager.deleteEvent(calendarID, epochStart);
        } catch (IOException | SQLException e) {
            LOGGER.fatal(String.format("Unable to delete a meeting[meeting_data].%n%s", e));
            return false;
        }
    }

    /**
     * Build an embed of a meeting to send to the user
     *
     * @param meetingID ID of the meeting
     * @param host host of the meeting
     * @param participant participant of the meeting
     * @param starttime starttime of the meeting
     * @param endtime endtime of the meeting
     * @param message additional message of the meeting
     * @return returns the Embed
     */
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
