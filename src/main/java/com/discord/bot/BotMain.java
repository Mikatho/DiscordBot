package com.discord.bot;

import com.discord.bot.listeners.CommandListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;

import javax.security.auth.login.LoginException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class BotMain {
    private JDA jda;
    private JDABuilder jdaBuilder;

    public static void main(String[] args) {

        try {
            new BotMain();
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }

    public BotMain() throws LoginException {

        final String BOT_TOKEN = "Njk0MTkxMzY3NzUxOTkxMzQ3.XqiLrg.mmC1Fvf6zRWdJN2dfsRMUu7WMhs";
        DatabaseManagement.getINSTANCE().connect();

        jdaBuilder = JDABuilder.createDefault(BOT_TOKEN);
        jdaBuilder.setStatus(OnlineStatus.ONLINE);

        //Listener für den Bot werden initialisiert
        jdaBuilder.addEventListeners(new CommandListener());

        jda = jdaBuilder.build();
        System.out.println("Bot online.");

        shutdown();
    }

    public void shutdown() {

        new Thread(() -> {
            String line = "";

            //Reader zum Lesen der Konsoleneingaben
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            try {
                while ((line = reader.readLine()) != null) {
                    if (line.equals("shutdown")) {
                        //Sofern JDA (Bot) initialisiert wurde
                        if (jda != null) {
                            jdaBuilder.setStatus(OnlineStatus.OFFLINE);
                            jda.shutdownNow();
                            DatabaseManagement.getINSTANCE().disconnect();
                            System.out.println("Bot offline.");
                        }
                        reader.close();
                        return;
                    } else {
                        //Falls man etwas anderes als "shutdown" eingibt
                        System.out.println("Use 'shutdown' to shutdown the Bot.");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
