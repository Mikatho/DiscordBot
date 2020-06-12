package com.discord.bot.data;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.mockito.Mockito;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;


public class UserDataTest {

    private static UserData testUser;
    private static UserData testUser2;

    private static String testInterests;
    private static String testCompetencies;
    private static String testAddress;
    private static String testgCalendarLink;

    @BeforeClass
    public static void create() {

        /**
         * Create unequal objects
         */
        testUser = new UserData("1234");
        testUser2 = new UserData("5678");

        testInterests = "lala";
        testCompetencies = "lulu";
        testAddress = "straße 22";
        testgCalendarLink = "www.gCalendar.com";
    }

    @Test
    public void userSetterGetterTest() {

        testUser.setInterests(testInterests);
        testUser.setCompetencies(testCompetencies);
        testUser.setAddress(testAddress);
        testUser.setgCalendarLink(testgCalendarLink);

        Assert.assertEquals(testUser.getInterests(),"lala");
        Assert.assertEquals(testUser.getCompetencies(),"lulu");
        Assert.assertEquals(testUser.getAddress(),"straße 22");
        Assert.assertEquals(testUser.getgCalendarLink(), "www.gCalendar.com");

        testUser.setInterests("asd");
        testUser.setCompetencies("qwe");
        testUser.setAddress("straße 99");
        testUser.setgCalendarLink("www.NewgCalendar.com");

        Assert.assertEquals(testUser.getInterests(),"asd");
        Assert.assertEquals(testUser.getCompetencies(),"qwe");
        Assert.assertEquals(testUser.getAddress(),"straße 99");
        Assert.assertEquals(testUser.getgCalendarLink(), "www.NewgCalendar.com");

        Assert.assertEquals(testUser.getUserID(),"1234" );
        Assert.assertEquals(testUser2.getUserID(),"5678" );
    }

    @Test
    public void equalsForDiffObjectsTest(){
    /**
    * Check assertion
    */
        Assert.assertNotEquals(testUser, testUser2);
    }

}

