package fr.sukikui.hardcoreclaimmanager.data;

import java.sql.*;

import fr.sukikui.hardcoreclaimmanager.HardcoreClaimManager;

/**
 * Class handling all interactions with the SQLite database
 */
public class DatabaseManager {
    private String databasePath;
    private static DatabaseManager databaseManager;
    private HardcoreClaimManager hardcoreClaimManager;

    private DatabaseManager(HardcoreClaimManager hardcoreClaimManager) {
        this.hardcoreClaimManager = hardcoreClaimManager;
        this.databasePath = this.hardcoreClaimManager.getProperties().getProperty("database-path");
    }

    /**
     * Method to get a unique instance of the DatabaseManager class
     * @param hardcoreClaimManager the plugin instance
     * @return a unique instance of the DatabaseManager class
     */
    public static DatabaseManager getInstance(HardcoreClaimManager hardcoreClaimManager) {
        if (databaseManager == null) {
            databaseManager = new DatabaseManager(hardcoreClaimManager);
        }
        return databaseManager;
    }

    /**
     * A private method to get a connection to the SQLite database
     * @return a connection to the SQLite database
     */
    private Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + databasePath + "\\HardcoreClaimManager.db");
            //This request indicates to SQLite database to activate foreign key constraint.
            String foreignKeyEnableRequest = "PRAGMA foreign_keys = ON";
            PreparedStatement statement = connection.prepareStatement(foreignKeyEnableRequest);
            statement.execute();
            statement.close();
            return connection;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Create all tables needed in the SQLite database to store plugin data
     */
    public void createDatabase() {
        String createClaimTableRequest = "CREATE TABLE IF NOT EXISTS Claim" +
                "(" +
                    "claimID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                    "playerName TEXT UNIQUE NOT NULL," +
                    "worldName TEXT UNIQUE NOT NULL," +
                    "corner1X INTEGER NOT NULL," +
                    "corner1Y INTEGER NOT NULL," +
                    "corner1Z INTEGER NOT NULL," +
                    "corner2X INTEGER NOT NULL," +
                    "corner2Y INTEGER NOT NULL," +
                    "corner2Z INTEGER NOT NULL," +
                    "FOREIGN KEY (playerName) REFERENCES Player(playerName)," +
                    "FOREIGN KEY (worldName) REFERENCES World(worldName)," +
                    "CONSTRAINT corner1 UNIQUE (worldName,corner1X,corner1Y,corner1Z)," +
                    "CONSTRAINT corner2 UNIQUE (worldName,corner2X,corner2Y,corner2Z)" +
                ")";
        String createPlayerTableRequest = "CREATE TABLE IF NOT EXISTS Player" +
                "(" +
                    "playerName TEXT NOT NULL PRIMARY KEY," +
                    "claimBlocs INTEGER NOT NULL" +
                ")";
        String createWorldTableRequest = "CREATE TABLE IF NOT EXISTS World" +
                "(" +
                    "worldName TEXT NOT NULL PRIMARY KEY" +
                ")";
        String createTrustedPlayersTableRequest = "CREATE TABLE IF NOT EXISTS TrustedPlayers" +
                "(" +
                    "claimID INTEGER NOT NULL," +
                    "playerName TEXT NOT NULL," +
                    "PRIMARY KEY (claimID,playerName)," +
                    "FOREIGN KEY (claimID) REFERENCES Claim(claimID)," +
                    "FOREIGN KEY (playerName) REFERENCES Player(playerName)" +
                ")";

        Connection connection = getConnection();
        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement(createPlayerTableRequest);
            statement.executeUpdate();

            statement = connection.prepareStatement(createWorldTableRequest);
            statement.executeUpdate();

            statement = connection.prepareStatement(createClaimTableRequest);
            statement.executeUpdate();

            statement = connection.prepareStatement(createTrustedPlayersTableRequest);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                statement.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void insertPlayer() {
        //insert a player in the database
    }

    public void deletePlayer() {
        //delete a player in the database
    }

    public void insertClaim() {
        //insert a claim in the database
    }

    public void deleteClaim() {
        //delete a claim in the database
    }

    public void insertTrustedPlayers() {
        //insert allowed players for a claim in the database
    }

    public void deleteTrustedPlayers() {
        //delete allowed players for a claim in the database
    }
}
