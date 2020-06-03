package com.discord.bot.data;


/**
 * The class <code>UserData</code> is saved as an object for every registered user in the database.
 * It contains the user attributes.
 *
 * @author      L2G4
 * @version     %I%, %G%
 * @see         com.discord.bot.commands.UserCommand
 * @see         com.discord.bot.UserManagement
 * @since       1.0
 */
public class UserData {

    private String userID;

    private String address;

    private String[] interests;

    private String[] competencies;

    private String gCalendarLink;

    public UserData(String userID) {

        this.userID = userID;
    }

    public final String getUserID() {
        return userID;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public final String getAddress() {
        return address;
    }

    public void setInterests(String[] interests) {
        this.interests = interests;
    }

    public final String[] getInterests() {
        return interests;
    }

    public void setCompetencies(String[] competencies) {
        this.competencies = competencies;
    }

    public final String[] getCompetencies() {
        return competencies;
    }

    public void setgCalendarLink(String gCalendarLink) {
        this.gCalendarLink = gCalendarLink;
    }

    public final String getgCalendarLink() {
        return gCalendarLink;
    }
}
