package com.discord.bot.external_functions;

import com.discord.bot.meeting.MeetingData;
import com.discord.bot.meeting.MeetingManagement;
import com.discord.bot.user.UserData;
import com.discord.bot.user.UserManagement;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;


/**
 * Contains all methods which access the database.
 * [clear], [connect], [disconnect], [createTable], [insert], [insertForeignKey], [delete user],
 * [delete meeting], [update user], [update meeting], [returnData], [registeredCheck]
 * Execute all function calls from <code>UserManagement</code> and <code>MeetingManagement</code>
 *
 * @author L2G4
 * @version %I%, %G%
 * @see UserManagement
 * @see MeetingManagement
 * @see UserManagement#getINSTANCE()
 * @see MeetingManagement#getINSTANCE()
 * @see com.discord.bot
 * @since 1.0
 */
public class DatabaseManagement {

    //creates INSTANCE of Class
    private static final DatabaseManagement INSTANCE = new DatabaseManagement();

    private Connection conn;
    private Statement stmt;
    private ResultSet rs;
    private PreparedStatement prepStmt;

    /**
     * This method return an instance of the DatabaseManagement object.
     *
     * @return INSTANCE    instance of the DatabaseManagement object
     */
    public static DatabaseManagement getINSTANCE() {
        return INSTANCE;
    }

    /**
     * Delete all entries in database.
     */
    public void clear() throws SQLException {

        String foreignMeetingUser = "DELETE FROM meetings_of_user;";
        String user = "DELETE FROM user_data;";
        String meeting = "DELETE FROM meeting_data;";

        stmt.execute(foreignMeetingUser);
        stmt.execute(user);
        stmt.execute(meeting);
    }

    /**
     * Check if the database.db still exist and connect to the database.
     * If the file do not exist it will create a new file.
     *
     * @throws SQLException when there is a problem with the database connection
     * @throws IOException  when there is a problem with the access of the file
     */
    public void connect() throws SQLException, IOException {

        conn = null;

        File file = new File("database.db");
        if (!file.exists()) {
            file.createNewFile();
        }

        String url = "jdbc:sqlite:" + file.getPath();
        conn = DriverManager.getConnection(url);

        stmt = conn.createStatement();
        createTable("user_data", "userID text PRIMARY KEY", "address text", "interests text", "competencies text", "gCalendarLink text");
        createTable("meeting_data", "meetingID integer PRIMARY KEY AUTOINCREMENT", "hostID text NOT NULL", "participantID text NOT NULL", "starttime INTEGER NOT NULL", "endtime INTEGER NOT NULL", "message text");
        createTable("meetings_of_user", "meetingID integer NOT NULL", "userID text NOT NULL", "FOREIGN KEY (meetingID) REFERENCES meeting_data (meetingID)", "FOREIGN KEY (userID) REFERENCES user_data (userID)");
    }

    /**
     * Close the connection to the database.
     *
     @throws SQLException when there is a problem with the database connection
     */
    public void disconnect() throws SQLException {

        if (prepStmt != null) {
            prepStmt.close();
        }

        if (rs != null) {
            rs.close();
        }
        if (stmt != null) {
            stmt.close();
        }
        if (conn != null) {
            conn.close();
        }
    }

    /**
     * Create tables by transfer SQL Code as String to create columns.
     *
     * @param strings SQL
     @throws SQLException when there is a problem with the database connection
     */
    public void createTable(String... strings) throws SQLException {

        StringJoiner joiner = new StringJoiner(",");
        for (int i = 1; i < strings.length; i++) {
            joiner.add(strings[i]);
        }

        String sql = String.format("CREATE TABLE IF NOT EXISTS %s (%s);", strings[0], joiner.toString());

        stmt.execute(sql);
    }

    /**
     * Inserts user into database
     *
     * @param user  user to insert
     * @throws SQLException when there is a problem with the database connection
     */
    public void insertUser(UserData user) throws SQLException {

        // SQL: insert user in database.
        String sql = "INSERT INTO user_data (userID) VALUES (?)";

        prepStmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        prepStmt.setString(1, user.getUserID());
        prepStmt.executeUpdate();
    }

