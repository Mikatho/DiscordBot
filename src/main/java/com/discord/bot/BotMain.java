package com.discord.bot;

import com.discord.bot.listeners.CommandListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;

import javax.security.auth.login.LoginException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


/**
 * The <code>BotMain</code> class contains the methods to initialize, start, and shutdown the Bot.
 *
 * @author      L2G4
 * @version     %I%, %G%
 * @see         com.discord.bot.BotMain
 * @see         com.discord.bot.listeners.CommandListener
 * @see         DatabaseManagement#getINSTANCE()
 * @see         com.discord.bot.DatabaseManagement
 * @since       1.0
 */
public class BotMain {

    private JDA jda;
    private JDABuilder jdaBuilder;

    /**
     * Create instance of <code>BotMain()</code>
     *
     * @exception   // TODO: 04.06.2020
     * @param   args
     */
    public static void main(String[] args) {

        try {
            new BotMain();
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }

    /**
     * The <code>BotMain()</code> method makes a declaration of the jdaBuilder and JDA.
     * It calls the method connect() in <code>DatabaseManagement</code> to build the database
     * and also holds the BotTokken. The method executes the Discord Bot.
     *
     * @throws LoginException   toDo
     */
    public BotMain() throws LoginException {

        final String BOT_TOKEN = "Njk0MTkxMzY3NzUxOTkxMzQ3.XqiLrg.mmC1Fvf6zRWdJN2dfsRMUu7WMhs";
        DatabaseManagement.getINSTANCE().connect();

        jdaBuilder = JDABuilder.createDefault(BOT_TOKEN);
        jdaBuilder.setStatus(OnlineStatus.ONLINE);

        /**
         * Initialize Command Listener.
         */
        jdaBuilder.addEventListeners(new CommandListener());

        jda = jdaBuilder.build();
        System.out.println("Bot online.");

        shutdown();
    }

    /**
     * The <code>shutdown()</code> method closes all connections and shuts down the Bot.
     *
     * @exception // TODO: 04.06.2020
     */
    public void shutdown() {

        new Thread(() -> {
            String line = "";

            /**
             * Reader for the console inputs.
             */
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            try {
                while ((line = reader.readLine()) != null) {
                    if (line.equals("shutdown")) {
                        /**
                         * Check, if Bot is initialized.
                         */
                        if (jda != null) {
                            jdaBuilder.setStatus(OnlineStatus.OFFLINE);
                            jda.shutdownNow();
                            DatabaseManagement.getINSTANCE().disconnect();
                            System.out.println("Bot offline.");
                        }
                        reader.close();
                        return;
                    } else {
                        /**
                         * If your input isn´t "shutdown".
                         */
                        System.out.println("Use 'shutdown' to shutdown the Bot.");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
