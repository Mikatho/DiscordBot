package com.discord.bot;

import com.discord.bot.data.MeetingData;
import com.discord.bot.data.UserActivity;
import com.discord.bot.data.UserData;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.StringJoiner;


/**
 * Contain all methods which access the database.
 * [clear], [connect], [disconnect], [createTable], [insert], [insertForeignKey], [delete user],
 * [delete meeting], [update user], [update meeting], [returnData], [registeredCheck]
 * Execute all function calls from <code>UserManagement</code> and <code>MeetingManagement</code>
 *
 * @author      L2G4
 * @version     %I%, %G%
 * @see         com.discord.bot.UserManagement
 * @see         com.discord.bot.MeetingManagement
 * @see         UserManagement#getINSTANCE()
 * @see         MeetingManagement#getINSTANCE()
 * @see         com.discord.bot
 * @since       1.0
 */
public class DatabaseManagement {

    private static final DatabaseManagement INSTANCE = new DatabaseManagement();    // creates INSTANCE of Class

    private Connection conn;
    private Statement stmt;
    private ResultSet rs;

    /**
     * This method return an instance of the DatabaseManagement object.
     *
     * @return  INSTANCE    instance of the DatabaseManagement object.
     */
    public static DatabaseManagement getINSTANCE() {
        return INSTANCE;
    }