    /**
     * Inserts meeting into database
     *
     * @param meeting   meeting to insert
     * @return meetingID
     * @throws SQLException when there is a problem with the database connection
     */
    public int insertMeeting(MeetingData meeting) throws SQLException {

        // SQL: insert meeting in database.
        String sql = "INSERT INTO meeting_data (hostID, participantID, startTime, endTime, message) VALUES (?, ?, ?, ?, ?)";

        prepStmt = conn.prepareStatement(sql);
        prepStmt.setString(1, meeting.getHostID());
        prepStmt.setString(2, meeting.getParticipantID());
        prepStmt.setLong(3, meeting.getStarttime());
        prepStmt.setLong(4, meeting.getEndtime());
        prepStmt.setString(5, meeting.getMessage());
        prepStmt.executeUpdate();

        rs = stmt.getGeneratedKeys();

        // Stores ID of inserted meeting
        if (rs.next()) {
            int meetingID = rs.getInt(1);

            insertForeignKey(meetingID, meeting.getHostID(), meeting.getParticipantID());
            return meetingID;
        }
        return 0;
    }

    /**
     * Searchs for earliest possible meeting times
     *
     * @param userID    user to search
     * @param starttime start of meeting
     * @param endtime   end of meeting
     * @param duration  duration of meeting
     * @return meetingtimes
     * @throws SQLException when there is a problem with the database connection
     */
    public long[] findEarliestPossibleMeetingtimes(String userID, long starttime, long endtime, int duration) throws SQLException {

        // Length of pause between meetings
        final int BREAKTIME_IN_SECONDS = 300000;

        long[] meetingtimes = new long[2];

        ArrayList<Long> starttimeList = new ArrayList<>();
        ArrayList<Long> endtimeList = new ArrayList<>();

        // Searchs and sorts all meeting times of the period
        String meetings = "SELECT starttime, endtime FROM meeting_data WHERE (? IN (hostID, participantID)) AND ((starttime BETWEEN ? AND ?) OR (endtime BETWEEN ? AND ?)) ORDER BY starttime ASC";

        // Checks if a meeting is running during the period
        String runningMeeting = "SELECT meetingID FROM meeting_data WHERE (? IN (hostID, participantID)) AND (starttime < ? AND endtime > ?)";

        prepStmt = conn.prepareStatement(meetings);
        prepStmt.setString(1, userID);
        prepStmt.setLong(2, starttime - BREAKTIME_IN_SECONDS);
        prepStmt.setLong(3, endtime + BREAKTIME_IN_SECONDS);
        prepStmt.setLong(4, starttime - BREAKTIME_IN_SECONDS);
        prepStmt.setLong(5, endtime + BREAKTIME_IN_SECONDS);
        rs = prepStmt.executeQuery();

        while (rs.next()) {
            starttimeList.add(rs.getLong("starttime"));
            endtimeList.add(rs.getLong("endtime"));
        }

        // If there is no meeting in the period
        if (starttimeList.isEmpty() && endtimeList.isEmpty()) {

            prepStmt = conn.prepareStatement(runningMeeting);
            prepStmt.setString(1, userID);
            prepStmt.setLong(2, starttime - BREAKTIME_IN_SECONDS);
            prepStmt.setLong(3, endtime + BREAKTIME_IN_SECONDS);
            rs = prepStmt.executeQuery();

            // Checks if a meeting is running during the period
            if (!rs.next()) {
                meetingtimes[0] = starttime;
                meetingtimes[1] = meetingtimes[0] + duration;
            }

            return meetingtimes;
        }

        // If there is exactly one meeting during the period
        if (starttimeList.size() == 1) {

            if (endtimeList.get(0) < starttime) {

                meetingtimes[0] = starttime + BREAKTIME_IN_SECONDS;
                meetingtimes[1] = meetingtimes[0] + duration;
            } else if ((starttimeList.get(0) - starttime) >= duration) {

                meetingtimes[0] = starttime;
                meetingtimes[1] = meetingtimes[0] + duration;
            } else if ((endtime - endtimeList.get(0)) >= (duration + BREAKTIME_IN_SECONDS)) {

                meetingtimes[0] = endtimeList.get(0) + BREAKTIME_IN_SECONDS;
                meetingtimes[1] = meetingtimes[0] + duration;
            } else if ((endtime - endtimeList.get(0)) >= duration) {

                meetingtimes[0] = endtimeList.get(0);
                meetingtimes[1] = meetingtimes[0] + duration;
            }
            return meetingtimes;
        }

        // Checks if there is enough time in between the meetings
        for (int i = 1; i < starttimeList.size(); i++) {

            // Checks if there is time for a pause
            if ((starttimeList.get(i) - endtimeList.get(i - 1)) >= (duration + 2 * BREAKTIME_IN_SECONDS)) {

                meetingtimes[0] = endtimeList.get(i - 1) + BREAKTIME_IN_SECONDS;
                meetingtimes[1] = meetingtimes[0] + duration;
                return meetingtimes;
            }

            // Checks edge cases
            if (endtimeList.get(i - 1) < starttime) {

                if ((starttimeList.get(i) - starttime) >= duration) {

                    meetingtimes[0] = starttime;
                    meetingtimes[1] = meetingtimes[0] + duration;
                    return meetingtimes;
                }
            } else if (starttimeList.get(i) > endtime) {

                if ((endtime - endtimeList.get(i - 1)) >= duration) {

                    meetingtimes[0] = endtimeList.get(i - 1);
                    meetingtimes[1] = meetingtimes[0] + duration;
                    return meetingtimes;
                }
            } else {

                if ((starttimeList.get(i) - endtimeList.get(i - 1)) >= duration) {

                    meetingtimes[0] = endtimeList.get(i - 1);
                    meetingtimes[1] = meetingtimes[0] + duration;
                    return meetingtimes;
                }
            }
        }

        // If no period for a meeting is found
        if ((endtime - endtimeList.get(endtimeList.size() - 1)) >= (duration + BREAKTIME_IN_SECONDS)) {

            meetingtimes[0] = endtimeList.get(endtimeList.size() - 1) + BREAKTIME_IN_SECONDS;
            meetingtimes[1] = meetingtimes[0] + duration;
        } else if ((endtime - endtimeList.get(endtimeList.size() - 1)) >= duration) {

            meetingtimes[0] = endtimeList.get(endtimeList.size() - 1);
            meetingtimes[1] = meetingtimes[0] + duration;
        }
        return meetingtimes;
    }

