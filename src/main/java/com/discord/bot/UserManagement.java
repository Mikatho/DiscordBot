package com.discord.bot;

import com.discord.bot.data.UserActivity;
import com.discord.bot.data.UserData;

import java.util.concurrent.ConcurrentHashMap;

public class UserManagement {

    private static final UserManagement INSTANCE = new UserManagement();

    private DatabaseManagement dbManager = DatabaseManagement.getINSTANCE();

    public static UserManagement getINSTANCE() {
        return INSTANCE;
    }

    public boolean register(String userID, String uName) {

        //Erstellt neue Instanz mit Daten
        UserData tempUser = new UserData(userID, uName);

        //Versucht User in Datenbank einzufügen
        return dbManager.insert(tempUser);
    }

    public boolean delete(String userID) {

        //Versucht User aus Datenbank zu löschen
        return dbManager.deleteUser(userID);
    }

    public boolean update(String userID, String column, String newValue) {

        /*Updated User-Instanz
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
         */

        //Versucht User in Datenbank zu updaten
        return dbManager.updateUser(userID, column, newValue);
    }

    public boolean startActivity(String time) {

        return true;
    }

    public boolean stopActivity(String time) {

        return true;
    }
}
