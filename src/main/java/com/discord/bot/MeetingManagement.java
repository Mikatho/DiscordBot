package com.discord.bot;

import com.discord.bot.data.MeetingData;

import java.util.concurrent.ConcurrentHashMap;

public class MeetingManagement {

    private static final MeetingManagement INSTANCE = new MeetingManagement();

    private DatabaseManagement dbManager = DatabaseManagement.getINSTANCE();

    private UserManagement userMng = UserManagement.getINSTANCE();

    public static MeetingManagement getINSTANCE() {
        return INSTANCE;
    }

    public boolean insert(String userID, String startTime, String endTime, String message) {

        //Erstellt neue Instanz mit Daten
        MeetingData tempMeeting = new MeetingData(userID, startTime, endTime, message);

        //Versucht Meeting in Datenbank einzufügen
        return dbManager.insert(tempMeeting);
    }


    public boolean delete(Integer meetingID) {

        //Versucht Meeting aus Datenbank zu löschen
        return dbManager.deleteMeeting(meetingID);
    }

    public boolean update(Integer meetingID, String column, String newValue) {

        //Versucht Meeting in Datenbank zu updaten
        return dbManager.updateMeeting(meetingID, column, newValue);
    }
}
