package fr.sukikui.hardcoreclaimmanager.claim;

import fr.sukikui.hardcoreclaimmanager.player.PlayerData;
import fr.sukikui.hardcoreclaimmanager.player.PlayerDataManager;
import org.bukkit.Location;

import java.util.ArrayList;

public class Claim {
    private ArrayList<PlayerData> trustedPlayers;
    private Location corner1;
    private Location corner2;

    public Claim(Location corner1, Location corner2) {
        this.corner1 = corner1;
        this.corner2 = corner2;
        this.trustedPlayers = new ArrayList<>();
    }

    public boolean isAllowed(PlayerData playerData) {
        if (this.trustedPlayers.contains(playerData)) {
            return true;
        }
        return false;
    }

    public void addTrustedPlayers(ArrayList<PlayerData> playersData) {
        //TODO verify that a the player data is valid (see if it is better to have array list of string instead for parameter
        PlayerData playerOwner = PlayerDataManager.getInstance().getPlayerDataByClaim(this);
        if (playerOwner != null) {
            playersData.remove(playerOwner);
        }
        for (PlayerData playerData : playersData) {
            if (!this.trustedPlayers.contains(playerData)) {
                this.trustedPlayers.add(playerData);
            }
        }
    }

    public void removeTrustedPlayers(ArrayList<PlayerData> playersData) {
        this.trustedPlayers.removeAll(playersData);
    }

    public int claimSurface() {
        return Math.abs(this.corner2.getBlockX() - this.corner1.getBlockX()) * Math.abs(this.corner2.getBlockZ() - this.corner1.getBlockZ());
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

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Claim) {
            Claim claim = (Claim) obj;
            if (this.corner1.equals(claim.getCorner1()) && this.corner2.equals(claim.getCorner2())) {
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
        return String.format("[Claim at (%d,%d) in the world %s]\n",corner1.getBlockX(),corner2.getBlockZ(),corner1.getWorld().getName());
    }
}
