package com.discord.bot;

import com.discord.bot.data.MeetingData;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

public class MeetingManagement {

    private static final MeetingManagement INSTANCE = new MeetingManagement();

    private DatabaseManagement dbManager = DatabaseManagement.getINSTANCE();

    private GoogleCalendarManagement calendarManager = GoogleCalendarManagement.getInstance();

    private ConcurrentHashMap<String, String> botMessageHolder = new ConcurrentHashMap<>();

    private ConcurrentHashMap<String, Object[]> botValueHolder = new ConcurrentHashMap<>();

    public static MeetingManagement getINSTANCE() {
        return INSTANCE;
    }

    public ConcurrentHashMap<String, String> getBotMessageHolder() {
        return botMessageHolder;
    }

    public ConcurrentHashMap<String, Object[]> getBotValueHolder() {
        return botValueHolder;
    }

    public int insert(String hostID, String participantID, long starttime, long endtime, String message) {

        MeetingData tempMeeting;

        //Erstellt neue Instanz mit Daten
        if (message.equals("N/a")) {
            //Wenn keine Message mitgegeben wurde
            tempMeeting = new MeetingData(hostID, participantID, starttime, endtime);
        } else {
            tempMeeting = new MeetingData(hostID, participantID, starttime, endtime, message);
        }

        try {
            return dbManager.insertMeeting(tempMeeting);
        } catch (SQLException e) {
            return 0;
        }
    }


    public boolean delete(Integer meetingID) {

        try {
            return dbManager.deleteMeeting(meetingID);
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean update(Integer meetingID, String hostID, String column, Object newValue) {

        //Versucht Meeting in Datenbank zu updaten
        try {
            dbManager.updateMeeting(meetingID, column, newValue, hostID);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    //Prüft, ob User registriert ist
    public boolean userIsRegistered (String userID) {

        try {
            return DatabaseManagement.getINSTANCE().registeredCheck(userID);
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean authorizationCheck(int meetingID, String userID) {

        //Wenn User nicht die nötige Berechtigung hat
        try {
            return dbManager.authorizationCheck(meetingID, userID);
        } catch (SQLException e) {
            return false;
        }
    }

    public long[] earliestPossibleMeeting(String userID, long starttime, long endtime, int duration) throws SQLException {

        return dbManager.findEarliestPossibleMeetingtimes(userID, starttime, endtime, duration);
    }

    //Returnt Google Calendar Event
    public String googleCalendarEvent(String userID, String eventName, String eventLocation, String eventDescription, long starttime, long endtime) {

        try {
            String calendarID = (String) dbManager.returnDataUser(userID)[3];
            return calendarManager.createNewEvent(calendarID, eventName, eventLocation, eventDescription, starttime, endtime);
        } catch (SQLException | IOException e) {
            return null;
        }
    }

    public Object[] search(int meetingID) {

        //Versucht User in Datenbank zu finden und zurückzugeben
        try {
            return dbManager.returnDataMeeting(meetingID);
        } catch (SQLException e) {
            return null;
        }
    }

    //Versucht Google Calender Event zu löschen
    public boolean deleteGoogleCalendarEvent(String userID, int meetingID) {

        try {
            String calendarID = (String) dbManager.returnDataUser(userID)[3];
            long epochStart = (long) dbManager.returnDataMeeting(meetingID)[2];
            return calendarManager.deleteEvent(calendarID, epochStart);
        } catch (IOException | SQLException e) {
            return false;
        }
    }

    //Baut Embed für Meetings
    public EmbedBuilder buildEmbed(String avatar, int meetingID, String host, String participant, String starttime, String endtime, String message) {

        return new EmbedBuilder()
                .setColor(new Color(140, 158, 255))
                .setAuthor("Meeting", null, avatar)
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
