package com.discord.bot;

import com.discord.bot.data.MeetingData;

import java.io.IOException;
import java.sql.SQLException;

public class MeetingManagement {

    private static final MeetingManagement INSTANCE = new MeetingManagement();

    private DatabaseManagement dbManager = DatabaseManagement.getINSTANCE();

    public static MeetingManagement getINSTANCE() {
        return INSTANCE;
    }

    public MeetingData insert(String hostID, String participantID, long starttime, long endtime, int duration, String message) {

        MeetingData tempMeeting;

        int returnedValue;

        //Returnt starttime & endtime als long[starttime, endtime]
        long[] meetingtimes = dbManager.findEarliestPossibleMeetingtimes(participantID, starttime, endtime, duration);

        long foundStarttime = meetingtimes[0];
        long foundEndtime = meetingtimes[1];

        //Falls das Suchen nach Start- und Endtime nicht erfolgreich ausgeführt werden konnte
        if (foundStarttime == 0 || foundEndtime == 0) {
            return null;
        }

        //Erstellt neue Instanz mit Daten
        if (message.equals("N/a")) {
            //Wenn keine Message mitgegeben wurde
            tempMeeting = new MeetingData(hostID, participantID, foundStarttime, foundEndtime);
        } else {
            tempMeeting = new MeetingData(hostID, participantID, foundStarttime, foundEndtime, message);
        }

        try {
            returnedValue = dbManager.insertMeeting(tempMeeting);
        } catch (SQLException e) {
            return null;
        }

        if (returnedValue == 0) {
            return null;
        }
        tempMeeting.setMeetingID(returnedValue);

        return tempMeeting;
    }


    public boolean delete(Integer meetingID, String userID) {

        //Wenn User nicht die nötige Berechtigung hat
        try {
            if (!dbManager.authorizationCheck(meetingID, userID)) {
                return false;
            }
        } catch (SQLException e) {
            return false;
        }

        //Versucht Meeting aus Datenbank zu löschen
        try {
            return dbManager.deleteMeeting(meetingID);
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean update(Integer meetingID, String hostID, String column, Object newValue) {

        //Wenn User nicht die nötige Berechtigung hat
        try {
            if (!dbManager.authorizationCheck(meetingID, hostID)) {
                return false;
            }
        } catch (SQLException e) {
            return false;
        }

        //Versucht Meeting in Datenbank zu updaten
        return dbManager.updateMeeting(meetingID, column, newValue, hostID);
    }

    //Prüft, ob User registriert ist
    public boolean userIsRegistered (String userID) {

        try {
            return DatabaseManagement.getINSTANCE().registeredCheck(userID);
        } catch (SQLException e) {
            return false;
        }
    }

    //Returnt Google Calendar Event
    public String googleCalendarEvent(String userID, String eventName, String eventLocation, String eventDescription, long starttime, long endtime) {

        String calendarID = (String) dbManager.returnData(userID)[3];

        try {
            return GoogleCalendarManagement.getInstance().createNewEvent(calendarID, eventName, eventLocation, eventDescription, starttime, endtime);
        } catch (IOException e) {
            return null;
        }
    }
}
