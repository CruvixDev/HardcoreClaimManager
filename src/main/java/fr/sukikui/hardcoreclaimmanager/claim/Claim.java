package fr.sukikui.hardcoreclaimmanager.claim;

import fr.sukikui.hardcoreclaimmanager.player.PlayerData;
import fr.sukikui.hardcoreclaimmanager.player.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.UUID;

public class Claim {
    private long claimID;
    private UUID ownerUUID;
    private boolean isAdmin;
    private ArrayList<String> trustedPlayers;
    private Location corner1;
    private Location corner2;

    public Claim(Location corner1, Location corner2, UUID ownerUUID, boolean isAdmin) {
        this.ownerUUID = ownerUUID;
        this.isAdmin = isAdmin;
        this.corner1 = corner1;
        this.corner2 = corner2;
        this.trustedPlayers = new ArrayList<>();
    }

    public boolean isAllowed(String playerName) {
        if (this.trustedPlayers.contains(playerName)) {
            return true;
        }
        return false;
    }

    public boolean isAllowed(UUID playerUUID) {
        String playerName = Bukkit.getServer().getPlayer(playerUUID).getName();
        if (this.trustedPlayers.contains(playerName)) {
            return true;
        }
        return false;
    }

    public void addTrustedPlayers(String playerToTrustName, UUID trustPlayer) {
        PlayerData trustPlayerData = PlayerDataManager.getInstance().getPlayerDataByUUID(trustPlayer);
        if (!trustPlayerData.isOwned(this)) {
            return;
        }
        PlayerData playerToTrustData = PlayerDataManager.getInstance().getPlayerDataByName(playerToTrustName);
        if (playerToTrustData != null && trustPlayerData != null) {
            this.trustedPlayers.add(playerToTrustName);
        }
    }

    public void removeTrustedPlayers(String playerName, UUID trustPlayer) {
        if (!trustPlayer.equals(this.ownerUUID)) {
            return;
        }
        this.trustedPlayers.remove(playerName);
    }

    public int getClaimSurface() {
        return Math.abs(this.corner2.getBlockX() - this.corner1.getBlockX()) * Math.abs(this.corner2.getBlockZ() - this.corner1.getBlockZ());
    }

    public long getClaimID() {
        return this.claimID;
    }

    public boolean isAdmin() {
        return this.isAdmin;
    }

    public static boolean isInSurface(Location point, Location point1, Location point2) {
        boolean isInSurface = false;

        int x = point.getBlockX();
        int z = point.getBlockZ();
        int xmax = 0;
        int xmin = 0;
        int zmax = 0;
        int zmin = 0;

        if (point1.getBlockX() >= point2.getBlockX()){
            xmax = point1.getBlockX();
            xmin = point2.getBlockX();
        }
        else{
            xmax = point2.getBlockX();
            xmin = point1.getBlockX();
        }

        if (point1.getBlockZ() >= point2.getBlockZ()){
            zmax = point1.getBlockZ();
            zmin = point2.getBlockZ();
        }
        else{
            zmax = point2.getBlockZ();
            zmin = point1.getBlockZ();
        }

        if ((xmin <= x && x <= xmax) && (zmin <= z && z <= zmax)){
            isInSurface = true;
        }
        return isInSurface;
    }

    public Location getCorner1() {
        return this.corner1;
    }

    public Location getCorner2() {
        return corner2;
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Claim) {
            Claim claim = (Claim) obj;
            if (Claim.isInSurface(claim.getCorner1(),this.corner1,this.corner2) &&
                    Claim.isInSurface(claim.getCorner2(),this.corner1,this.corner2)) {
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

    @Override public String toString() {
        return String.format("[Claim at (%d,%d) and (%d,%d) in the world \"%s\"]\n",corner1.getBlockX(),corner1.getBlockZ(),corner2.getBlockX(),corner2.getBlockZ(),corner1.getWorld().getName());
    }
}
