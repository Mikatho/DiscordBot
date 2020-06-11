package com.discord.bot;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.security.GeneralSecurityException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class GoogleCalendarManagementTest {

    GoogleCalendarManagement googleCalendarManagement = GoogleCalendarManagement.getInstance();

    @Test
    public void testCreateNewEventsExceptions() {

        try {
            googleCalendarManagement.createNewEvent("0", "TestEvent", "Test", "Test", 1234567890,1234567890 );
        } catch (RuntimeException | IOException expectedException) {
            assertEquals("Google Calendar id has the wrong format",expectedException.getMessage());
        }

        try {
            googleCalendarManagement.createNewEvent("aaaaaaaaaaaaaaaaaaaaaaaaaa@group.calendar.google.com", "TestEvent", "Test", "Test", 1234,1234567890 );
        } catch (RuntimeException | IOException expectedException) {
            assertEquals("The Epoch start has a wrong format",expectedException.getMessage());
        }
    }

    @Test
    public void testGetCalendarLink() {

        String gCalendarId= "theIdOfTheGoogleCalendar";
        String expect = "https://calendar.google.com/calendar/r?cid=" + gCalendarId;
        assertEquals(expect,googleCalendarManagement.getPublicCalendarLink(gCalendarId));
    }

    @PrepareForTest(GoogleCalendarManagement.class)
    public void testStuff() {
        PowerMockito.mockStatic(GoogleCalendarManagement.class);
        BDDMockito.given(GoogleCalendarManagement);
    }


}
