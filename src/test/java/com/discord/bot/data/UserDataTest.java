package com.discord.bot.data;

import org.junit.Assert;
import org.junit.Test;


public class UserDataTest {

    UserData testUser = new UserData("1234");

    String[] testInterests = {"lala", "lili"};
    String[] testCompetencies = {"lala", "lulu"};
    String testAddress = "straße 22";

    @Test
    public void userSetterGetterTest() {

        testUser.setInterests(testInterests);
        testUser.setCompetencies(testCompetencies);
        testUser.setAddress(testAddress);

        Assert.assertEquals(testUser.getInterests()[0],"lala");
        Assert.assertEquals(testUser.getInterests()[1],"lili");

        Assert.assertEquals(testUser.getCompetencies()[0],"lala");
        Assert.assertEquals(testUser.getCompetencies()[1],"lulu");

        Assert.assertEquals(testUser.getAddress(),"straße 22");
    }

}