    /**
     * The method updated the ForeignKeys from the table User_Data.
     *
     * @param columnID      column of data
     * @param hostID        unique Discord userID from host
     * @param participantID unique Discord userID from participant
     *
     * @throws SQLException when there is a problem with the database connection
     */
    private void insertForeignKey(Integer columnID, String hostID, String participantID) throws SQLException {

        String sql;

        if (hostID.equals(participantID)) {

            sql = "INSERT INTO meetings_of_user (meetingID, userID) VALUES (?, ?)";
        } else {

            sql = "INSERT INTO meetings_of_user (meetingID, userID) VALUES (?, ?), (?, ?)";
        }

        prepStmt = conn.prepareStatement(sql);
        prepStmt.setInt(1, columnID);
        prepStmt.setString(2, hostID);

        if (!hostID.equals(participantID)) {

            prepStmt.setInt(3, columnID);
            prepStmt.setString(4, participantID);
        }
        prepStmt.executeUpdate();
    }

    /**
     * Deletes foreign key
     *
     * @param userID user to delete
     */
    private void deleteForeignKey(String userID) throws SQLException {

        // SQL: delete ForeignKey in database.
        String sql = "DELETE FROM meetings_of_user WHERE userID = ?";

        prepStmt = conn.prepareStatement(sql);
        prepStmt.setString(1, userID);
        prepStmt.executeUpdate();
    }

    /**
     * Overload method because meetingID can equal userID
     *
     * @param meetingID meeting to delete
     */
    private void deleteForeignKey(int meetingID) throws SQLException {

        String sql = "DELETE FROM meetings_of_user WHERE meetingID = ?";

        prepStmt = conn.prepareStatement(sql);
        prepStmt.setInt(1, meetingID);
        prepStmt.executeUpdate();
    }

