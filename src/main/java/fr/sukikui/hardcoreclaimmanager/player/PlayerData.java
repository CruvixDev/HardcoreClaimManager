package fr.sukikui.hardcoreclaimmanager.player;

import fr.sukikui.hardcoreclaimmanager.HardcoreClaimManager;
import fr.sukikui.hardcoreclaimmanager.claim.Claim;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Class storing all data related to players for this plugin
 */
public class PlayerData {
    private String playerName;
    private UUID playerUUID;
    private float claimBlocks;
    private long joinDate;
    private long lastSaveBlocksGain;
    private ArrayList<Claim> claims;
    private Location lastToolLocation;

    public PlayerData(String playerName, UUID playerUUID) {
        this.playerUUID = playerUUID;
        this.playerName = playerName;
        HardcoreClaimManager hardcoreClaimManager = (HardcoreClaimManager) Bukkit.getServer().getPluginManager().getPlugin("HardcoreClaimManager");
        this.claimBlocks = Integer.parseInt(hardcoreClaimManager.getProperties().getProperty("default-claim-blocks"));
        this.claims = new ArrayList<>();
        this.joinDate = System.currentTimeMillis();
        this.lastSaveBlocksGain = this.joinDate;
    }

    public PlayerData(String playerName, UUID playerUUID, float claimBlocks) {
        this.playerUUID = playerUUID;
        this.playerName = playerName;
        this.claimBlocks = claimBlocks;
        this.claims = new ArrayList<>();
        this.joinDate = System.currentTimeMillis();
        this.lastSaveBlocksGain = this.joinDate;
    }

    /**
     * Method adding a certain amount of blocks of claim for a specific player
     * @param amount the number of blocks of claim to add
     */
    public void addClaimBlocks(float amount) {
        this.claimBlocks += amount;
    }

    public void removeClaimBlocks(float amount) {
        if (this.claimBlocks - amount > 0) {
            this.claimBlocks -= amount;
        }
    }

    /**
     * Verify if the specified claim is owned by this player
     * @param claim the claim to verify
     * @return true if the claim is owned by this player, false otherwise
     */
    public boolean isOwned(Claim claim) {
        if (this.claims.contains(claim)) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     *
     * @return the player name
     */
    public String getPlayerName() {
        return this.playerName;
    }

    /**
     *
     * @return the player UUID
     */
    public UUID getPlayerUUID() {
        return this.playerUUID;
    }

    /**
     *
     * @return the player join date (Timestamp)
     */
    public long getJoinDate() {
        return joinDate;
    }

    /**
     *
     * @return the last time blocks of claim has been attributed to the player
     */
    public long getLastSaveBlocksGain() {
        return this.lastSaveBlocksGain;
    }

    /**
     *
     * @return the amount of blocks of claim a player has
     */
    public int getClaimBlocks() {
        return (int) this.claimBlocks;
    }

    /**
     *
     * @return the last location where the player left click with the default tool selector
     */
    public Location getLastToolLocation() {
        return this.lastToolLocation;
    }

    /**
     * Update the list of claims that the player owned according to the PlayerDataManager class
     */
    public void updateClaims() {
        this.claims = new ArrayList<>();
        for (Claim claim : PlayerDataManager.getInstance().getClaims()) {
            if (claim.getOwnerUUID().equals(this.playerUUID) && !this.claims.contains(claim)) {
                this.claims.add(claim);
            }
        }
    }

    /**
     *
     * @param lastSaveBlocksGain the last time that the player has seen his blocks of claim amount updated
     */
    public void setLastSaveBlocksGain(long lastSaveBlocksGain) {
        this.lastSaveBlocksGain = lastSaveBlocksGain;
    }

    /**
     *
     * @param location the last location where the player left click with the default tool selector
     */
    public void setLastToolLocation(Location location) {
        this.lastToolLocation = location;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PlayerData) {
            PlayerData playerData = (PlayerData) obj;
            if (playerData.getPlayerName().equals(this.playerName)) {
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }

    @Override
    public String toString() {
        String toString = String.format("The player %s own :\n",this.playerName);
        for (Claim claim : this.claims) {
            toString += claim.toString();
        }
        return toString;
    }
}
