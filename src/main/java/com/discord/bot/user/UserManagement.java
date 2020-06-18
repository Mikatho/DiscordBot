package com.discord.bot.user;

import com.discord.bot.external_functions.DatabaseManagement;
import com.discord.bot.external_functions.GoogleCalendarManagement;
import com.discord.bot.communication.CommandManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;

/**
 * This Class is the adapter of the <code>DatabaseManaging</code> and <code>UserCommand</code>.
 * It handles the interactions between them.
 *
 * @author L2G4
 * @version %I%, %G%
 * @see com.discord.bot.communication.commands.UserCommand
 * @see DatabaseManagement
 * @see UserData
 * @see CommandManager
 * @since 1.0
 */
public class UserManagement {

    private static final Logger LOGGER = LogManager.getLogger(UserManagement.class.getName());

    private static final UserManagement INSTANCE = new UserManagement();

    private DatabaseManagement dbManager = DatabaseManagement.getINSTANCE();

    /**
     * The getINSTANCE() method return the instance of the UserManagement object.
     *
     * @return INSTANCE    instance of the UserManagement object.
     */
    public static UserManagement getINSTANCE() {
        return INSTANCE;
    }

    /**
     * The register method creates with the userID a new instance of <code>UserData</code>
     * and insert it in the database.
     *
     * @param userID unique ID to identify a user.
     * @return <code>true</code> Successfully added to the database;
     * <code>false</code> Could not add to database.
     */
    public boolean register(String userID) {

        // Instantiate UserData.
        UserData tempUser = new UserData(userID);

        // Add user to database.
        try {
            dbManager.insertUser(tempUser);
            return true;
        } catch (SQLException e) {
            LOGGER.fatal(String.format("Unable to add user to database[user_data].%n%s", e));
            return false;
        }
    }

    /**
     * Delete user in database.
     * Call the <code>#deleteUser(String userID)</code> method in <code>DatabaseManagement</code>.
     * This method is called from the <code>UserCommand</code> class.
     *
     * @param userID unique Discord ID.
     * @return <code>true</code> Successfully deleted the user;
     * <code>false</code> user doesn´t exists.
     */
    public boolean delete(String userID) {

        try {
            return dbManager.deleteUser(userID);
        } catch (SQLException e) {
            LOGGER.fatal(String.format("Unable to delete user in database[user_data].%n%s", e));
            return false;
        }
    }

    /**
     * This method updates the manipulated userData in the database.
     *
     * @param userID   unique Discord ID.
     * @param column   [interests} / [competencies] / [address].
     * @param newValue contains new attribute for the userData.
     * @return <code>true</code> Successfully update the userData;
     * <code>false</code> could not be updated.
     */
    public boolean update(String userID, String column, String newValue) {

        try {
            dbManager.updateUser(userID, column, newValue);
            return true;
        } catch (SQLException e) {
            LOGGER.fatal(String.format("Unable to update user[user_data].%n%s", e));
            return false;
        }
    }

    /**
     * Call <code>returnData(String userID)</code> in <code>DatabaseManagement()</code> to get data
     * of the userID.
     *
     * @param userID unique Discord ID of search user.
     * @return data    contains instance of user data.
     */
    public UserData search(String userID) {

        try {
            return dbManager.returnUser(userID);
        } catch (SQLException e) {
            LOGGER.fatal(String.format("Unable to return userData[user_data].%n%s", e));
            return null;
        }
    }

    public boolean userIsRegistered(String userID) {

        try {
            return DatabaseManagement.getINSTANCE().registeredCheck(userID);
        } catch (SQLException e) {
            LOGGER.fatal(String.format("Unable to check if user is registered[user_data].%n%s", e));
            return false;
        }
    }

    public String googleCalendarID(String nickname) {

        try {
            return GoogleCalendarManagement.getInstance().createCalendar(nickname);
        } catch (IOException e) {
            LOGGER.fatal(String.format("Unable to get CoogleCalenderManagement instance.%n%s", e));
            return null;
        }
    }

    public String googleCalendarLink(String calendarID) {

        return GoogleCalendarManagement.getInstance().getPublicCalendarLink(calendarID);
    }
}
