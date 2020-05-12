package com.discord.bot;

import com.discord.bot.data.MeetingData;

import java.util.concurrent.ConcurrentHashMap;

public class MeetingManagement {

    private static final MeetingManagement INSTANCE = new MeetingManagement();

    private ConcurrentHashMap<Integer, MeetingData> meetings = new ConcurrentHashMap<>();

    private int meetingID;

    private DatabaseManagement dbManager = DatabaseManagement.getINSTANCE();

    private UserManagement userMng = UserManagement.getINSTANCE();

    public static MeetingManagement getINSTANCE() {
        return INSTANCE;
    }

    public ConcurrentHashMap<Integer, MeetingData> getMeetings() {
        return meetings;
    }

    public void setMeetingID(int meetingID) {
        this.meetingID = meetingID;
    }

    public boolean insert(String userID, String startTime, String endTime, String message) {

        //Inkrementiert MeetingID
        meetingID++;

        //Erstellt neue Instanz mit Daten
        MeetingData tempMeeting = new MeetingData(meetingID, userID, startTime, endTime, message);

        //Fügt Meeting zu User hinzu
        userMng.getUser().get(userID).getMeetings().add(tempMeeting);

        //Fügt Instanz in HashMap hinzu
        meetings.put(meetingID, tempMeeting);

        //Versucht Meeting in Datenbank einzufügen
        return dbManager.insert(tempMeeting);
    }


    public boolean delete(Integer meetingID) {

        //Speichert sich Meeting
        MeetingData tempMeeting = meetings.get(meetingID);

        //Löscht Meeting vom User
        userMng.getUser().get(tempMeeting.getUserID()).getMeetings().remove(tempMeeting);

        //Löscht Meeting aus HashMap
        meetings.remove(meetingID);

        //Versucht Meeting aus Datenbank zu löschen
        return dbManager.delete(tempMeeting);
    }

    public boolean update(Integer meetingID, String column, String newValue) {

        //speichert sich Meeting
        MeetingData tempMeeting = meetings.get(meetingID);

        //Updatet Meeting-Instanz
        switch (column) {
            case "starttime":
                tempMeeting.setStartTime(newValue);
                break;
            case "endtime":
                tempMeeting.setEndTime(newValue);
                break;
            case "message":
                tempMeeting.setMessage(newValue);
                break;
        }

        //Versucht Meeting in Datenbank zu updaten
        return dbManager.update(tempMeeting, column, newValue);
    }
}
