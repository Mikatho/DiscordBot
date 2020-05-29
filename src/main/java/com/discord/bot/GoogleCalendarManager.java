package com.discord.bot;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.*;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class GoogleCalendarManager {
    /**
     * Tag for debugging and logging
     */
    public static final String TAG = "GoogleCalendarManager: ";

    private static final String APPLICATION_NAME = "Google Calendar API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required. If modifying these
     * scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    /**
     * The Timezones the events and calendars will be created in
     */
    private static final String BOT_TIMEZONE = "Europe/Zurich";

    /**
     * Build a Service Worker for the google Calendar API
     */
    static Calendar service;

    /**
     * Singleton implementation
     */
    private static GoogleCalendarManager INSTANCE;
    static {
        try {
            INSTANCE = new GoogleCalendarManager();
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            System.out.println(TAG + "didn't connect to Server");
        }
    }
    /**
     * @return a GoogleKalenderManager
     */
    public static GoogleCalendarManager getInstance() { return INSTANCE; };

    /**
     * private method for creating n new GoogleKalendar, only used by this class
     * @throws IOException when there is an error with the network connection
     * @throws GeneralSecurityException when there is a faulty token
     */
    private GoogleCalendarManager() throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME).build();
        System.out.println(TAG + "Google Calendar successfully connected");
    }

    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = GoogleCalendarManager.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException(TAG + "Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
                clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline").build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    /**
     * This functions creates a new Calendar for a specific user.
     * @param calendarID the ID of the Calendar that the event is added to
     * @param eventName name of the event
     * @param eventLocation name of the event location
     * @param eventDescription name of the eventDescription
     * @param startTime with following format: "2020-05-28T09:00:00"
     * @param endTime with following format: "2020-05-28T17:00:00"
     * @throws IOException when the event can't be added to a users calendar
     * @return a link to the newly created Event
     */
    private String createNewEvent(String calendarID, String eventName, String eventLocation, String eventDescription, DateTime startTime, DateTime endTime) throws IOException {
        Event event = new Event()
                .setSummary(eventName)
                .setLocation(eventLocation)
                .setDescription(eventDescription);

        // DateTime startDateTime = new DateTime("2020-05-28T09:00:00-07:00");
        EventDateTime start = new EventDateTime()
                .setDateTime(startTime)
                .setTimeZone(BOT_TIMEZONE);
        event.setStart(start);

        //DateTime endDateTime = new DateTime("2020-05-28T17:00:00-07:00");
        EventDateTime end = new EventDateTime()
                .setDateTime(endTime)
                .setTimeZone(BOT_TIMEZONE);
        event.setEnd(end);

        event = service.events().insert(calendarID, event).execute();
        System.out.printf(TAG + "A new Event was created under: %s\n", event.getHtmlLink());
        return event.getHtmlLink();
    }

    /**
     * @param userName the name of the user that belongs to tne calendar
     * @return com.google.api.services.calendar.model.Calendar
     * @throws IOException if the insertion fails
     */
    private com.google.api.services.calendar.model.Calendar createCalendar(String userName) throws IOException {
        // CREATE A NEW CALENDAR //
        String calendarName = userName + "'s Terminkalender";

        // Create a new calendar
        com.google.api.services.calendar.model.Calendar newcalendar = new com.google.api.services.calendar.model.Calendar();
        newcalendar.setSummary(calendarName);
        newcalendar.setTimeZone(BOT_TIMEZONE);

        // Insert the new calendar
        com.google.api.services.calendar.model.Calendar newCalendar = service.calendars().insert(newcalendar).execute();

        System.out.println(TAG  + "Created a new Calendar with ID: " + newCalendar.getId());

        return newcalendar;
    }

}