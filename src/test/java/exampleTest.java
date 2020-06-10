import com.discord.bot.data.UserData;
import org.junit.Assert;
import org.junit.Test;


public class exampleTest {

    UserData user = new UserData("1234");


    @Test
    public void simpleCheck() {

        String[] interests = new String[1];
        interests[0] = "blubbern";
      //  user.setInterests(interests);


  //      Assert.assertEquals(user.getInterests()[0],"blubbern");
    }
}
