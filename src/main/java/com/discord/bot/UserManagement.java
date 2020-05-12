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

        //Erstellt neue Instanz mit Daten
        UserData tempUser = new UserData(uID, uName);

        //Fügt Instanz in HashMap hinzu
        user.put(uID, tempUser);

        //Versucht User in Datenbank einzufügen
        return dbManager.insert(tempUser);
    }

    public boolean delete(String uID) {

        //Speichert sich User
        UserData tempUser = user.get(uID);

        //Löscht User aus HashMap
        user.remove(uID);

        //Versucht User aus Datenbank zu löschen
        return dbManager.delete(tempUser);
    }

    public boolean update(String uID, String column, String newValue) {

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
                    values[i] = values[i].trim();
                }
            } else {
                values = newValue.split(" ");
            }

            if (column.equals("interests")) {
                tempUser.setInterests(values);
            } else if (column.equals("competencies")) {
                tempUser.setCompetencies(values);
            }
        }

        //Versucht User in Datenbank zu updaten
        return dbManager.update(tempUser, column, newValue);
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
