package com.discord.bot;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

public class TestMain {

    public static final String TAG = "TESTMAIN: ";

    private static GoogleCalendarManager gCalendar = GoogleCalendarManager.getInstance();

    public static void main(String[] args) throws IOException {

        //gCalendar.createCalendar("Patrick");

        gCalendar.addUserToUserlist("Patrick", "m7rongpbcdqo5af10sl2d5nmig@group.calendar.google.com");

        //Create details for a new Event in a Users Calendar
        String testUserName = "Patrick";
        String name = "testevent";
        String testlocation = "FH Kiel";
        String testdesc = "Dies ist ein Test Event";
        String start = "2020-06-01T09:00:00";
        String end = "2020-06-01T17:00:00";

        gCalendar.createNewEvent(gCalendar.getUserCalLink(testUserName), name, testlocation, testdesc, start, end);

        // get calendar id of a user (Only works if the users get initialized from the database)
        System.out.println(TAG + gCalendar.getUserCalLink(testUserName));

        // get the public link to the calendar of a user
        System.out.println(TAG + gCalendar.getPublicCalendarLink(testUserName));

    }



}
