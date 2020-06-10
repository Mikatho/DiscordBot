package com.discord.bot;

import com.discord.bot.data.UserData;
import org.graalvm.compiler.hotspot.replacements.AssertionSnippets;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;

import static org.junit.Assert.*;

public class UserManagementTest {

    UserManagement testUserManagement = new UserManagement();
    UserData testUserData = new UserData("99699");
    DatabaseManagement dbManagement = new DatabaseManagement();

    @Test
    public void getINSTANCETest() {

        UserManagement testUserManagement2 = new UserManagement();
        Assert.assertNotEquals(testUserManagement.getINSTANCE(), testUserManagement2);
    }

    @Test
    public void register() throws IOException, SQLException {

        //UserManagement testUserManagement = new UserManagement();
        //UserData testUserData = new UserData("42");

        dbManagement.connect();
        dbManagement.clear();

        //dbManagement.insertUser(testUserData);
        //testUserManagement.register("42");
        //Assert.assertTrue(testUserManagement.register("42"));

        //UserData tempUser = new UserData("42");
        //testUserManagement.register("42");
        //dbManager.insertUser(tempUser);
    }

    @Test
    public void delete() throws IOException, SQLException {

        dbManagement.connect();
        dbManagement.clear();

        dbManagement.insertUser(testUserData);
        Assert.assertTrue( dbManagement.deleteUser("99699") );

        dbManagement.clear();
        dbManagement.disconnect();
    }

    @Test
    public void update() throws SQLException, IOException {

        dbManagement.connect();
        dbManagement.clear();

        testUserData.setInterests("interesting");
        testUserData.setCompetencies("competence");
        testUserData.setAddress("avenue");

        UserData testUserManagement2 = testUserData;

        dbManagement.insertUser(testUserData);
/*        dbManagement.insertUser(testUserManagement2);

        dbManagement.updateUser("99699", "address", "street");

        Assert.assertNotEquals(dbManagement.returnDataUser("99699"), testUserManagement2);
        Vergleich beider adressen !!!!!!!!!!!
*/
        dbManagement.clear();
        dbManagement.disconnect();
    }

    @Test
    public void registerSearchTest() throws IOException, SQLException {

        UserManagement testUserManagement = new UserManagement();

        UserData testUserData = new UserData("123");
        DatabaseManagement dbManagement = new DatabaseManagement();
        dbManagement.connect();
        dbManagement.clear();

        dbManagement.insertUser(testUserData);
        Assert.assertTrue(dbManagement.registeredCheck("123") );

        dbManagement.deleteUser("123");
        Assert.assertFalse(dbManagement.registeredCheck("123"));

  //      Object[] receivedData = testUserManagement.search("123");

       // Assert.assertEquals(testUserData.getUserID(), receivedData[]  );

    }

    @Test
    public void startActivity() {
    }

    @Test
    public void stopActivity() {
    }

    @Test
    public void userIsRegistered() {
    }

    @Test
    public void googleCalendarID() {
    }

    @Test
    public void googleCalendarLink() {
    }
}