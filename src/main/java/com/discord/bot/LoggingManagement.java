package com.discord.bot;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.StringJoiner;


/**
 * @author L2G4
 * @version %I%, %G%
 * @see com.discord.bot.LoggingManagement
 * @see com.discord.bot.CommandManager
 * @since 1.0
 */
public class LoggingManagement {

    private static final Logger LOGGER = LogManager.getLogger(LoggingManagement.class.getName());
    private static final LoggingManagement INSTANCE = new LoggingManagement();  // creates INSTANCE of Class

    private static final int LOG_CAPACITY = 10;

    private LinkedList<String> commandLog = new LinkedList<>();

    /**
     * This method return an instance of the <code>LoggingManagement</code> object.
     *
     * @return INSTANCE    instance of the LoggingManagement object
     */
    public static LoggingManagement getINSTANCE() {
        return INSTANCE;
    }


    /**
     * Delete data in Logfile by overwrite the entries with an empty string.
     */
    public void clear() {

        try {
            PrintWriter writer = new PrintWriter("commands.log");
            writer.print("");
            writer.close();
        } catch (FileNotFoundException e) {
            LOGGER.fatal(String.format("Unable to find or connect to commands.log.%n%s", e));
        }
    }

    /**
     * Add the input time of the commands to the entries.
     *
     * @param command Input of user.
     */
    public void addToLog(String command) {

        // Add the input time to the command.
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        LocalDateTime localTime = LocalDateTime.now();

        commandLog.addLast(formatter.format(localTime) + "  " + command);
    }

    /**
     * Log all commands to a Limit in a String.
     *
     * @return joiner  contains all entered commands.
     */
    public String logToConsole() {

        StringJoiner joiner = new StringJoiner("\n");

        // Set a max, that only a few commands are shown.
        if (commandLog.size() <= LOG_CAPACITY) {
            for (String item : commandLog) {
                joiner.add(item);
            }
        } else {
            ListIterator<String> li = commandLog.listIterator(commandLog.size() - LOG_CAPACITY);

            while (li.hasNext()) {
                joiner.add(li.next());
            }
        }
        return joiner.toString();
    }

    // Save all logs in a file.
    public void saveToFile() {

        String fileName = "commands.log";

        try {
            // Create file if it doesn´t exist.
            File file = new File(fileName);
            if (!file.exists()) {
                String tempMsg = Boolean.toString(file.createNewFile());
                LOGGER.info(tempMsg);
            }
        } catch (IOException e) {
            LOGGER.fatal(String.format("Unable to ceate new file.%n%s", e));
        }

        // Save all commands in a file and clear the list.
        try (FileWriter fw = new FileWriter(fileName, true);
             BufferedWriter bw = new BufferedWriter(fw)) {

            for (String item : commandLog) {
                bw.write(item);
                bw.newLine();
            }

            commandLog.clear();

        } catch (IOException e) {
            LOGGER.fatal(String.format("Unable to save logs.%n%s", e));
        }
    }
}
