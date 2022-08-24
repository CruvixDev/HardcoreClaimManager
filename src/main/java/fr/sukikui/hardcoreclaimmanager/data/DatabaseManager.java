package fr.sukikui.hardcoreclaimmanager.data;

import java.sql.*;
import java.util.UUID;

import fr.sukikui.hardcoreclaimmanager.HardcoreClaimManager;
import fr.sukikui.hardcoreclaimmanager.claim.Claim;
import fr.sukikui.hardcoreclaimmanager.player.PlayerData;
import fr.sukikui.hardcoreclaimmanager.player.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;

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
                    "claimID LONG NOT NULL PRIMARY KEY AUTOINCREMENT," +
                    "worldName TEXT UNIQUE NOT NULL," +
                    "isAdmin BOOLEAN NOT NULL," +
                    "corner1X INTEGER NOT NULL," +
                    "corner1Y INTEGER NOT NULL," +
                    "corner1Z INTEGER NOT NULL," +
                    "corner2X INTEGER NOT NULL," +
                    "corner2Y INTEGER NOT NULL," +
                    "corner2Z INTEGER NOT NULL," +
                    "playerName TEXT NOT NULL," +
                    "playerUUID TEXT NOT NULL," +
                    "FOREIGN KEY (playerName,playerUUID) REFERENCES Player(playerName,playerUUID) ON DELETE CASCADE," +
                    "FOREIGN KEY (worldName) REFERENCES World(worldName) ON DELETE CASCADE," +
                    "CONSTRAINT corner1 UNIQUE (worldName,corner1X,corner1Y,corner1Z)," +
                    "CONSTRAINT corner2 UNIQUE (worldName,corner2X,corner2Y,corner2Z)," +
                    "CHECK (corner1X != corner2X AND corner1Z != corner2Z)," +
                    "CHECK (isAdmin IN (0,1))" +
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
                    "FOREIGN KEY (claimID) REFERENCES Claim(claimID) ON DELETE CASCADE," +
                    "FOREIGN KEY (playerName,playerUUID) REFERENCES Player(playerName,playerUUID) ON DELETE CASCADE," +
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

        Connection connection = getConnection();
        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement(insertPlayerRequest);
            statement.setString(1,playerData.getPlayerName());
            statement.setString(2,playerData.getPlayerUUID().toString());
            statement.setInt(3,playerData.getClaimBlocks());

            statement.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
                statement.close();
                connection.close();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void deletePlayer(PlayerData playerData) {
        String deletePlayerRequest = "DELETE FROM Player WHERE playerName=? AND playerUUID=?";

        Connection connection = getConnection();
        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement(deletePlayerRequest);
            statement.setString(1,playerData.getPlayerName());
            statement.setString(2,playerData.getPlayerUUID().toString());
            statement.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
                statement.close();
                connection.close();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void insertClaim(Claim claim, PlayerData playerData) {
        String insertClaimRequest = "INSERT INTO Claim (worldName,idAdmin,corner1X,corner1Y,corner1Z,corner2X,corner2Y," +
                "corner2Z,playerName,playerUUID) VALUES (?,?,?,?,?,?,?,?,?,?)";
        String insertWorldRequest = "INSERT INTO World (worldName) VALUES (?)";

        Connection connection = getConnection();
        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement(insertWorldRequest);
            statement.setString(1,claim.getCorner1().getWorld().getName());
            statement.executeUpdate();

            statement = connection.prepareStatement(insertClaimRequest);
            statement.setString(1,claim.getCorner1().getWorld().getName());
            statement.setBoolean(2,claim.isAdmin());
            statement.setInt(3,claim.getCorner1().getBlockX());
            statement.setInt(4,claim.getCorner1().getBlockY());
            statement.setInt(5,claim.getCorner1().getBlockZ());
            statement.setInt(6,claim.getCorner2().getBlockX());
            statement.setInt(7,claim.getCorner2().getBlockY());
            statement.setInt(8,claim.getCorner2().getBlockZ());
            statement.setString(9,playerData.getPlayerName());
            statement.setString(10,playerData.getPlayerUUID().toString());
            statement.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
                statement.close();
                connection.close();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void deleteClaim(Claim claim) {
        String deleteClaimRequest = "DELETE FROM CLAIM WHERE claimID=?";

        Connection connection = getConnection();
        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement(deleteClaimRequest);
            statement.setLong(1,claim.getClaimID());
            statement.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
                statement.close();
                connection.close();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void insertTrustedPlayer(PlayerData playerData, Claim claim) {
        String insertTrustedPlayerRequest = "INSERT INTO TrustedPlayers (claimID,playerName,playerUUID) VALUES (?,?,?)";

        Connection connection = getConnection();
        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement(insertTrustedPlayerRequest);
            statement.setLong(1,claim.getClaimID());
            statement.setString(2,playerData.getPlayerName());
            statement.setString(3,playerData.getPlayerUUID().toString());

            statement.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
                statement.close();
                connection.close();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void deleteTrustedPlayers(PlayerData playerData, Claim claim) {
        String deleteTrustedPlayerRequest = "DELETE FROM TrustedPlayers WHERE claimID=? AND playerName=? AND" +
                "playerUUID=?";

        Connection connection = getConnection();
        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement(deleteTrustedPlayerRequest);
            statement.setLong(1,claim.getClaimID());
            statement.setString(2,playerData.getPlayerName());
            statement.setString(3,playerData.getPlayerUUID().toString());

            statement.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
                statement.close();
                connection.close();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void getAll() {
        String selectClaimsAndPlayersRequest = "SELECT * FROM Claim INNER JOIN Player ON Player.playerName=" +
                "Claim.playerName AND Player.playerUUID=Claim.playerUUID";
        String selectAllTrustedPlayers = "SELECT * FROM TrustedPlayers";

        Connection connection = getConnection();
        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement(selectClaimsAndPlayersRequest);
            ResultSet claimsResultSet = statement.executeQuery();

            statement = connection.prepareStatement(selectAllTrustedPlayers);
            ResultSet trustedPlayersResultSet = statement.executeQuery();

            while (claimsResultSet.next()) {
                PlayerDataManager.getInstance().addNewPlayerData(claimsResultSet.getString("playerName"),
                        UUID.fromString(claimsResultSet.getString("playerUUID")),
                        claimsResultSet.getInt("claimBlocks"));
                Location corner1 = Bukkit.getWorld(claimsResultSet.getString("worldName")).getBlockAt(
                        claimsResultSet.getInt("corner1X"),
                        claimsResultSet.getInt("corner1Y"),
                        claimsResultSet.getInt("corner1Z")
                ).getLocation();
                Location corner2 = Bukkit.getWorld(claimsResultSet.getString("worldName")).getBlockAt(
                        claimsResultSet.getInt("corner2X"),
                        claimsResultSet.getInt("corner2Y"),
                        claimsResultSet.getInt("corner2Z")
                ).getLocation();
                PlayerDataManager.getInstance().createClaim(corner1,corner2,UUID.fromString(claimsResultSet.getString
                        ("playerUUID")),claimsResultSet.getBoolean("isAdmin"),
                        claimsResultSet.getLong("claimID")
                );
            }

            while (trustedPlayersResultSet.next()) {
                Claim claim = PlayerDataManager.getInstance().getClaimById(trustedPlayersResultSet.
                        getLong("claimID"));
                claim.addTrustedPlayers(trustedPlayersResultSet.getString("playerName"),
                        UUID.fromString(trustedPlayersResultSet.getString("playerUUID")));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
                statement.close();
                connection.close();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
