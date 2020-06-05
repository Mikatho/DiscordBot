package com.discord.bot;

import com.discord.bot.data.UserActivity;
import com.discord.bot.data.UserData;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.concurrent.ConcurrentHashMap;


/**
 * This Class is the adapter of the <code>DatabaseManaging</code> and <code>UserCommand</code>.
 * It handles the interactions between them.
 *
 * @author      L2G4
 * @version     %I%, %G%
 * @see         com.discord.bot.commands.UserCommand
 * @see         com.discord.bot.DatabaseManagement
 * @see         com.discord.bot.data.UserData
 * @see         com.discord.bot.CommandManager
 * @since       1.0
 */
public class UserManagement {

    private static final UserManagement INSTANCE = new UserManagement();    // creates INSTANCE of Class

    private DatabaseManagement dbManager = DatabaseManagement.getINSTANCE();

    /**
     * The getINSTANCE() method return the instance of the UserManagement object.
     *
     * @return  INSTANCE    instance of the UserManagement object.
     */
    public static UserManagement getINSTANCE() {
        return INSTANCE;
    }

    /**
     * The register method creates with the userID a new instance of <code>UserData</code>
     * and insert it in the database.
     *
     * @param   userID      unique ID to identify a user.
     * @return  <code>true</code> Successfully added to the database;
     *                  <code>false</code> Could not add to database.
     */
    public boolean register(String userID) {

        UserData tempUser = new UserData(userID);
        return dbManager.insert(tempUser);
    }

    /**
     * Delete user in database.
     * Call the <code>#deleteUser(String userID)</code> method in <code>DatabaseManagement</code>.
     * This method is called from the <code>UserCommand</code> class.
     *
     * @param   userID      unique Discord ID.
     * @return  <code>true</code> Successfully deleted the user;
     *                  <code>false</code> user doesn´t exists.
     */
    public boolean delete(String userID) {

        return dbManager.deleteUser(userID);
    }

    /**
     * This method updates the manipulated userData in the database.
     *
     * @param userID    unique Discord ID.
     * @param column    [interests} / [competencies] / [address].
     * @param newValue  contains new attribute for the userData.
     * @return  <code>true</code> Successfully update the userData;
     *                  <code>false</code> could not be updated.
     */
    public boolean update(String userID, String column, String newValue) {

        return dbManager.updateUser(userID, column, newValue);
    }

    /**
     * Call <code>returnData(String userID)</code> in <code>DatabaseManagement()</code> to get data
     * of the userID.
     *
     * @see com.discord.bot.commands.UserCommand#executeCommand(MessageChannel, Message)
     * @see com.discord.bot.DatabaseManagement#returnData(String)
     *
     * @param   userID  unique Discord ID of search user.
     * @return  data    contains instance of user data.
     */
    public Object[] search(String userID) {

        Object[] data = dbManager.returnData(userID);
        return data;
    }

    public boolean startActivity(String time) {
    //todo
        return true;
    }

    public boolean stopActivity(String time) {
    //todo
        return true;
    }
}
