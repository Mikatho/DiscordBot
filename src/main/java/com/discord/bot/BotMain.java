package com.discord.bot;

import com.discord.bot.listeners.CommandListener;
<<<<<<< HEAD
=======
import com.discord.bot.listeners.PMListener;
>>>>>>> fa80302b304130282fe7f33c3855d11f3576c846
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;

import javax.security.auth.login.LoginException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class BotMain {

<<<<<<< HEAD
=======
    public static BotMain INSTANCE;

>>>>>>> fa80302b304130282fe7f33c3855d11f3576c846
    private JDA jda;
    private JDABuilder jdaBuilder;

    public static void main(String[] args) {
<<<<<<< HEAD

=======
>>>>>>> fa80302b304130282fe7f33c3855d11f3576c846
        try {
            new BotMain();
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }

<<<<<<< HEAD
    public BotMain() throws LoginException {

        final String BOT_TOKEN = "Njk0MTkxMzY3NzUxOTkxMzQ3.XqiLrg.mmC1Fvf6zRWdJN2dfsRMUu7WMhs";

        //Bot wird mit Datenbank verbunden
=======
    public BotMain () throws LoginException {
        INSTANCE = this;

        final String BOT_TOKEN = "Njk0MTkxMzY3NzUxOTkxMzQ3.XqiLrg.mmC1Fvf6zRWdJN2dfsRMUu7WMhs";

>>>>>>> fa80302b304130282fe7f33c3855d11f3576c846
        DatabaseManagement.getINSTANCE().connect();

        jdaBuilder = JDABuilder.createDefault(BOT_TOKEN);
        jdaBuilder.setStatus(OnlineStatus.ONLINE);
<<<<<<< HEAD

        //Listener für den Bot werden initialisiert
        jdaBuilder.addEventListeners(new CommandListener());
=======
        jdaBuilder.addEventListeners(new CommandListener());
        //jdaBuilder.addEventListeners(new PMListener());
>>>>>>> fa80302b304130282fe7f33c3855d11f3576c846

        jda = jdaBuilder.build();
        System.out.println("Bot online.");

        shutdown();
    }

    public void shutdown() {
<<<<<<< HEAD

        new Thread(() -> {
            String line = "";

            //Reader zum Lesen der Konsoleneingaben
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            try {
                while ((line = reader.readLine()) != null) {
                    if (line.equals("shutdown")) {
                        //Sofern JDA (Bot) initialisiert wurde
=======
        new Thread (() -> {
            String line = "";
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            try {
                while((line = reader.readLine()) != null) {
                    if (line.equals("shutdown")) {
>>>>>>> fa80302b304130282fe7f33c3855d11f3576c846
                        if (jda != null) {
                            jdaBuilder.setStatus(OnlineStatus.OFFLINE);
                            jda.shutdownNow();
                            DatabaseManagement.getINSTANCE().disconnect();
                            System.out.println("Bot offline.");
                        }
                        reader.close();
                        return;
                    } else {
<<<<<<< HEAD
                        //Falls man etwas anderes als "shutdown" eingibt
                        System.out.println("Use 'shutdown' to shutdown the Bot.");
                    }
                }
            } catch (IOException e) {
=======
                        System.out.println("Use 'shutdown' to shutdown the Bot.");
                    }
                }
            } catch(IOException e) {
>>>>>>> fa80302b304130282fe7f33c3855d11f3576c846
                e.printStackTrace();
            }
        }).start();
    }
}
