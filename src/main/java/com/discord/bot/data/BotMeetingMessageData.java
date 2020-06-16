package com.discord.bot.data;


/**
 * The <code>BotMeetingMessageData</code> class contains all parameters of a message from another bot to arrange the meeting.
 *
 * @author L2G4
 * @version %I%, %G%
 * @see com.discord.bot.commands.MeetingCommand
 * @see com.discord.bot.commands.BotMeetingCommand
 * @since 1.0
 */
public class BotMeetingMessageData {

    private String message;

    private String hostID;

    private String participantID;

    private String foreignUserName;

    private String startDateISO;

    private int duration;

    private long epochPeriodEnd;

    private String meetingMessage;

    private boolean firstStep;

    public BotMeetingMessageData(String message, String hostID, String participantID, String foreignUserName, int duration, String startDateISO, long epochEnd, String meetingMessage, boolean firstStep) {

        this.message = message;
        this.hostID = hostID;
        this.participantID = participantID;
        this.foreignUserName = foreignUserName;
        this.duration = duration;
        this.startDateISO = startDateISO;
        this.epochPeriodEnd = epochEnd;
        this.meetingMessage = meetingMessage;
        this.firstStep = firstStep;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public final String getMessage() {
        return message;
    }

    public final String getHostID() {
        return hostID;
    }

    public final String getParticipantID() {
        return participantID;
    }

    public final String getForeignUserName() {
        return foreignUserName;
    }

    public final int getDuration() {
        return duration;
    }

    public void setStartDateISO(String startDateISO) {
        this.startDateISO = startDateISO;
    }

    public final String getStartDateISO() {
        return startDateISO;
    }

    public final long getEpochPeriodEnd() {
        return epochPeriodEnd;
    }

    public String getMeetingMessage() {
        return meetingMessage;
    }

    public final boolean getFirstStep() {
        return firstStep;
    }
}
