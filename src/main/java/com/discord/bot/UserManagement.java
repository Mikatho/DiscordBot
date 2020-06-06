package com.discord.bot;

import com.discord.bot.data.UserData;

import java.io.IOException;
import java.sql.SQLException;

public class UserManagement {

    private static final UserManagement INSTANCE = new UserManagement();

    private DatabaseManagement dbManager = DatabaseManagement.getINSTANCE();

    public static UserManagement getINSTANCE() {
        return INSTANCE;
    }

    public boolean register(String userID) {

        //Erstellt neue Instanz mit Daten
        UserData tempUser = new UserData(userID);

        //Versucht User in Datenbank einzufügen
        try {
            return dbManager.insertUser(tempUser);
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean delete(String userID) {

        //Versucht User aus Datenbank zu löschen
        try {
            return dbManager.deleteUser(userID);
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean update(String userID, String column, String newValue) {

        //Versucht User in Datenbank zu updaten
        try {
            return dbManager.updateUser(userID, column, newValue);
        } catch (SQLException e) {
            return false;
        }
    }

    public Object[] search(String userID) {

        //Versucht User in Datenbank zu finden und zurückzugeben
        return dbManager.returnData(userID);
    }

    public boolean startActivity(String time) {

        return true;
    }

    public boolean stopActivity(String time) {

        return true;
    }

    //Prüft, ob User registriert ist
    public boolean userIsRegistered(String userID) {

        try {
            return DatabaseManagement.getINSTANCE().registeredCheck(userID);
        } catch (SQLException e) {
            return false;
        }
    }

    public String googleCalendarID(String nickname) {

        try {
            return GoogleCalendarManagement.getInstance().createCalendar(nickname);
        } catch (IOException e) {
            return null;
        }
    }

    public String googleCalendarLink(String calendarID) {

            return GoogleCalendarManagement.getInstance().getPublicCalendarLink(calendarID);
    }
}
