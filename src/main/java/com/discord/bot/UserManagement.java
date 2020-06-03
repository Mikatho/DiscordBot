package com.discord.bot;

import com.discord.bot.data.UserData;

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
        return dbManager.insertUser(tempUser);
    }

    public boolean delete(String userID) {

        //Versucht User aus Datenbank zu löschen
        return dbManager.deleteUser(userID);
    }

    public boolean update(String userID, String column, String newValue) {

        //Versucht User in Datenbank zu updaten
        return dbManager.updateUser(userID, column, newValue);
    }

    public boolean startActivity(String time) {

        return true;
    }

    public boolean stopActivity(String time) {

        return true;
    }

    public boolean userIsRegistered (String userID) {

        return DatabaseManagement.getINSTANCE().registeredCheck(userID);
    }

    public Object [] returnUser(String userID) {

        return dbManager.returnData(userID);
    }
}
