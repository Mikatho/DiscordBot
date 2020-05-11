package com.discord.bot;

<<<<<<< HEAD
import java.io.*;
=======
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
>>>>>>> fa80302b304130282fe7f33c3855d11f3576c846
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.ListIterator;
<<<<<<< HEAD
import java.util.StringJoiner;
=======
>>>>>>> fa80302b304130282fe7f33c3855d11f3576c846

public class LoggingManagement {

    private static final LoggingManagement INSTANCE = new LoggingManagement();

    private static final int LOG_CAPACITY = 10;

    private LinkedList<String> commandLog = new LinkedList<>();

    public static LoggingManagement getINSTANCE() {
        return INSTANCE;
    }

<<<<<<< HEAD
    //Löscht Daten in Logfile, indem Datei mit leerem String überschrieben wird
    public void clear() {

        try {
            PrintWriter writer = new PrintWriter("commands.log");
            writer.print("");
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void addToLog(String command) {

        //Fügt zum Command die Zeit des Aufrufs hinzu
=======
    public void addToLog(String command) {
>>>>>>> fa80302b304130282fe7f33c3855d11f3576c846
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        LocalDateTime localTime = LocalDateTime.now();

        commandLog.addLast(formatter.format(localTime) + " " + command);
    }

<<<<<<< HEAD
    public String logToConsole() {

        StringJoiner joiner = new StringJoiner("\n");

        //Stellt sicher, dass nur gewisse Anzahl an Commands gepostet werden
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
=======
    public void logToConsole() {
        ListIterator<String> li = commandLog.listIterator(commandLog.size()-LOG_CAPACITY);

        while (li.hasNext()) {
            System.out.println(li.next());
        }
>>>>>>> fa80302b304130282fe7f33c3855d11f3576c846
    }

    public void saveToFile() {

        String fileName = "commands.log";

        try {
<<<<<<< HEAD
            //Datei wird erstellt, falls noch nicht vohanden
=======
>>>>>>> fa80302b304130282fe7f33c3855d11f3576c846
            File file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

<<<<<<< HEAD
        //Schreibt alle Commands der Liste in die Datei und cleart die Liste
        try {
            FileWriter fw = new FileWriter(fileName, true);
=======
        try {
            FileWriter fw = new FileWriter(fileName);
>>>>>>> fa80302b304130282fe7f33c3855d11f3576c846
            BufferedWriter bw = new BufferedWriter(fw);

            for (String item : commandLog) {
                bw.write(item);
                bw.newLine();
            }

            bw.close();
<<<<<<< HEAD
            System.out.println("Successfully saved log to file.");

            commandLog.clear();
=======
            System.out.println("File created successfully.");

            if (!commandLog.isEmpty()) {
                commandLog.clear();
            }
>>>>>>> fa80302b304130282fe7f33c3855d11f3576c846

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
