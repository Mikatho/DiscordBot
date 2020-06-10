package com.discord.bot;

import com.discord.bot.data.UserData;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import static org.junit.Assert.*;

public class DatabaseManagementTest {

    @Test
    public void getINSTANCE() {

        DatabaseManagement dbManagement_1 = new DatabaseManagement();
        DatabaseManagement dbManagement_2 = new DatabaseManagement();

        Assert.assertNotEquals(dbManagement_1.getINSTANCE(), dbManagement_2);
    }

    @Test
    public void clear() throws IOException, SQLException {
        UserData testUserData = new UserData("0112358");
        DatabaseManagement dbManagement = new DatabaseManagement();
        dbManagement.connect();
        dbManagement.clear();

        Assert.assertFalse(dbManagement.registeredCheck("0112358"));

        dbManagement.insertUser(testUserData);
        Assert.assertTrue(dbManagement.registeredCheck("0112358") );

        dbManagement.clear();
        Assert.assertFalse(dbManagement.registeredCheck("0112358"));

        dbManagement.deleteUser("0112358");
        dbManagement.disconnect();
    }

    @Test
    public void connect() throws IOException, SQLException {
        DatabaseManagement dbManagement = new DatabaseManagement();
/*        File fileTest = new File("databaseTest.db");

        Assert.assertFalse(fileTest.exists());
*/
        dbManagement.connect();
    }

    @Test
    public void disconnect() throws IOException, SQLException {

    }

    @Test
    public void createTable() {
    }

    @Test
    public void insertUser() throws IOException, SQLException {
        UserData testUserData = new UserData("0112358");
        DatabaseManagement dbManagement = new DatabaseManagement();
        dbManagement.connect();
        dbManagement.clear();

        Assert.assertFalse(dbManagement.registeredCheck("0112358"));

        dbManagement.insertUser(testUserData);
        Assert.assertTrue(dbManagement.registeredCheck("0112358") );

        dbManagement.deleteUser("0112358");
        dbManagement.disconnect();
    }

    @Test
    public void insertMeeting() {
    }

    @Test
    public void insertActivity() {
    }

    @Test
    public void findEarliestPossibleMeetingtimes() {
    }

    @Test
    public void deleteUser() {
    }

    @Test
    public void deleteMeeting() {
    }

    @Test
    public void updateUser() {
    }

    @Test
    public void updateMeeting() {
    }

    @Test
    public void returnDataUser() throws IOException, SQLException {

        UserData testUserData = new UserData("42");
        testUserData.setInterests("interesting");
        testUserData.setCompetencies("competence");
        testUserData.setAddress("avenue");

        DatabaseManagement dbManagement = new DatabaseManagement();
        dbManagement.connect();
        dbManagement.clear();

 //       dbManagement.insertUser(testUserData);
 //       Object[] receivedData = dbManagement.returnDataUser("42");

 //       Assert.assertTrue( dbManagement.registeredCheck("42") );
//        Assert.assertNotNull(receivedData);
//        Assert.assertNull( receivedData[0] );
        //Assert.assertNotEquals(receivedData[1], testUserData.getInterests());
        //Assert.assertEquals(receivedData[1], testUserData.getInterests());

        dbManagement.deleteUser("42");
        dbManagement.disconnect();
    }

    @Test
    public void returnDataMeeting() {
    }

    @Test
    public void registeredCheck() throws IOException, SQLException {
        UserData testUserData = new UserData("42");
        DatabaseManagement dbManagement = new DatabaseManagement();
        dbManagement.connect();
        dbManagement.clear();

        Assert.assertFalse(dbManagement.registeredCheck("42"));

        dbManagement.insertUser(testUserData);
        Assert.assertTrue(dbManagement.registeredCheck("42"));
        Assert.assertFalse(dbManagement.registeredCheck("43"));

        dbManagement.deleteUser("42");
        dbManagement.disconnect();
    }

    @Test
    public void authorizationCheck() {
    }
}