    /**
     * Delete the desired user.
     *
     * @param userID unique Discord userID
     * @return <code>true</code> if user deleted from database;
     * *                  <code>false</code> if user doesn´t exist.
     * @throws SQLException when there is a problem with the database connection
     */
    public boolean deleteUser(String userID) throws SQLException {

        deleteForeignKey(userID);

        // SQL: delete user in database.
        String sql = "DELETE FROM user_data WHERE userID = ?";

        prepStmt = conn.prepareStatement(sql);
        prepStmt.setString(1, userID);
        prepStmt.executeUpdate();
        return true;
    }

    /**
     * Delete a meeting. Therefore the method get the meetingID.
     *
     * @param meetingID unique generated meeting ID.
     * @return <code>true</code> if meeting deleted from database;
     * <code>false</code> if meeting doesn´t exist.
     * @throws SQLException when there is a problem with the database connection
     */
    public boolean deleteMeeting(int meetingID) throws SQLException {

        deleteForeignKey(meetingID);

        String sql = "DELETE FROM meeting_data WHERE meetingID = ?";

        prepStmt = conn.prepareStatement(sql);
        prepStmt.setInt(1, meetingID);
        prepStmt.executeUpdate();
        return true;
    }

    /**
     * Update the attribute [address], [interests] or [competencies] in userData(userID).
     *
     * @param userID   unique Discord userID
     * @param column   column to be updated
     * @param newValue new entry for the database.db
     * @throws SQLException when there is a problem with the database connection
     */
    public void updateUser(String userID, String column, String newValue) throws SQLException {

        // SQL: Update of user attributes.
        String sql = "UPDATE user_data SET " + column + " = ? WHERE userID = ?";

        prepStmt = conn.prepareStatement(sql);
        prepStmt.setString(1, newValue);
        prepStmt.setString(2, userID);
        prepStmt.executeUpdate();
    }

    /**
     * Update the meetingData with the new transferred values.
     *
     * @param meetingID unique generated meeting ID.
     * @param column    [@Participant] [starttime] [endtime] [message]
     * @param newValue  new entry for the database.db
     * @throws SQLException when there is a problem with the database connection
     */
    public void updateMeeting(int meetingID, String column, Object newValue, String hostID) throws SQLException {

        // SQL: Update of meeting attributes.
        String sqlUpdate = "UPDATE meeting_data SET " + column + " = ? WHERE meetingID = ?";

        prepStmt = conn.prepareStatement(sqlUpdate);
        prepStmt.setObject(1, newValue);
        prepStmt.setInt(2, meetingID);
        prepStmt.executeUpdate();

        // SQL: Update of foreign key
        if (column.equals("participant")) {

            String sqlUpdateForeign = "UPDATE meetings_of_user SET userID = ? WHERE meetingID = ? AND userID != ?";

            prepStmt = conn.prepareStatement(sqlUpdateForeign);
            prepStmt.setObject(1, newValue);
            prepStmt.setInt(2, meetingID);
            prepStmt.setString(3, hostID);
            prepStmt.executeUpdate();
        }
    }

    /**
     * <code>returnDataUser(String userID)</code> method gets the userData from Database.db and
     * return them as an object[].
     *
     * @param userID unique Discord userID.
     * @return data    contains user data instance.
     * @throws SQLException Unable to access database.
     */
    public UserData returnUser(String userID) throws SQLException {

        // SQL: get userData.
        String sql = "SELECT * FROM user_data WHERE userID = ?";


        UserData user = new UserData(userID);

        prepStmt = conn.prepareStatement(sql);
        prepStmt.setString(1, userID);
        rs = prepStmt.executeQuery();

        user.setAddress(rs.getString("address"));
        user.setInterests(rs.getString("interests"));
        user.setCompetencies(rs.getString("competencies"));
        user.setgCalendarLink(rs.getString("gCalendarLink"));

        return user;
    }

    /**
     * <code>returnDataMeeting(String userID)</code> method gets the meetingData from Database.db and
     * return them as an object[].
     *
     * @param meetingID unique meeting ID.
     * @return data    Object. Contains all meetingData.
     * @throws SQLException Unable to access database.
     */
    public MeetingData returnMeeting(int meetingID) throws SQLException {

        // SQL: get meetingData.
        String sql = "SELECT * FROM meeting_data WHERE meetingID = ?";

        prepStmt = conn.prepareStatement(sql);
        prepStmt.setInt(1, meetingID);
        rs = prepStmt.executeQuery();

        MeetingData meeting = new MeetingData(rs.getString("hostID"), rs.getString("participantID"), rs.getLong("starttime"), rs.getLong("endtime"), rs.getString("message"));

        meeting.setMeetingID(meetingID);

        return meeting;
    }

