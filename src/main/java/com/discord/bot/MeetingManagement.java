package com.discord.bot;

import com.discord.bot.data.MeetingData;

import java.util.Arrays;

public class MeetingManagement {

    private static final MeetingManagement INSTANCE = new MeetingManagement();

    private DatabaseManagement dbManager = DatabaseManagement.getINSTANCE();

    public static MeetingManagement getINSTANCE() {
        return INSTANCE;
    }

    public Object[] insert(String hostID, String participantID, long starttime, long endtime, int duration, String message) {

        MeetingData tempMeeting;

        Object[] returnValues = new Object[3];

        //Returnt starttime & endtime als long[starttime, endtime]
        long[] meetingtimes = dbManager.findEarliestPossibleMeetingtimes(participantID, starttime, endtime, duration);

        long foundStarttime = meetingtimes[0];
        long foundEndtime = meetingtimes[1];

        //Falls das Suchen nach Start- und Endtime nicht erfolgreich ausgeführt werden konnte
        if (foundStarttime == 0 || foundEndtime == 0) {
            return returnValues;
        }

        //Erstellt neue Instanz mit Daten
        if (message.equals("N/a")) {
            //Wenn keine Message mitgegeben wurde
            tempMeeting = new MeetingData(hostID, participantID, foundStarttime, foundEndtime);
        } else {
            tempMeeting = new MeetingData(hostID, participantID, foundStarttime, foundEndtime, message);
        }

        //Fügt Daten zu Rückgabe-Array hinzu
        returnValues[0] = dbManager.insert(tempMeeting);
        returnValues[1] = foundStarttime;
        returnValues[2] = foundEndtime;

        return returnValues;
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
}
