package com.discord.bot;

import com.discord.bot.listeners.CommandListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;

import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;


import javax.security.auth.login.LoginException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.sql.SQLException;
import java.util.logging.Level;


/**
 * The <code>BotMain</code> class contains the methods to initialize, start, and shutdown the Bot.
 *
 * @author L2G4
 * @version %I%, %G%
 * @see com.discord.bot.BotMain
 * @see com.discord.bot.listeners.CommandListener
 * @see DatabaseManagement#getINSTANCE()
 * @see com.discord.bot.DatabaseManagement
 * @since 1.0
 */
public class BotMain {
    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(BotMain.class.getName());

    private static final String BOT_TOKEN = "NzIwNzE1NjE0NjM2NzM2NTQz.XuKLVg.g5Bc89uLXA2-C9_-hbdim8Z87mo";
    private static JDA jda;
    private static JDABuilder jdaBuilder;

    static {
        try {
            jdaBuilder = JDABuilder.createDefault(BOT_TOKEN)
                    .setStatus(OnlineStatus.ONLINE)
                    .setActivity(Activity.listening("!help"))
                    .setChunkingFilter(ChunkingFilter.ALL)
                    .enableIntents(GatewayIntent.GUILD_MEMBERS);
            // Initialize Listener.
            jdaBuilder.addEventListeners(new CommandListener());

            jda = jdaBuilder.build();
            LOGGER.log(Level.FINE, "Bot online");
        } catch (Exception e) {
            LOGGER.log(Level.FINE, e.getMessage());
        }
    }

    /**
     * Create instance of <code>BotMain()</code>
     *
     * @param args Array. contains all variables from command call.
     */
    public static void main(String[] args) {
        try {
            new BotMain();
        } catch (LoginException e) {
            LOGGER.log(Level.FINE, String.format("Unable to instantiate BotMain.%n%s", e));
        }
    }

    /**
     * The <code>BotMain()</code> method makes a declaration of the jdaBuilder and JDA.
     * It calls the method connect() in <code>DatabaseManagement</code> to build the database
     * and also holds the BotTokken. The method executes the Discord Bot.
     *
     * @throws LoginException Unable to get the instance of database.
     */
    public BotMain() throws LoginException {

        try {
            DatabaseManagement.getINSTANCE().connect();
        } catch (SQLException | IOException e) {
            LOGGER.log(Level.FINE, String.format("Unable to connect to database.%n%s", e));
        }

        shutdown();
    }

    /*
     * The <code>shutdown()</code> method closes all connections and shuts down the Bot.
     */
    public void shutdown() {

        new Thread(() -> {
            String line;

            // Reader for the console inputs.
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            try {
                while ((line = reader.readLine()) != null) {
                    if (line.equals("shutdown")) {
                        // Check, if Bot is initialized.
                        if (jda != null) {
                            jdaBuilder.setStatus(OnlineStatus.OFFLINE);
                            jda.shutdownNow();
                            DatabaseManagement.getINSTANCE().disconnect();
                            LOGGER.log(Level.FINE,"Bot offline.");
                        }
                        reader.close();
                        return;
                    } else {
                        // If your input isn´t "shutdown".
                        LOGGER.log(Level.FINE,"Use 'shutdown' to shutdown the Bot.");
                    }
                }
            } catch (IOException | SQLException e) {
                LOGGER.log(Level.FINE, String.format("Unable to shutdown the bot.%n%s", e));
            }
        }).start();
    }

    public static JDA getJda() {
        return jda;
    }
}
