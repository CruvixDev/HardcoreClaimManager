package fr.sukikui.hardcoreclaimmanager.data;

import java.sql.*;

import fr.sukikui.hardcoreclaimmanager.HardcoreClaimManager;
import fr.sukikui.hardcoreclaimmanager.claim.Claim;
import fr.sukikui.hardcoreclaimmanager.player.PlayerData;

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
        Connection connection;
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
                    "worldName TEXT UNIQUE NOT NULL," +
                    "corner1X INTEGER NOT NULL," +
                    "corner1Y INTEGER NOT NULL," +
                    "corner1Z INTEGER NOT NULL," +
                    "corner2X INTEGER NOT NULL," +
                    "corner2Y INTEGER NOT NULL," +
                    "corner2Z INTEGER NOT NULL," +
                    "playerName TEXT NOT NULL," +
                    "playerUUID TEXT NOT NULL," +
                    "FOREIGN KEY (playerName,playerUUID) REFERENCES Player(playerName,playerUUID)," +
                    "FOREIGN KEY (worldName) REFERENCES World(worldName)," +
                    "CONSTRAINT corner1 UNIQUE (worldName,corner1X,corner1Y,corner1Z)," +
                    "CONSTRAINT corner2 UNIQUE (worldName,corner2X,corner2Y,corner2Z)," +
                    "CHECK (corner1X != corner2X AND corner1Z != corner2Z)" +
                ")";
        String createPlayerTableRequest = "CREATE TABLE IF NOT EXISTS Player" +
                "(" +
                    "playerName TEXT NOT NULL," +
                    "playerUUID TEXT NOT NULL," +
                    "claimBlocks INTEGER NOT NULL" +
                    "PRIMARY KEY (playerName,playerUUID)" +
                ")";
        String createWorldTableRequest = "CREATE TABLE IF NOT EXISTS World" +
                "(" +
                    "worldName TEXT NOT NULL PRIMARY KEY" +
                ")";
        String createTrustedPlayersTableRequest = "CREATE TABLE IF NOT EXISTS TrustedPlayers" +
                "(" +
                    "claimID INTEGER NOT NULL," +
                    "playerName TEXT NOT NULL," +
                    "playerUUID TEXT NOT NULL," +
                    "FOREIGN KEY (claimID) REFERENCES Claim(claimID)," +
                    "FOREIGN KEY (playerName,playerUUID) REFERENCES Player(playerName,playerUUID)," +
                    "PRIMARY KEY (claimID,playerName,playerUUID)" +
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

    public void insertPlayer(PlayerData playerData) {
        String insertPlayerRequest = "INSERT INTO Player (playerName,playerUUID,claimBlocks) VALUES (?,?,?)";
    }

    public void deletePlayer(PlayerData playerData) {
        String deletePlayerRequest = "DELETE FROM Player WHERE playerName=? AND playerUUID=?";
        //TODO Delete all claims related to this player
    }

    public void insertClaim(Claim claim, PlayerData playerData) {
        String insertClaimRequest = "INSERT INTO Claim (worldName,corner1X,corner1Y,corner1Z,corner2X,corner2Y," +
                "corner2Z,playerName,playerUUID) VALUES (?,?,?,?,?,?,?,?,?)";
    }

    public void deleteClaim() {
        //delete a claim in the database
        //TODO Delete players if no claim are related to him
    }

    public void insertWorld(String worldName) {
        String insertWorldRequest = "INSERT INTO World (worldName) VALUES (?)";
    }

    public void insertTrustedPlayers() {
        //insert allowed players for a claim in the database
    }

    public void deleteTrustedPlayers() {
        //delete allowed players for a claim in the database
    }
}
