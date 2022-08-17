package fr.sukikui.hardcoreclaimmanager.player;

import fr.sukikui.hardcoreclaimmanager.HardcoreClaimManager;
import fr.sukikui.hardcoreclaimmanager.claim.Claim;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.UUID;

public class PlayerData {
    private String playerName;
    private UUID playerUUID;
    private int claimBlocks;
    private ArrayList<Claim> claims;
    private Location lastToolLocation;

    public PlayerData(String playerName, UUID playerUUID) {
        this.playerUUID = playerUUID;
        this.playerName = playerName;
        HardcoreClaimManager hardcoreClaimManager = (HardcoreClaimManager) Bukkit.getServer().getPluginManager().getPlugin("HardcoreClaimManager");
        this.claimBlocks = Integer.parseInt(hardcoreClaimManager.getProperties().getProperty("default-claim-blocks"));
        this.claims = new ArrayList<>();
    }

    public void addClaimBlocks(int amount) {
        this.claimBlocks += amount;
    }

    public void removeClaimBlocks(int amount) {
        if (this.claimBlocks - amount > 0) {
            this.claimBlocks -= amount;
        }
    }

    public boolean isOwned(Claim claim) {
        if (this.claims.contains(claim)) {
            return true;
        }
        else {
            return false;
        }
    }

    public String getPlayerName() {
        return this.playerName;
    }

    public UUID getPlayerUUID() {
        return this.playerUUID;
    }

    public int getClaimBlocks() {
        return this.claimBlocks;
    }

    public Location getLastToolLocation() {
        return lastToolLocation;
    }

    public void updateClaims() {
        for (Claim claim : PlayerDataManager.getInstance().getClaims()) {
            if (claim.getOwnerUUID().equals(this.playerUUID) && !this.claims.contains(claim)) {
                this.claims.add(claim);
            }
        }
    }

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