    /**
     * Delete all entries in database.
     *
     * @exception   // TODO: 04.06.2020
     */
    public void clear() {
        String meeting = "DELETE FROM meeting_data;";
        String activity = "DELETE FROM user_activity;";
        String user = "DELETE FROM user_data;";

        try {
            stmt.execute(meeting);
            stmt.execute(activity);
            stmt.execute(user);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Check if the database.db still exist and connect to the database.
     * If the file do not exist it will create a new file.
     *
     * @throws  // TODO: 04.06.2020
     */
    public void connect() {

        conn = null;

        try {
            /**
             * If database.db do not exist, create new database.
             */
            File file = new File("database.db");
            if (!file.exists()) {
                file.createNewFile();
            }

            String url = "jdbc:sqlite:" + file.getPath();
            conn = DriverManager.getConnection(url);

            System.out.println("Connection to Database created.");

            stmt = conn.createStatement();
            createTable("user_data", "userID text PRIMARY KEY", "address text", "interests text", "competencies text", "gCalendarLink text", "meetings INTEGER", "activities INTEGER", "FOREIGN KEY (meetings) REFERENCES meeting_data (meetingID)", "FOREIGN KEY (activities) REFERENCES user_activity (activityID)");
            createTable("meeting_data", "meetingID integer PRIMARY KEY AUTOINCREMENT", "hostID text NOT NULL", "participantID text NOT NULL", "starttime INTEGER NOT NULL", "endtime INTEGER NOT NULL", "message text");
            createTable("user_activity", "activityID integer PRIMARY KEY AUTOINCREMENT", "starttime text NOT NULL", "endtime text");
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Close the connection to the database.
     *
     * @exception   // TODO: 04.06.2020
     */
    public void disconnect() {
        try {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
            System.out.println("Connection to Database cut.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create tables by transfer SQL Code as String to create columns.
     *
     * @param   strings     SQL
     * @exception   // TODO: 04.06.2020
     */
    public void createTable(String... strings) {

        StringJoiner joiner = new StringJoiner(",");
        for (int i = 1; i < strings.length; i++) {
            joiner.add(strings[i]);
        }

        String sql = String.format("CREATE TABLE IF NOT EXISTS %s (%s);", strings[0], joiner.toString());

        try {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Check which object the method get: [UserData], [MeetingData], [UserActivity] and
     * insert the data in the right table in database.
     *
     * @param   obj     instance of [UserData], [MeetingData] or [UserActivity]
     * @exception   // TODO: 04.06.2020
     * @exception   // TODO: 04.06.2020  
     * @exception // TODO: 04.06.2020  
     * @return  <code>true</code> successfully added to the Database;
     *                   <code>false</code> could not add.
     */
    public boolean insert(Object obj) {

        /**
         * Check which object should insert: [UserData], [MeetingData], [UserActivity].
         */
        if (obj instanceof UserData) {

            /**
             * Cast object to UserData.
             */
            UserData user = (UserData) obj;

            /**
             * String with SQL code to insert UserData in user_data table in database.db.
             */
            String sql = "INSERT INTO user_data (userID) VALUES (?)";

            /**
             * Insert userData in database.
             */
            try (PreparedStatement prepStmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                prepStmt.setString(1, user.getUserID());
                prepStmt.executeUpdate();
                System.out.println("Successfully added the User to the Database!");
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Could not add the User to the Database!");
            }
        } else if (obj instanceof MeetingData) {

            /**
             * Cast object to MeetingData.
             */
            MeetingData meeting = (MeetingData) obj;

            /**
             * String with SQL code to insert MeetingData in meeting_data table in database.db.
             */
            String sql = "INSERT INTO meeting_data (hostID, participantID, startTime, endTime, message) VALUES (?, ?, ?, ?, ?)";

            /**
             * Insert meetingData in database.
             */
            try (PreparedStatement prepStmt = conn.prepareStatement(sql)) {
                prepStmt.setString(1, meeting.getUserID());
                prepStmt.setString(2, meeting.getParticipantID());
                prepStmt.setLong(3, meeting.getStartTime());
                prepStmt.setLong(4, meeting.getEndTime());
                prepStmt.setString(5, meeting.getMessage());
                prepStmt.executeUpdate();

                //Holt sich automatisch generierte ID
                rs = stmt.getGeneratedKeys();

                //Speichert sich ID aus ResultSet als Integer
                if (rs.next()) {
                    int meetingID = rs.getInt(1);

                    //Versucht ForeignKey in User_Data zu aktualisieren
                    if (!insertForeignKey("meetings", meetingID, meeting.getUserID(), meeting.getParticipantID())) {
                        return false;
                    }
                }

                System.out.println("Successfully added the Meeting to the Database!");
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Could not add the Meeting to the Database!");
            }
        } else if (obj instanceof UserActivity) {

            /**
             * Cast object to UserActivity.
             */
            UserActivity activity = (UserActivity) obj;

            /**
             * String with SQL code to insert UserActivity in user_activity table in database.db.
             */
            String sql = "INSERT INTO user_activity (activityID, starttime) VALUES (?, ?)";

            /**
             * Insert activityData in database.
             */
            try (PreparedStatement prepStmt = conn.prepareStatement(sql)) {
                prepStmt.setInt(1, activity.getActivityID());
                prepStmt.setString(2, activity.getStarttime());
                prepStmt.executeUpdate();
                System.out.println("Successfully added the Activity to the Database!");
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Could not add the Activity to the Database!");
            }
        }
        return false;
    }

    /**
     * The method updated the ForeignKeys from the table User_Data.
     *
     * @param   column          todo
     * @param   columnID        todo
     * @param   hostID          unique Discord userID from host
     * @param   participantID   unique Discord userID from participant
     * @exception // TODO: 04.06.2020
     * @return  <code>true</code> wenn ;
     * *                  <code>false</code> if data could not be added.
     */
    public boolean insertForeignKey(String column, Integer columnID, String hostID, String participantID) {

        String sql = "UPDATE user_data SET " + column + " = ? WHERE userID = ? OR userID = ?";

        try (PreparedStatement prepStmt = conn.prepareStatement(sql)) {
            prepStmt.setInt(1, columnID);
            prepStmt.setString(2, hostID);
            prepStmt.setString(3, participantID);
            prepStmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Data could not be added to User!");
            return false;
        }
    }

    /**
     * Delete the desired user.
     *
     * @param   userID  unique Discord userID
     * @exception // TODO: 04.06.2020
     * @return  <code>true</code> if user deleted from database;
     * *                  <code>false</code> if user doesn´t exist.
     */
    public boolean deleteUser(String userID) {

        //SQL-Code zum Löschen aus der Datenbank
        String sql = "DELETE FROM user_data WHERE userID = ?";

        //Versucht User aus Datenbank zu löschen
        try (PreparedStatement prepStmt = conn.prepareStatement(sql)) {
            prepStmt.setString(1, userID);
            prepStmt.executeUpdate();
            System.out.println("Successfully deleted the User from the Database!");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("User does not exist!");
            return  false;
        }
    }

    /**
     * Delete a meeting. Therefore the method get the meetingID.
     *
     * @param   meetingID   unique generated meeting ID.
     * @exception // TODO: 04.06.2020
     * @return  <code>true</code> if meeting deleted from database;
     *                  <code>false</code> if meeting doesn´t exist.
     */
    public boolean deleteMeeting(int meetingID) {

        //SQL-Code zum Löschen aus der Datenbank
        String sql = "DELETE FROM meeting_data WHERE meetingID = ?";

        //Versucht Meeting aus Datenbank zu löschen
        try (PreparedStatement prepStmt = conn.prepareStatement(sql)) {
            prepStmt.setInt(1, meetingID);
            prepStmt.executeUpdate();
            System.out.println("Successfully deleted the Meeting from the Database!");
            return true;
        } catch (SQLException e) {
            System.out.println("Meeting does not exist!");
            return false;
        }
    }

    /**
     * Update the attribute [address], [interests] or [competencies] in userData(userID).
     *
     * @param   userID      unique Discord userID
     * @param   column      todo
     * @param   newValue    new entry for the database.db
     * @exception   // TODO: 04.06.2020  
     * @return  <code>true</code> if database is succesfull updated;
     *                  <code>false</code> could not update.
     */
    public boolean updateUser(String userID, String column, String newValue) {

        String sql = "UPDATE user_data SET " + column + " = ? WHERE userID = ?";

        /**
         * Update the database.
         */
        try (PreparedStatement prepStmt = conn.prepareStatement(sql)) {
            prepStmt.setString(1, newValue);
            prepStmt.setString(2, userID);
            prepStmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("User could not be updated!");
            return false;
        }
    }

    /**
     * Update the meetingData with the new transferred values.
     * 
     * @param meetingID     unique generated meeting ID.
     * @param column        [@Participant] [starttime] [endtime] [message]
     * @param newValue      new entry for the database.db
     * @exception   // TODO: 04.06.2020                       
     * @return  <code>true</code> If meeting update the meeting;
     *                  <code>false</code> Meeting could not be updated.
     */
    public boolean updateMeeting(int meetingID, String column, Object newValue) {
        
        String sql = "UPDATE meeting_data SET " + column + " = ? WHERE meetingID = ?";
        
        try (PreparedStatement prepStmt = conn.prepareStatement(sql)) {
            prepStmt.setObject(1, newValue);
            prepStmt.setInt(2, meetingID);
            prepStmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Meeting could not be updated!");
            return false;
        }
    }

    /**
     * <code>ReturnData(String userID)</code> method gets the userData from Database.db and
     * return them as an object[].
     *
     * @param   userID  unique Discord userID.
     * @exception   // TODO: 04.06.2020
     * @return  data    contains user data instance.
     */
    public Object[] returnData(String userID) {

        String sql = "SELECT * FROM user_data WHERE userID = ?";

        Object[] data = new Object[3];

        try (PreparedStatement prepStmt = conn.prepareStatement(sql)) {
            prepStmt.setString(1, userID);
            ResultSet rsData = prepStmt.executeQuery();

            data[0] = rsData.getString("address");
            data[1] = rsData.getString("interests");
            data[2] = rsData.getString("competencies");
        } catch (SQLException e) {
            System.out.println("Could not get User data properly!");
        }
        return data;
    }

    /**
     * Search the userID in <code>database.db</code> to check if the user is registered.
     *
     * @param       userID          unique Discord userID.
     * @exception   SQLException    database access denied.
     * @return  <code>true</code> if there is a user entry;
     *                  <code>false</code> check for user failed(catch) / no user entry (return rs.getBoolean(0);).
     */
    public boolean registeredCheck(String userID) {

        /**
         * SQL Code: Check if there is an entry of userID in the database.
         */
        String sql = "SELECT COUNT (*) FROM user_data WHERE userID = ?";

        try (PreparedStatement prepStmt = conn.prepareStatement(sql)) {
            prepStmt.setString(1, userID);

            rs = prepStmt.executeQuery();

            if (rs.next()) {
                return rs.getBoolean(1);
            }
        } catch (SQLException e) {
            System.out.println("Failed to check for User!");
            return false;
        }
        return true;
    }
}
