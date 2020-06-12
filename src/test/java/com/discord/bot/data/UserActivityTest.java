package com.discord.bot.data;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class UserActivityTest  {

    private static UserActivity userActivityTest;
    private static UserActivity userActivityTest2;

    private static int activityID;
    private static long starttime;
    private static long endtime;


    @BeforeClass
    public static void create() {

        /**
         * Create unequal objects
         */
        userActivityTest = new UserActivity(1234);
        userActivityTest2 = new UserActivity(5678);

        activityID = 1;
        starttime = 22;
    }

    @Test
    public void userActivitySetterGetterTest() {

        Assert.assertEquals(userActivityTest.getStarttime(),1234);
        Assert.assertEquals(userActivityTest2.getStarttime(),5678);

        userActivityTest.setEndtime(456);
        userActivityTest2.setEndtime(6786);
        Assert.assertEquals(userActivityTest.getEndtime(),456);
        Assert.assertEquals(userActivityTest2.getEndtime(),6786);

        Assert.assertNotNull(userActivityTest.getStarttime());
        Assert.assertNotNull(userActivityTest.getActivityID());
    }

    @Test
    public void equalsForDiffObjectsTest(){

        Assert.assertNotEquals(userActivityTest, userActivityTest2);
    }
}