package com.discord.bot;

import com.discord.bot.listeners.CommandListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;

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

    final static Logger logger = LogManager.getLogger(BotMain.class.getName());
    private static JDA jda;
    private JDABuilder jdaBuilder;

    /**
     * Create instance of <code>BotMain()</code>
     *
     * @param args Array. contains all variables from command call.
     * @throws LoginException Basic login exception. If method is unable to instantiate.
     */
    public static void main(String[] args) {

        try {
            new BotMain();
        } catch (LoginException e) {
            logger.fatal("Unable to instantiate BotMain.\n" + e);
        }
    }

    /**
     * The <code>BotMain()</code> method makes a declaration of the jdaBuilder and JDA.
     * It calls the method connect() in <code>DatabaseManagement</code> to build the database
     * and also holds the BotTokken. The method executes the Discord Bot.
     *
     * @throws SQLException Unable to get the instance of database.
     * @throws IOException  Unable to access the database.
     */
    public BotMain() throws LoginException {

        final String BOT_TOKEN = "Njk0MTkxMzY3NzUxOTkxMzQ3.XqiLrg.mmC1Fvf6zRWdJN2dfsRMUu7WMhs";

        try {
            DatabaseManagement.getINSTANCE().connect();
        } catch (SQLException | IOException e) {
            logger.fatal("Unable to connect to database.\n" + e);
        }

        jdaBuilder = JDABuilder.createDefault(BOT_TOKEN)
                .setStatus(OnlineStatus.ONLINE)
                .setChunkingFilter(ChunkingFilter.ALL)
                .enableIntents(GatewayIntent.GUILD_MEMBERS);

        /**
         * Initialize Listener.
         */
        jdaBuilder.addEventListeners(new CommandListener());

        jda = jdaBuilder.build();
        System.out.println("Bot online.");

        shutdown();
    }

    /**
     * The <code>shutdown()</code> method closes all connections and shuts down the Bot.
     *
     * @throws IOException  Basic login exception. Unable to interact with the BufferedReader.
     * @throws SQLException No access to database.
     */
    public void shutdown() {

        new Thread(() -> {
            String line;

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
            } catch (IOException | SQLException e) {
                logger.fatal("Unable to shutdown the Bot.\n" + e);
            }
        }).start();
    }

    public static JDA getJda() {
        return jda;
    }
}
