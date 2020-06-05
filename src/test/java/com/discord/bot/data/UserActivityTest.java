package com.discord.bot.data;

import org.junit.Assert;
import org.junit.Test;

public class UserActivityTest  {

    UserActivity userActivityTest = new UserActivity("123");

    @Test
    public void userActivitySetterGetterTest() {

        Assert.assertEquals(userActivityTest.getStarttime(),"123");

        userActivityTest.setEndtime("456");

        Assert.assertEquals(userActivityTest.getEndtime(),"456");
    }
}