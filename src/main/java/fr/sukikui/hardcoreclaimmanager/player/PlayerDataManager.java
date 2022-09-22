package fr.sukikui.hardcoreclaimmanager.player;

import fr.sukikui.hardcoreclaimmanager.HardcoreClaimManager;
import fr.sukikui.hardcoreclaimmanager.claim.Claim;
import fr.sukikui.hardcoreclaimmanager.claim.ClaimResults;
import fr.sukikui.hardcoreclaimmanager.enums.ClaimCreationMessages;
import fr.sukikui.hardcoreclaimmanager.enums.ClaimCreationSource;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * The god class managing all claims and players data and verify their integrity before using them
 */
public class PlayerDataManager {
    private HardcoreClaimManager hardcoreClaimManager;
    private static PlayerDataManager playerDataManager;
    private ArrayList<PlayerData> playersData;
    private ArrayList<Claim> claims;

    private PlayerDataManager() {
        this.playersData = new ArrayList<>();
        this.claims = new ArrayList<>();
        this.hardcoreClaimManager = HardcoreClaimManager.getInstance();
    }

    /**
     * Method to get the unique instance of the PlayerDataManager class
     * @return the unique instance of the PlayerDataManager class
     */
    public static PlayerDataManager getInstance() {
        if (playerDataManager == null) {
            playerDataManager = new PlayerDataManager();
        }
        return playerDataManager;
    }

    /**
     * Method to create a claim with several conditions to respect in the manager
     * @param corner1 the first corner of the claim
     * @param corner2 the second corner of the claim
     * @param playerUUID the player UUID asking to create a claim
     * @param isAdmin true the claim is an admin claim, false the claim is a player claim
     * @return a string reason whether the claim is successfully created or not
     */
    public ClaimResults createClaim(Location corner1, Location corner2, UUID playerUUID, boolean isAdmin, long claimId, ClaimCreationSource source) {
        Claim claim = new Claim(corner1,corner2,playerUUID,isAdmin,claimId);
        PlayerData playerData = getPlayerDataByUUID(playerUUID);
        if (playerData == null) {
            return new ClaimResults(null,ClaimCreationMessages.PlayerDoesNotExists,claim.getClaimSurface());
        }
        if (isRiding(claim)) {
            return new ClaimResults(null,ClaimCreationMessages.ClaimsRiding,claim.getClaimSurface());
        }
        if (!claim.getCorner1().getWorld().equals(claim.getCorner2().getWorld())) {
            return new ClaimResults(null,ClaimCreationMessages.CornersNotInTheSameWorld,claim.getClaimSurface());
        }
        if (claim.getCorner1().getBlockX() - claim.getCorner2().getBlockX() == 0 || claim.getCorner1().getBlockZ() -
                claim.getCorner2().getBlockZ() == 0) {
            return new ClaimResults(null,ClaimCreationMessages.ClaimNotValid,claim.getClaimSurface());
        }
        int claimMinSurface;
        int claimMaxSurface;
        int claimMinWidth;
        try {
            claimMinSurface = Integer.parseInt(this.hardcoreClaimManager.getProperties().getProperty("min-claim-size"));
        }
        catch (NumberFormatException e) {
            claimMinSurface = 25;
        }
        try {
            claimMaxSurface = Integer.parseInt(this.hardcoreClaimManager.getProperties().getProperty("max-claim-size"));
        }
        catch (NumberFormatException e) {
            claimMaxSurface = 10000;
        }
        try {
            claimMinWidth = Integer.parseInt(this.hardcoreClaimManager.getProperties().getProperty("min-claim-width"));
        }
        catch (NumberFormatException e) {
            claimMinWidth = 5;
        }
        if (claim.getClaimSurface() < claimMinSurface) {
            return new ClaimResults(null,ClaimCreationMessages.ClaimTooSmall,claim.getClaimSurface());
        }
        if (claim.getClaimSurface() > claimMaxSurface) {
            return new ClaimResults(null,ClaimCreationMessages.ClaimTooBig,claim.getClaimSurface());
        }
        if (claim.getWidth() < claimMinWidth || claim.getHeight() < claimMinWidth) {
            return new ClaimResults(claim, ClaimCreationMessages.ClaimTooShrink, claim.getClaimSurface());
        }
        if (!this.claims.contains(claim)) {
            if (source.equals(ClaimCreationSource.PLAYER)) {
                if (isAdmin) {
                    this.claims.add(claim);
                    playerData.updateClaims();
                    return new ClaimResults(claim,ClaimCreationMessages.ClaimAdminCreated,claim.getClaimSurface());
                }
                else if (claim.getClaimSurface() <= playerData.getClaimBlocks()) {
                    this.claims.add(claim);
                    playerData.updateClaims();
                    playerData.removeClaimBlocks(claim.getClaimSurface());
                    return new ClaimResults(claim,ClaimCreationMessages.ClaimCreated,claim.getClaimSurface());
                }
                else {
                    return new ClaimResults(null,ClaimCreationMessages.NotEnoughBlock,claim.getClaimSurface());
                }
            }
            else if (source.equals(ClaimCreationSource.DATABASE)) {
                this.claims.add(claim);
                playerData.updateClaims();
            }
        }
        return null;
    }

