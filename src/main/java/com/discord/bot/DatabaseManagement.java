package com.discord.bot;

import com.discord.bot.data.MeetingData;
import com.discord.bot.data.UserActivity;
import com.discord.bot.data.UserData;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.StringJoiner;

public class DatabaseManagement {

    private static final DatabaseManagement INSTANCE = new DatabaseManagement();

    private Connection conn;
    private Statement stmt;

    public static DatabaseManagement getINSTANCE() {
        return INSTANCE;
    }

    //Löscht Daten in Datenbank
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
            createTable("user_data", "userID text PRIMARY KEY", "serverNickname text NOT NULL", "address text", "interests text", "competencies text", "gCalendarLink text", "meetings INTEGER", "activities INTEGER", "FOREIGN KEY (meetings) REFERENCES meeting_data (meetingID)", "FOREIGN KEY (activities) REFERENCES user_activity (activityID)");
            createTable("meeting_data", "meetingID integer PRIMARY KEY AUTOINCREMENT", "userID text NOT NULL", "starttime text NOT NULL", "endtime text NOT NULL", "message text");
            createTable("user_activity", "activityID integer PRIMARY KEY AUTOINCREMENT", "starttime text NOT NULL", "endtime text");
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            if (conn != null) {
                stmt.close();
                conn.close();
                System.out.println("Connection to Database cut.");
            }
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

    public boolean insert(Object obj) {

        //Prüft welches Objekt eingefügt werden soll
        if (obj instanceof UserData) {

            //Castet Objekt in richtigen Datentypen
            UserData user = (UserData) obj;

            //SQL-Code zum Einfügen in die Datenbank
            String sql = "INSERT INTO user_data (userID, serverNickname) VALUES (?, ?)";

            //Versucht User in Datenbank einzufügen
            try (PreparedStatement prepStmt = conn.prepareStatement(sql)) {
                prepStmt.setString(1, user.getUserID());
                prepStmt.setString(2, user.getServerNickname());
                prepStmt.executeUpdate();
                System.out.println("Successfully added the User to the Database!");
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Could not add the User to the Database!");
            }
        } else if (obj instanceof MeetingData) {

            //Castet Objekt in richtigen Datentypen
            MeetingData meeting = (MeetingData) obj;

            //SQL-Code zum Einfügen in die Datenbank
            String sql = "INSERT INTO meeting_data (userID, startTime, endTime, message) VALUES (?, ?, ?, ?)";

            //Versucht Meeting in Datenbank einzufügen
            try (PreparedStatement prepStmt = conn.prepareStatement(sql)) {
                prepStmt.setString(1, meeting.getUserID());
                prepStmt.setString(2, meeting.getStartTime());
                prepStmt.setString(3, meeting.getEndTime());
                prepStmt.setString(4, meeting.getMessage());
                prepStmt.executeUpdate();
                System.out.println("Successfully added the Meeting to the Database!");
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Could not add the Meeting to the Database!");
            }
        } else if (obj instanceof UserActivity) {

            //Castet Objekt in richtigen Datentypen
            UserActivity activity = (UserActivity) obj;

            //SQL-Code zum Einfügen in die Datenbank
            String sql = "INSERT INTO user_activity (activityID, starttime) VALUES (?, ?)";

            //Versucht Activity in Datenbank einzufügen
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

    public boolean updateMeeting(int meetingID, String column, String newValue) {

        //SQL-Code zum Updaten der Werte
        String sql = "UPDATE meeting_data SET " + column + " = ? WHERE meetingID = ?";

        //Versucht Datenbank zu updaten
        try (PreparedStatement prepStmt = conn.prepareStatement(sql)) {
            prepStmt.setString(1, newValue);
            prepStmt.setInt(2, meetingID);
            prepStmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Meeting could not be updated!");
            return false;
        }
    }

    //Funktion um UserData abzurufen
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
            e.printStackTrace();
        }
        return data;
    }
}
