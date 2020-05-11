package com.discord.bot;

import com.discord.bot.data.MeetingData;

<<<<<<< HEAD
=======
import java.sql.*;
>>>>>>> fa80302b304130282fe7f33c3855d11f3576c846
import java.util.concurrent.ConcurrentHashMap;

public class MeetingManagement {

    private static final MeetingManagement INSTANCE = new MeetingManagement();

    private ConcurrentHashMap<Integer, MeetingData> meetings = new ConcurrentHashMap<>();

    private int meetingID;

<<<<<<< HEAD
    private UserManagement userMng = UserManagement.getINSTANCE();

=======
>>>>>>> fa80302b304130282fe7f33c3855d11f3576c846
    private DatabaseManagement dbManager = DatabaseManagement.getINSTANCE();

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

<<<<<<< HEAD
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
=======
        meetingID++;

        MeetingData tempData = new MeetingData(meetingID, userID, startTime, endTime, message);

        if (!dbManager.insert(tempData)) {
            return false;
        }

        meetings.put(meetingID, tempData);
        return true;
>>>>>>> fa80302b304130282fe7f33c3855d11f3576c846
    }


    public boolean delete(Integer meetingID) {

<<<<<<< HEAD
        //Speichert sich Meeting
        MeetingData tempMeeting = meetings.get(meetingID);

        //Löscht Meeting vom User
        userMng.getUser().get(tempMeeting.getUserID()).getMeetings().remove(tempMeeting);

        //Löscht Meeting aus HashMap
        meetings.remove(meetingID);

        //Versucht Meeting aus Datenbank zu löschen
        return dbManager.delete(tempMeeting);
=======
        MeetingData meeting = meetings.get(meetingID);

        if (!dbManager.delete(meeting)) {
            return false;
        }

        meetings.remove(meetingID);
        return true;
>>>>>>> fa80302b304130282fe7f33c3855d11f3576c846
    }

    public boolean update(Integer meetingID, String column, String newValue) {

<<<<<<< HEAD
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
=======
        MeetingData meetingData = meetings.get(meetingID);

        if (!dbManager.update(meetingData, column, newValue)) {
            return false;
        }

        switch (column) {
            case "starttime":
                meetingData.setStartTime(newValue);
                break;
            case "endtime":
                meetingData.setEndTime(newValue);
                break;
            case "message":
                meetingData.setMessage(newValue);
                break;
        }
        return true;
>>>>>>> fa80302b304130282fe7f33c3855d11f3576c846
    }
}
