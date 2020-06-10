package com.discord.bot.data;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.mockito.Mockito;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;


public class UserDataTest {

    UserData testUser = new UserData("1234");

    String testInterests = "lala";
    String testCompetencies = "lulu";
    String testAddress = "straße 22";

    @Test
    public void userSetterGetterTest() {

        testUser.setInterests(testInterests);
        testUser.setCompetencies(testCompetencies);
        testUser.setAddress(testAddress);

        Assert.assertEquals(testUser.getInterests(),"lala");
        Assert.assertEquals(testUser.getCompetencies(),"lulu");
        Assert.assertEquals(testUser.getAddress(),"straße 22");

        testUser.setInterests("asd");
        testUser.setCompetencies("qwe");
        testUser.setAddress("straße 99");

        Assert.assertEquals(testUser.getInterests(),"asd");
        Assert.assertEquals(testUser.getCompetencies(),"qwe");
        Assert.assertEquals(testUser.getAddress(),"straße 99");
    }

    @Test
    public void equalsForDiffObjectsTest(){
        UserData testUser_1 = new UserData("1234");
        UserData testUser_2 = new UserData("5678");

        Assert.assertNotEquals(testUser_1, testUser_2);
    }

}

