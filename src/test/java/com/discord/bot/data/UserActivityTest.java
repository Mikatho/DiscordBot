package com.discord.bot.data;

import org.junit.Assert;
import org.junit.Test;

public class UserActivityTest  {

    UserActivity userActivityTest = new UserActivity(123);

    @Test
    public void userActivitySetterGetterTest() {

        Assert.assertEquals(userActivityTest.getStarttime(),123);

        userActivityTest.setEndtime(456);
        Assert.assertEquals(userActivityTest.getEndtime(),456);
    }

    @Test
    public void equalsForDiffObjectsTest(){
        UserActivity testActivity_1 = new UserActivity(1234);
        UserActivity testActivity_2 = new UserActivity(5678);

        Assert.assertNotEquals(testActivity_1, testActivity_2);
    }
}