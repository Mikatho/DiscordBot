package com.discord.bot;

import com.discord.bot.data.MeetingData;
import com.discord.bot.data.UserData;

import java.io.IOException;

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

        returnedValue = dbManager.insertMeeting(tempMeeting);

        if (returnedValue == 0) {
            return null;
        }
        tempMeeting.setMeetingID(returnedValue);

        return tempMeeting;
    }


    public boolean delete(Integer meetingID, String userID) {

        //Wenn User nicht die nötige Berechtigung hat
        if (!dbManager.authorizationCheck(meetingID, userID)) {
            return false;
        }

        //Versucht Meeting aus Datenbank zu löschen
        return dbManager.deleteMeeting(meetingID);
    }

    public boolean update(Integer meetingID, String hostID, String column, Object newValue) {

        //Wenn User nicht die nötige Berechtigung hat
        if (!dbManager.authorizationCheck(meetingID, hostID)) {
            return false;
        }

        //Versucht Meeting in Datenbank zu updaten
        return dbManager.updateMeeting(meetingID, column, newValue, hostID);
    }

    public boolean userIsRegistered (String userID) {

        return DatabaseManagement.getINSTANCE().registeredCheck(userID);
    }

    public String googleCalendarEvent(String userID, String eventName, String eventLocation, String eventDescription, long starttime, long endtime) {

        UserData tempUser = new UserData(userID);

        tempUser.setgCalendarLink((String) dbManager.returnData(userID)[3]);

        try {
            return GoogleCalendarManager.getInstance().createNewEvent(tempUser, eventName, eventLocation, eventDescription, starttime, endtime);
        } catch (IOException e) {
            System.out.println("Could not create Event in Google Calendar.");
            return null;
        }
    }
}