    /**
     * Method to remove a claim in the manager
     * @param claim
     * @param playerUUID
     */
    public boolean removeClaim(Claim claim, UUID playerUUID) {
        OfflinePlayer player = (Bukkit.getPlayer(playerUUID) == null) ? Bukkit.getOfflinePlayer
                (playerUUID) : Bukkit.getPlayer(playerUUID);
        PlayerData playerData;
        if (Bukkit.getServer().getOperators().contains(player)) {
            playerData = getPlayerDataByUUID(claim.getOwnerUUID());
        }
        else {
            playerData = getPlayerDataByUUID(playerUUID);
        }
        if (playerData != null && playerData.isOwned(claim)) {
            this.claims.remove(claim);
            playerData.updateClaims();
            if (!claim.isAdmin()) {
                playerData.addClaimBlocks(claim.getClaimSurface());
            }
            return true;
        }
        return false;
    }

    /**
     * Add a new player in the manager
     * @param playerName the name of the player
     * @param playerUUID the UUID of the player
     */
    public PlayerData addNewPlayerData(String playerName, UUID playerUUID, long lastJoinDate) {
        boolean exists = false;
        if (Bukkit.getServer().getPlayer(playerName) != null) {
            exists = true;
        }
        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            if (offlinePlayer.getName().equals(playerName)) {
                exists = true;
                break;
            }
        }
        if (exists) {
            PlayerData playerData = new PlayerData(playerName,playerUUID, lastJoinDate);
            if (!playersData.contains(playerData)) {
                playersData.add(playerData);
                return playerData;
            }
        }
        return null;
    }

    public PlayerData addNewPlayerData(String playerName, UUID playerUUID, float claimBlocks, long lastJoinDate) {
        boolean exists = false;
        if (Bukkit.getServer().getPlayer(playerName) != null) {
            exists = true;
        }
        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            if (offlinePlayer.getName().equals(playerName)) {
                exists = true;
                break;
            }
        }
        if (exists) {
            PlayerData playerData = new PlayerData(playerName,playerUUID,claimBlocks,lastJoinDate);
            if (!playersData.contains(playerData)) {
                playersData.add(playerData);
                return playerData;
            }
        }
        return null;
    }

    /**
     * Return the PlayerData of the player by name
     * @param playerName the name of the player
     * @return the PlayerData of this player
     */
    public PlayerData getPlayerDataByName(String playerName) {
        for (PlayerData playerData : playersData) {
            if (playerData.getPlayerName().equals(playerName)) {
                return playerData;
            }
        }
        return null;
    }

    /**
     * Return the PlayerData of the player by UUID
     * @param playerUUID the UUID of the player
     * @return the PlayerData of this player
     */
    public PlayerData getPlayerDataByUUID(UUID playerUUID) {
        for (PlayerData playerData : playersData) {
            if (playerData.getPlayerUUID().equals(playerUUID)) {
                return playerData;
            }
        }
        return null;
    }

    /**
     *
     * @return all claims managed by the PlayerDataManager class
     */
    public List<Claim> getClaims() {
        return Collections.unmodifiableList(this.claims);
    }

    /**
     *
     * @return all player's data managed by the PlayerDataManager class
     */
    public List<PlayerData> getPlayersData() {
        return Collections.unmodifiableList(this.playersData);
    }

    /**
     * Get claim by its unique identifier
     * @param claimID the claim unique identifier
     * @return the claim with this unique identifier
     */
    public Claim getClaimById(long claimID) {
        for (Claim claim : claims) {
            if (claim.getClaimID() == claimID) {
                return claim;
            }
        }
        return null;
    }

    /**
     * Return the claim at a specific location
     * @param location the location to verify
     * @return the claim at this position or null if no claims exist at this location
     */
    public Claim getClaimAt(Location location) {
        for (Claim claim : claims) {
            if (Claim.isInSurface(location,claim.getCorner1(),claim.getCorner2())) {
                return claim;
            }
        }
        return null;
    }

    /**
     * Verify if a claim is not riding another
     * @param claimToVerify the claim to verify
     * @return true if the claim is riding another, false otherwise
     */
    public boolean isRiding(Claim claimToVerify) {
        boolean isRiding = false;
        for (Claim claim : this.claims) {
            if (Claim.isInSurface(claimToVerify.getCorner1(),claim.getCorner1(),claim.getCorner2()) ||
            Claim.isInSurface(claimToVerify.getCorner2(),claim.getCorner1(),claim.getCorner2()) ||
            Claim.isInSurface(claim.getCorner1(),claimToVerify.getCorner1(),claimToVerify.getCorner2()) ||
            Claim.isInSurface(claim.getCorner2(),claimToVerify.getCorner1(),claimToVerify.getCorner2())){
                isRiding = true;
            }
        }
        return isRiding;
    }
}
