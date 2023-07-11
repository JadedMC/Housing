package net.jadedmc.housing;

import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Manages the connection process to MySQL.
 */
public class MySQL {
    private Connection connection;

    /**
     * Loads the MySQL database connection info.
     * @param plugin Instance of the plugin.
     */
    public MySQL(HousingPlugin plugin) {
        String host = plugin.settingsManager().getConfig().getString("MySQL.host");
        String database = plugin.settingsManager().getConfig().getString("MySQL.database");
        String username = plugin.settingsManager().getConfig().getString("MySQL.username");
        String password = plugin.settingsManager().getConfig().getString("MySQL.password");
        int port = plugin.settingsManager().getConfig().getInt("MySQL.port");

        // Runs connection tasks async.
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, ()-> {
            // Attempts to connect to the MySQL database.
            try {
                synchronized(HousingPlugin.class) {
                    Class.forName("com.mysql.jdbc.Driver");
                    connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true&useSSL=false&characterEncoding=utf8", username, password);
                }
            }
            catch(SQLException | ClassNotFoundException exception) {
                // If the connection fails, logs the error.
                exception.printStackTrace();
                return;
            }

            // Prevents losing connection to MySQL.
            plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, ()-> {
                try {
                    connection.isValid(0);
                }
                catch (SQLException exception) {
                    exception.printStackTrace();
                }
            }, 504000, 504000);

            // Create tables if they do not exist.
            try {
                PreparedStatement housingTemplates = connection.prepareStatement("CREATE TABLE IF NOT EXISTS housing_templates (" +
                        "templateID VARCHAR(36) NOT NULL, " +
                        "houseUUID VARCHAR(36) NOT NULL, " +
                        "PRIMARY KEY (templateID));");
                housingTemplates.execute();

                PreparedStatement housingHouses = connection.prepareStatement("CREATE TABLE IF NOT EXISTS housing_houses (" +
                        "houseUUID VARCHAR(36) NOT NULL, " +
                        "ownerUUID VARCHAR(36) NOT NULL, " +
                        "spawn VARCHAR(36) NOT NULL, " +
                        "PRIMARY KEY (houseUUID));");
                housingHouses.execute();

                PreparedStatement housingFiles = connection.prepareStatement("CREATE TABLE IF NOT EXISTS housing_house_files (" +
                        "houseUUID VARCHAR(36) NOT NULL, " +
                        "houseFile MEDIUMBLOB NOT NULL, " +
                        "PRIMARY KEY (houseUUID));");
                housingFiles.execute();

                PreparedStatement houseSettings = connection.prepareStatement("CREATE TABLE IF NOT EXISTS housing_house_settings (" +
                        "houseUUID VARCHAR(36) NOT NULL, " +
                        "houseName VARCHAR(36) NOT NULL, " +
                        "houseIcon VARCHAR(36) NOT NULL, " +
                        "visibility VARCHAR(24) NOT NULL, " +
                        "weather VARCHAR(24) NOT NULL, " +
                        "time BIGINT NOT NULL, " +
                        "gameMode VARCHAR(24) NOT NULL, " +
                        "pvp BOOLEAN NOT NULL, " +
                        "PRIMARY KEY (houseUUID));");
                houseSettings.execute();
            }
            catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    /**
     * Close a connection.
     */
    public void closeConnection() {
        if(isConnected()) {
            try {
                connection.close();
            }
            catch(SQLException exception) {
                exception.printStackTrace();
            }
        }
    }

    /**
     * Get the connection.
     * @return Connection
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Get if plugin is connected to the database.
     * @return Connected
     */
    private boolean isConnected() {
        return (connection != null);
    }
}