    /**
     * Gets all meetings of the user
     *
     * @param userID user to search meetings for
     * @return List with all meetings
     * @throws SQLException Unable to access database.
     */
    public List<MeetingData> returnMultipleMeetings(String userID) throws SQLException {

        ArrayList<MeetingData> meetingList = new ArrayList<>();

        long currentEpoch = System.currentTimeMillis();

        // SQL: get all meetings of user
        String sql = "SELECT * FROM meeting_data WHERE (? IN (hostID, participantID)) AND starttime > ? ORDER BY starttime ASC";

        prepStmt = conn.prepareStatement(sql);
        prepStmt.setString(1, userID);
        prepStmt.setLong(2, currentEpoch);
        rs = prepStmt.executeQuery();

        //Fetches all meeting datas, puts them into MeetingData-Objects and then into a list
        while (rs.next()) {
            MeetingData tempData = new MeetingData(rs.getString("hostID"), rs.getString("participantID"), rs.getLong("starttime"), rs.getLong("endtime"), rs.getString("message"));
            tempData.setMeetingID(rs.getInt("meetingID"));

            meetingList.add(tempData);
        }
        return meetingList;
    }

    /**
     * Gets all meetings of the user until a certain time
     * Overloaded with certain time
     *
     * @param userID user to search meetings for
     * @return List with all meetings
     * @throws SQLException Unable to access database.
     */
    public List<MeetingData> returnMultipleMeetings(String userID, long duration) throws SQLException {

        ArrayList<MeetingData> meetingList = new ArrayList<>();

        long currentEpoch = System.currentTimeMillis();

        long durationEpoch = currentEpoch + duration;

        // SQL: get all meetings of user
        String sql = "SELECT * FROM meeting_data WHERE (? IN (hostID, participantID)) AND (? < starttime AND starttime < ?) ORDER BY starttime ASC";

        prepStmt = conn.prepareStatement(sql);
        prepStmt.setString(1, userID);
        prepStmt.setLong(2, currentEpoch);
        prepStmt.setLong(3, durationEpoch);
        rs = prepStmt.executeQuery();

        //Fetches all meeting datas, puts them into MeetingData-Objects and then into a list
        while (rs.next()) {
            MeetingData tempData = new MeetingData(rs.getString("hostID"), rs.getString("participantID"), rs.getLong("starttime"), rs.getLong("endtime"), rs.getString("message"));
            tempData.setMeetingID(rs.getInt("meetingID"));

            meetingList.add(tempData);
        }
        return meetingList;
    }

    /**
     * Search the userID in <code>database.db</code> to check if the user is registered.
     *
     * @param userID unique Discord userID.
     * @return <code>true</code> if there is a user entry;
     * <code>false</code> check for user failed(catch) / no user entry (return rs.getBoolean(0);).
     * @throws SQLException database access denied.
     */
    public boolean registeredCheck(String userID) throws SQLException {

        // SQL Code:Check if there is an entry of userID in the database.
        String sql = "SELECT COUNT (*) FROM user_data WHERE userID = ?";

        prepStmt = conn.prepareStatement(sql);
        prepStmt.setString(1, userID);
        rs = prepStmt.executeQuery();

        if (rs.next()) {
            return rs.getBoolean(1);
        }
        return false;
    }

    /**
     * Check if user is authorised.
     *
     * @param meetingID meeting to check
     * @param userID    user to check
     * @return if user is authorized
     * @throws SQLException when there is a problem with the database connection
     */
    public boolean authorizationCheck(int meetingID, String userID) throws SQLException {

        // SQL: Check authorization of user.
        String sql = "SELECT hostID FROM meeting_data WHERE meetingID = ?";

        prepStmt = conn.prepareStatement(sql);
        prepStmt.setInt(1, meetingID);
        rs = prepStmt.executeQuery();

        return rs.getString(1).equals(userID);
    }
}
