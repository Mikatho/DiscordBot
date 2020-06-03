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

    public boolean register(String userID) {

        //Erstellt neue Instanz mit Daten
        UserData tempUser = new UserData(userID);

        //Versucht User in Datenbank einzufügen
        return dbManager.insert(tempUser);
    }

    public boolean delete(String userID) {

        //Versucht User aus Datenbank zu löschen
        return dbManager.deleteUser(userID);
    }

    public boolean update(String userID, String column, String newValue) {

        //Versucht User in Datenbank zu updaten
        return dbManager.updateUser(userID, column, newValue);
    }

    public Object[] search(String userID) {

        //Versucht UserData aus Datenbank zu holen
        Object[] data = dbManager.returnData(userID);
        return data;
    }

    public boolean startActivity(String time) {

        return true;
    }

    public boolean stopActivity(String time) {

        return true;
    }
}
