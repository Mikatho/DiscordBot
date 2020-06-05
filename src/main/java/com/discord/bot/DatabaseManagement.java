package com.discord.bot;

import com.discord.bot.data.MeetingData;
import com.discord.bot.data.UserActivity;
import com.discord.bot.data.UserData;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.StringJoiner;


/**
 * Contains all methods which access the database.
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

    private static final DatabaseManagement INSTANCE = new DatabaseManagement();

    private Connection conn;
    private Statement stmt;
    private ResultSet rs;

    /**
     * This method return an instance of the DatabaseManagement object.
     *
     * @return  INSTANCE    instance of the DatabaseManagement object
     */
    public static DatabaseManagement getINSTANCE() {
        return INSTANCE;
    }

    //Löscht Daten in Datenbank
    public void clear() {

        String foreignMeetingUser = "DELETE FROM meetings_of_user;";
        String user = "DELETE FROM user_data;";
        String meeting = "DELETE FROM meeting_data;";
        String activity = "DELETE FROM activity_data;";

        try {
            stmt.execute(foreignMeetingUser);
            stmt.execute(user);
            stmt.execute(meeting);
            stmt.execute(activity);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void connect() {

        conn = null;

        try {
            //Erstellt neue Datenbank-Datei, falls nicht vorhanden
            File file = new File("database.db");
            if (!file.exists()) {
                file.createNewFile();
            }

            String url = "jdbc:sqlite:" + file.getPath();
            conn = DriverManager.getConnection(url);

            System.out.println("Connection to Database created.");

            stmt = conn.createStatement();
            createTable("user_data", "userID text PRIMARY KEY", "address text", "interests text", "competencies text", "gCalendarLink text", "activities INTEGER");
            createTable("meeting_data", "meetingID integer PRIMARY KEY AUTOINCREMENT", "hostID text NOT NULL", "participantID text NOT NULL", "starttime INTEGER NOT NULL", "endtime INTEGER NOT NULL", "message text");
            createTable("activity_data", "activityID integer PRIMARY KEY AUTOINCREMENT", "starttime INTEGER NOT NULL", "endtime INTEGER");
            createTable("meetings_of_user", "meetingID integer NOT NULL", "userID text NOT NULL", "FOREIGN KEY (meetingID) REFERENCES meeting_data (meetingID)", "FOREIGN KEY (userID) REFERENCES user_data (userID)");
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

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

    //Erstellt Tabellen, indem man SQL-Text zum Erstellen der Spalten als String übergibt
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

    public boolean insertUser(UserData user) {

        //SQL-Code zum Einfügen in die Datenbank
        String sql = "INSERT INTO user_data (userID) VALUES (?)";

        //Versucht User in Datenbank einzufügen
        try (PreparedStatement prepStmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            prepStmt.setString(1, user.getUserID());
            prepStmt.executeUpdate();
            System.out.println("Successfully added the User to the Database!");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Could not add the User to the Database!");
            return false;
        }
    }

    public int insertMeeting(MeetingData meeting) {

        //SQL-Code zum Einfügen in die Datenbank
        String sql = "INSERT INTO meeting_data (hostID, participantID, startTime, endTime, message) VALUES (?, ?, ?, ?, ?)";

        //Versucht Meeting in Datenbank einzufügen
        try (PreparedStatement prepStmt = conn.prepareStatement(sql)) {
            prepStmt.setString(1, meeting.getUserID());
            prepStmt.setString(2, meeting.getParticipantID());
            prepStmt.setLong(3, meeting.getStarttime());
            prepStmt.setLong(4, meeting.getEndtime());
            prepStmt.setString(5, meeting.getMessage());
            prepStmt.executeUpdate();

            //Holt sich automatisch generierte ID
            rs = stmt.getGeneratedKeys();

            //Speichert sich ID aus ResultSet als Integer
            if (rs.next()) {
                int meetingID = rs.getInt(1);

                //Versucht ForeignKey in User_Data zu aktualisieren
                if (!insertForeignKey(meetingID, meeting.getUserID(), meeting.getParticipantID())) {
                    return 0;
                }

                System.out.println("Successfully added the Meeting to the Database!");
                return meetingID;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Could not add the Meeting to the Database!");
            return 0;
        }
        return 0;
    }

    public boolean insertActivity (UserActivity activity) {

        //SQL-Code zum Einfügen in die Datenbank
        String sql = "INSERT INTO activity_data (activityID, starttime) VALUES (?, ?)";

        //Versucht Activity in Datenbank einzufügen
        try (PreparedStatement prepStmt = conn.prepareStatement(sql)) {
            prepStmt.setInt(1, activity.getActivityID());
            prepStmt.setLong(2, activity.getStarttime());
            prepStmt.executeUpdate();
            System.out.println("Successfully added the Activity to the Database!");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Could not add the Activity to the Database!");
            return false;
        }
    }

    public long[] findEarliestPossibleMeetingtimes(String userID, long starttime, long endtime, int duration) {

        //Länge der Pause zwischen Meetings in Sekunden
        final int BREAKTIME_IN_SECONDS = 300;

        //Variable die zurückgegeben wird
        long[] meetingtimes = new long[2];

        ArrayList<Long> starttimeList = new ArrayList<>();
        ArrayList<Long> endtimeList = new ArrayList<>();

        //Sucht alle zeitlichen Daten in dem angefragten Zeitraum heraus und sortiert diese
        String meetings = "SELECT starttime, endtime FROM meeting_data WHERE (? IN (hostID, participantID)) AND ((starttime BETWEEN ? AND ?) OR (endtime BETWEEN ? AND ?)) ORDER BY starttime ASC";
        //Sucht heraus, ob ein Meeting über den gesamten Zeitraum läuft
        String runningMeeting = "SELECT meetingID FROM meeting_data WHERE (? IN (hostID, participantID)) AND (starttime < ? AND endtime > ?)";

        try (PreparedStatement prepStmt = conn.prepareStatement(meetings)) {
            prepStmt.setString(1, userID);
            prepStmt.setLong(2, starttime - BREAKTIME_IN_SECONDS);
            prepStmt.setLong(3, endtime + BREAKTIME_IN_SECONDS);
            prepStmt.setLong(4, starttime - BREAKTIME_IN_SECONDS);
            prepStmt.setLong(5, endtime + BREAKTIME_IN_SECONDS);
            rs = prepStmt.executeQuery();

            //Fügt herausgesuchte Daten in jeweilige Liste
            while (rs.next()) {
                starttimeList.add(rs.getLong("starttime"));
                endtimeList.add(rs.getLong("endtime"));
            }

        } catch (SQLException e) {
            System.out.println("Could not get all meetings of the user properly!");
            return meetingtimes;
        }

        //Wenn es keine anderen Termine in dem Zeitraum gibt
        if (starttimeList.isEmpty() && endtimeList.isEmpty()) {

            try (PreparedStatement prepStmt = conn.prepareStatement(runningMeeting)) {
                prepStmt.setString(1, userID);
                prepStmt.setLong(2, starttime - BREAKTIME_IN_SECONDS);
                prepStmt.setLong(3, endtime + BREAKTIME_IN_SECONDS);
                rs = prepStmt.executeQuery();

                //Prüft, ob etwas zurückgegeben wurde
                if (!rs.next()) {
                    //Nichts wurde zurückgegben -> es läuft kein Meeting über den Zeitraum
                    meetingtimes[0] = starttime;
                    meetingtimes[1] = meetingtimes[0] + duration;
                }

                return meetingtimes;
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Could not test if there is one meeting during the period!");
                return meetingtimes;
            }
        }

        //Wenn es genau einen anderen Termin in dem Zeitraum gibt
        if (starttimeList.size() == 1) {

            //Prüf, in welchem Zeitraum der Termin liegt
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

        //Prüft, ob zwischen anderen Terminen genügend Zeit ist
        for (int i = 1; i < starttimeList.size(); i++) {

            //Prüft, ob eine Pause eingelegt werden kann
            if ((starttimeList.get(i) - endtimeList.get(i - 1)) >= (duration + 2 * BREAKTIME_IN_SECONDS)) {

                meetingtimes[0] = endtimeList.get(i - 1) + BREAKTIME_IN_SECONDS;
                meetingtimes[1] = meetingtimes[0] + duration;
                return meetingtimes;
            }

            //Prüft Grenzfälle
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

        //Wenn kein passender Zeitraum zwischen den Terminen gefunden wurde
        if ((endtime - endtimeList.get(endtimeList.size() - 1)) >= (duration + BREAKTIME_IN_SECONDS)) {

            meetingtimes[0] = endtimeList.get(endtimeList.size() - 1) + BREAKTIME_IN_SECONDS;
            meetingtimes[1] = meetingtimes[0] + duration;
        } else if ((endtime - endtimeList.get(endtimeList.size() - 1)) >= duration) {

            meetingtimes[0] = endtimeList.get(endtimeList.size() - 1);
            meetingtimes[1] = meetingtimes[0] + duration;
        }
        return meetingtimes;
    }

    //Funktion um ForeignKey-Daten zu erstellen
    public boolean insertForeignKey(Integer columnID, String hostID, String participantID) {

        String sql;

        //Wenn der User mit sich selber einen Termin vereinbart (damit nicht 2x dasselbe eingefügt wird)
        if (hostID.equals(participantID)) {

            sql = "INSERT INTO meetings_of_user (meetingID, userID) VALUES (?, ?)";
        } else {

            sql = "INSERT INTO meetings_of_user (meetingID, userID) VALUES (?, ?), (?, ?)";
        }

        try (PreparedStatement prepStmt = conn.prepareStatement(sql)) {
            prepStmt.setInt(1, columnID);
            prepStmt.setString(2, hostID);

            //Wenn der User mit jemand anderen einen Termin vereinbart (damit keine Exception geworfen wird)
            if (!hostID.equals(participantID)) {

                prepStmt.setInt(3, columnID);
                prepStmt.setString(4, participantID);
            }
            prepStmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Data could not be added to 'Meetings_of_User'!");
            return false;
        }
    }

    //Funktion um ForeignKey-Daten zu löschen
    public boolean deleteForeignKey(String userID) {

        String sql = "DELETE FROM meetings_of_user WHERE userID = ?";

        try (PreparedStatement prepStmt = conn.prepareStatement(sql)) {
            prepStmt.setString(1, userID);
            prepStmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("User could not be deleted from 'Meetings_of_User'!");
            return false;
        }
    }
    //Method-Overload, da meetingID = userID sein kann
    public boolean deleteForeignKey(int meetingID) {

        String sql = "DELETE FROM meetings_of_user WHERE meetingID = ?";

        try (PreparedStatement prepStmt = conn.prepareStatement(sql)) {
            prepStmt.setInt(1, meetingID);
            prepStmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Meeting could not be deleted from 'Meetings_of_User'!");
            return false;
        }
    }

    public boolean deleteUser(String userID) {

        if(!deleteForeignKey(userID)) {
            return false;
        }

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
            return false;
        }
    }

    public boolean deleteMeeting(int meetingID) {

        if(!deleteForeignKey(meetingID)) {
            return false;
        }

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

    public boolean updateUser(String userID, String column, String newValue) {

        //SQL-Code zum Updaten der Werte
        String sql = "UPDATE user_data SET " + column + " = ? WHERE userID = ?";

        //Versucht Datenbank zu updaten
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

    public boolean updateMeeting(int meetingID, String column, Object newValue, String hostID) {

        //SQL-Code zum Updaten der Werte
        String sqlUpdate = "UPDATE meeting_data SET " + column + " = ? WHERE meetingID = ?";

        //Versucht Datenbank zu updaten
        try (PreparedStatement prepStmt = conn.prepareStatement(sqlUpdate)) {
            prepStmt.setObject(1, newValue);
            prepStmt.setInt(2, meetingID);
            prepStmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Meeting could not be updated!");
            return false;
        }

        //Aktualisiert ForeignKey, wenn Participant geupdated werden soll
        if (column.equals("participant")) {

            //Updated Meeting mit der richtigen meetingID und nicht der hostID (= Meeting mit der ParticipantID)
            String sqlUpdateForeign = "UPDATE meetings_of_user SET userID = ? WHERE meetingID = ? AND userID != ?";

            try (PreparedStatement prepStmt = conn.prepareStatement(sqlUpdateForeign)) {
                prepStmt.setObject(1, newValue);
                prepStmt.setInt(2, meetingID);
                prepStmt.setString(3, hostID);
                prepStmt.executeUpdate();
            } catch (SQLException e) {
                System.out.println("Participant could not be updated in 'Meetings_Of_User'!");
                return false;
            }
        }
        return true;
    }

    //Funktion um UserData abzurufen
    public Object[] returnData(String userID) {

        String sql = "SELECT * FROM user_data WHERE userID = ?";

        Object[] data = new Object[4];

        try (PreparedStatement prepStmt = conn.prepareStatement(sql)) {
            prepStmt.setString(1, userID);
            rs = prepStmt.executeQuery();

            data[0] = rs.getString("address");
            data[1] = rs.getString("interests");
            data[2] = rs.getString("competencies");
            data[3] = rs.getString("gCalendarLink");
        } catch (SQLException e) {
            System.out.println("Could not get User data properly!");
        }
        return data;
    }

    //Prüft, ob User in Datenbank registriert ist
    public boolean registeredCheck(String userID) {

        //Sql-Code um zu gucken, ob User in Datenbank registriert ist
        String sql = "SELECT COUNT (*) FROM user_data WHERE userID = ?";

        try (PreparedStatement prepStmt = conn.prepareStatement(sql)) {
            prepStmt.setString(1, userID);
            rs = prepStmt.executeQuery();

            if (rs.next()) {
                //Return True, wenn User registriert ist, False, wenn nicht
                return rs.getBoolean(1);
            }
        } catch (SQLException e) {
            System.out.println("Failed to check for User!");
            return false;
        }
        return false;
    }

    //Prüft, ob User für die Aktion berechtigt ist
    public boolean authorizationCheck(int meetingID, String userID) {

        String sql = "SELECT hostID FROM meeting_data WHERE meetingID = ?";

        try (PreparedStatement prepStmt = conn.prepareStatement(sql)) {
            prepStmt.setInt(1, meetingID);
            rs = prepStmt.executeQuery();

            return rs.getString(1).equals(userID);
        } catch (SQLException e) {
            System.out.println("Failed to check if user exists.");
            return false;
        }
    }
}
