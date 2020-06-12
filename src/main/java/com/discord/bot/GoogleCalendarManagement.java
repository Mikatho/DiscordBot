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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is for creating and Managing public Google Calendars for all Users that work with our
 * Discord Bot.
 *
 * If you change the system this class will be called on, delete the storedCredentials in the token folder.
 *
 * This class will print out a error warning that can be ignored.
 * More information on this bug:
 * https://stackoverflow.com/questions/30634827/warning-unable-to-change-permissions-for-everybody
 *
 * @Author L2G4 SEG
 */
public class GoogleCalendarManagement {

    private static final Logger LOGGER = Logger.getLogger(GoogleCalendarManagement.class.getName());


    /**
     * Tag for debugging and logging
     */
    public static final String TAG = "GoogleCalendarManager: ";

    private static final String APPLICATION_NAME = "DisAppointment Terminkalender";
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
    private final Calendar service;

    /**
     * Singleton implementation with try catch block, to catch errors while connecting
     * to the Google API
     */
    private static GoogleCalendarManagement INSTANCE;
    static {
        try {
            INSTANCE = new GoogleCalendarManagement();
        } catch (IOException | GeneralSecurityException e) {
            LOGGER.log(Level.FINE, TAG + "didn't connect to Server");
        }
    }

    public static GoogleCalendarManagement getInstance() { return INSTANCE; }

    /**
     * private method for creating n new GoogleKalendar, only used by this class
     * @throws IOException when there is an error with the network connection
     * @throws GeneralSecurityException when there is a faulty token
     */
    private GoogleCalendarManagement() throws IOException, GeneralSecurityException {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        service = new Calendar.Builder(httpTransport, JSON_FACTORY, getCredentials(httpTransport))
                .setApplicationName(APPLICATION_NAME).build();
        LOGGER.log(Level.FINE,TAG + "Google Calendar successfully connected");
    }

    /**
     * Creates an authorized Credential object.
     *
     * @param httpTransport The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport httpTransport) throws IOException {
        // Load client secrets.
        InputStream in = GoogleCalendarManagement.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException(TAG + "Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY,
                clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline").build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    /**
     * This functions creates a new Calendar for a specific user.
     *
     * @param userCalID the id of the User Calender
     * @param eventName name of the event
     * @param eventLocation name of the event location
     * @param eventDescription name of the eventDescription
     * @param epochStart start time in epoch format
     * @param epochEnd end time in epoch format
     * @throws IOException when the event can't be added to a users calendar
     * @return a link to the newly created Event
     */
    public String createNewEvent(String userCalID, String eventName, String eventLocation, String eventDescription, long epochStart, long epochEnd) throws IOException {

        Event event = new Event()
                .setSummary(eventName)
                .setLocation(eventLocation)
                .setDescription(eventDescription);

        DateTime startDateTime = new DateTime(epochStart);
        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone(BOT_TIMEZONE);
        event.setStart(start);

        DateTime endDateTime = new DateTime(epochEnd);
        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone(BOT_TIMEZONE);
        event.setEnd(end);

        event = service.events().insert(userCalID, event).execute()
        .setVisibility("public");

        LOGGER.log(Level.FINE,TAG + "A new Event was created under: %s\n", event.getHtmlLink());

        return event.getHtmlLink();
    }

    /**
     * Creates a Calendar for a User.
     *
     * @param cName the name of the Calendar that will be displayed in google calendars
     * @return String with calendar id
     * @throws IOException if the insertion fails
     */
    public String createCalendar(String cName) throws IOException {

        // CREATE A NEW CALENDAR //
        String calendarName = cName + "'s Terminkalender";

        // Create a new calendar
        com.google.api.services.calendar.model.Calendar tempCalendar = new com.google.api.services.calendar.model.Calendar()
        .setSummary(calendarName)
        .setTimeZone(BOT_TIMEZONE);

        tempCalendar = service.calendars().insert(tempCalendar).execute();

        String logMessage = String.format("created a new Calendar with ID: %s",tempCalendar.getId());
        LOGGER.log(Level.FINE,logMessage);

        // Create access rule with associated scope
        AclRule rule = new AclRule();
        AclRule.Scope scope = new AclRule.Scope();

        //Make the Calendar public so it can be shared
        scope.setType("default").setValue("");
        rule.setScope(scope).setRole("reader");

        // Insert new access rule (Can delete Var if not needed any further)
        service.acl().insert(tempCalendar.getId(), rule).execute();

        return tempCalendar.getId();
    }


    /**
     * Returns a public link to a Calendar so the User can add it to their browser.
     *
     * @param userCalID the id of the User Calender
     * @return the link to the public google Calendar of that person.
     */
    public String getPublicCalendarLink(String userCalID) {

        return "https://calendar.google.com/calendar/r?cid=" + userCalID;
    }

    /**
     * Delete a event at the given start time
     *
     * @param calendarID the id of the calendar
     * @param epochStart the start time of the event that should be deleted in epoch
     * @return true if the deletion was sucessfull
     * @throws IOException when the service worker doesn't find the calendar
     */
    public boolean deleteEvent (String calendarID, long epochStart) throws IOException {

        DateTime desiredDate = new DateTime(epochStart);
        Events events = service.events().list(calendarID)
                .setMaxResults(1)
                .setTimeMin(desiredDate)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
        List<Event> items = events.getItems();

        if (items.isEmpty()) {
            LOGGER.log(Level.FINE,TAG + "There was no event in the specified date and time");
            return false;
           } else {
            //Get the Time the event starts so we can compare it
            EventDateTime gEventTime = items.get(0).getStart();
            if (gEventTime.getDateTime().getValue() == epochStart) {
                //Extract the event id of the event we want to delete
                String eventID = items.get(0).getId();
                service.events().delete(calendarID, eventID).execute();

                LOGGER.log(Level.FINE,TAG + "Sucessfully deleted a event");
                return true;
            } else {
                LOGGER.log(Level.FINE,TAG + "Event Time != the time of the event to delete");
                return false;
            }
        }
    }
}
