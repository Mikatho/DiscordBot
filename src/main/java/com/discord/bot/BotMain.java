package com.discord.bot;

import com.discord.bot.listeners.CommandListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;

import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.security.auth.login.LoginException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.sql.SQLException;


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

    private static final Logger LOGGER = LogManager.getLogger(BotMain.class.getName());
    private static JDA jda;
    private JDABuilder jdaBuilder;

    /**
     * Create instance of <code>BotMain()</code>
     *
     * @param args Array. contains all variables from command call.
     */
    public static void main(String[] args) {

        try {
            new BotMain();
        } catch (LoginException e) {
            LOGGER.fatal(String.format("Unable to instantiate BotMain.%n%s", e));
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

        final String BOT_TOKEN = "Njk0MTkxMzY3NzUxOTkxMzQ3.XqiLrg.mmC1Fvf6zRWdJN2dfsRMUu7WMhs";

        try {
            DatabaseManagement.getINSTANCE().connect();
        } catch (SQLException | IOException e) {
            LOGGER.fatal(String.format("Unable to connect to database.%n%s", e));
        }

        jdaBuilder = JDABuilder.createDefault(BOT_TOKEN)
                .setStatus(OnlineStatus.ONLINE)
                .setActivity(Activity.listening("!help"))
                .setChunkingFilter(ChunkingFilter.ALL)
                .enableIntents(GatewayIntent.GUILD_MEMBERS);

        // Initialize Listener.
        jdaBuilder.addEventListeners(new CommandListener());

        jda = jdaBuilder.build();
        System.out.println("Bot online.");

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
                            System.out.println("Bot offline.");
                        }
                        reader.close();
                        return;
                    } else {
                        // If your input isn´t "shutdown".
                        System.out.println("Use 'shutdown' to shutdown the Bot.");
                    }
                }
            } catch (IOException | SQLException e) {
                LOGGER.fatal(String.format("Unable to shutdown the bot.%n%s", e));
            }
        }).start();
    }

    public static JDA getJda() {
        return jda;
    }
}
