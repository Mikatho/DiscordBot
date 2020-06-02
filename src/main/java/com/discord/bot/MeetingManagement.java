package com.discord.bot;

import com.discord.bot.data.MeetingData;

import java.util.Arrays;

public class MeetingManagement {

    private static final MeetingManagement INSTANCE = new MeetingManagement();

    private DatabaseManagement dbManager = DatabaseManagement.getINSTANCE();

    public static MeetingManagement getINSTANCE() {
        return INSTANCE;
    }

    public boolean insert(String hostID, String participantID, long starttime, long endtime, int duration, String message) {

        MeetingData tempMeeting;

        //Returnt starttime & endtime als long[starttime, endtime]
        long[] meetingtimes = dbManager.findEarliestPossibleMeetingtimes(participantID, starttime, endtime, duration);

        //Falls das Suchen nach Start- und Endtime nicht erfolgreich ausgeführt werden konnte
        if (meetingtimes[0] == 0 || meetingtimes[1] == 0) {
            return false;
        }

        //Erstellt neue Instanz mit Daten
        if (message == null) {
            //Wenn keine Message mitgegeben wurde
            tempMeeting = new MeetingData(hostID, participantID, meetingtimes[0], meetingtimes[1]);
        } else {
            tempMeeting = new MeetingData(hostID, participantID, meetingtimes[0], meetingtimes[1], message);
        }

        //Versucht Meeting in Datenbank einzufügen
        return dbManager.insert(tempMeeting);
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
