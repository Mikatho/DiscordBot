import com.discord.bot.external_functions.DatabaseManagement;
import com.discord.bot.user.UserData;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;

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

        dbManagement.insertUser(testUserData);
        Assert.assertTrue(dbManagement.registeredCheck("0112358") );

        dbManagement.deleteUser("0112358");
        dbManagement.disconnect();
    }

    @Test
    public void connect() throws IOException, SQLException {
        DatabaseManagement dbManagement = new DatabaseManagement();
        dbManagement.connect();
    }

    @Test
    public void insertUser() throws IOException, SQLException {
        UserData testUserData = new UserData("0112358");
        DatabaseManagement dbManagement = new DatabaseManagement();
        dbManagement.connect();

        Assert.assertTrue(dbManagement.registeredCheck("0112358"));

        dbManagement.deleteUser("0112358");
        dbManagement.disconnect();
    }

    @Test
    public void registeredCheck() throws IOException, SQLException {
        UserData testUserData = new UserData("42");
        DatabaseManagement dbManagement = new DatabaseManagement();
        dbManagement.connect();

        dbManagement.insertUser(testUserData);

        Assert.assertTrue(dbManagement.registeredCheck("42"));
        Assert.assertFalse(dbManagement.registeredCheck("43"));

        dbManagement.deleteUser("42");
        dbManagement.disconnect();
    }

}