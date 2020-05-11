package com.discord.bot;

import com.discord.bot.data.UserActivity;
import com.discord.bot.data.UserData;

import java.util.concurrent.ConcurrentHashMap;

public class UserManagement {

    private static final UserManagement INSTANCE = new UserManagement();

    private ConcurrentHashMap<String, UserData> user = new ConcurrentHashMap<>();

    private int activityID;

    private DatabaseManagement dbManager = DatabaseManagement.getINSTANCE();

    public static UserManagement getINSTANCE() {
        return INSTANCE;
    }

    public ConcurrentHashMap<String, UserData> getUser() {
        return user;
    }

    public ConcurrentHashMap<Integer, UserActivity> activities = new ConcurrentHashMap<>();

    public ConcurrentHashMap<Integer, UserActivity> getActivities() {
        return activities;
    }

    public void setActivityID(int activityID) {
        this.activityID = activityID;
    }

    public boolean register(String uID, String uName) {

<<<<<<< HEAD
        //Erstellt neue Instanz mit Daten
        UserData tempUser = new UserData(uID, uName);

        //Fügt Instanz in HashMap hinzu
        user.put(uID, tempUser);

        //Versucht User in Datenbank einzufügen
        return dbManager.insert(tempUser);
=======
        UserData userData = new UserData(uID, uName);

        if (!dbManager.insert(userData)) {
            return false;
        }

        user.put(uID, userData);
        return true;
>>>>>>> fa80302b304130282fe7f33c3855d11f3576c846
    }

    public boolean delete(String uID) {

<<<<<<< HEAD
        //Speichert sich User
        UserData tempUser = user.get(uID);

        //Löscht User aus HashMap
        user.remove(uID);

        //Versucht User aus Datenbank zu löschen
        return dbManager.delete(tempUser);
=======
        UserData userData = user.get(uID);

        if (!dbManager.delete(userData)) {
            return false;
        }

        user.remove(uID);
        return true;
>>>>>>> fa80302b304130282fe7f33c3855d11f3576c846
    }

    public boolean update(String uID, String column, String newValue) {

<<<<<<< HEAD
        //Speichert sich User
        UserData tempUser = user.get(uID);

        //Updated User-Instanz
        if (column.equals("address")) {
            tempUser.setAddress(newValue);
        } else {
            String[] values;

            //Prüft, ob mehrere Argumente mit Komma oder Leerzeichen getrennt wurden
            if (newValue.contains(",")) {
                values = newValue.split(",");

                for (int i = 0; i < values.length; i++) {
=======
        UserData userData = user.get(uID);

        if (!dbManager.update(userData, column, newValue)) {
            return false;
        }

        if (column.equals("address")) {
            userData.setAddress(newValue);
        } else {
            String[] values;

            if (newValue.contains(",")) {
                values = newValue.split(",");

                for (int i=0; i<values.length; i++) {
>>>>>>> fa80302b304130282fe7f33c3855d11f3576c846
                    values[i] = values[i].trim();
                }
            } else {
                values = newValue.split(" ");
            }

            if (column.equals("interests")) {
<<<<<<< HEAD
                tempUser.setInterests(values);
            } else if (column.equals("competencies")) {
                tempUser.setCompetencies(values);
            }
        }

        //Versucht User in Datenbank zu updaten
        return dbManager.update(tempUser, column, newValue);
=======
                userData.setInterests(values);
            } else if (column.equals("competencies")) {
                userData.setCompetencies(values);
            }
        }
        return true;
>>>>>>> fa80302b304130282fe7f33c3855d11f3576c846
    }

    public boolean startActivity(String time) {

        activityID++;

        activities.put(activityID, new UserActivity(activityID, time));

        return true;
    }

    public boolean stopActivity(String time) {

        return true;
    }
}
