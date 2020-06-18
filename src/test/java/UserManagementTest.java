import com.discord.bot.external_functions.DatabaseManagement;
import com.discord.bot.user.UserManagement;
import com.discord.bot.user.UserData;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;

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
    public void update() throws SQLException, IOException {

        dbManagement.connect();

        UserData testUserData2 = new UserData("996996");
        testUserData2.setInterests("interesting");
        testUserData2.setCompetencies("competence");
        testUserData2.setAddress("avenue");

        dbManagement.updateUser("99699", "address", "street");
        dbManagement.disconnect();
    }

    @Test
    public void registerSearchTest() throws IOException, SQLException {

        UserManagement testUserManagement = new UserManagement();

        UserData testUserData = new UserData("123");
        DatabaseManagement dbManagement = new DatabaseManagement();
        dbManagement.connect();

        dbManagement.deleteUser("123");
        Assert.assertFalse(dbManagement.registeredCheck("123"));
    }

}