package fr.sukikui.hardcoreclaimmanager.claim;

import fr.sukikui.hardcoreclaimmanager.Messages;
import fr.sukikui.hardcoreclaimmanager.player.PlayerData;
import fr.sukikui.hardcoreclaimmanager.player.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Class representing a claim protection in a world
 */
public class Claim {
    private long claimID;
    private UUID ownerUUID;
    private boolean isAdmin;
    private ArrayList<String> trustedPlayers;
    private Location corner1;
    private Location corner2;

    public Claim(Location corner1, Location corner2, UUID ownerUUID, boolean isAdmin, long claimId) {
        this.claimID = claimId;
        this.ownerUUID = ownerUUID;
        this.isAdmin = isAdmin;
        this.corner1 = corner1;
        this.corner2 = corner2;
        this.trustedPlayers = new ArrayList<>();
    }

    /**
     * Method which tests if a player is allowed to interact with this claim
     * @param playerName the name of the player to test
     * @return true if the player is allowed or false if the player is not allowed or if the playerName is the owner
     */
    public boolean isAllowed(String playerName) {
        return this.trustedPlayers.contains(playerName);
    }

    /**
     * Method which tests if a player is allowed to interact with this claim
     * @param playerUUID the UUID of the player to test
     * @return true if the player is allowed or false if the player is not allowed or if the playerUUID is the owner
     */
    public boolean isAllowed(UUID playerUUID) {
        String playerName = Bukkit.getServer().getPlayer(playerUUID).getName();
        return this.trustedPlayers.contains(playerName);
    }

    /**
     * Add a player into the trusted players list allowed to interact with this claim
     * @param playerToTrustName the name of the player to trust in this claim
     * @param trustPlayer the UUID of the player which asking to trust a player
     */
    public boolean addTrustedPlayers(String playerToTrustName, UUID trustPlayer) {
        PlayerData playerToTrustData = PlayerDataManager.getInstance().getPlayerDataByName(playerToTrustName);
        if (playerToTrustData != null && !trustedPlayers.contains(playerToTrustName)) {
            if (Bukkit.getPlayer(trustPlayer) != null && Bukkit.getPlayer(trustPlayer).isOp()) {
                if (!playerToTrustData.getPlayerUUID().equals(this.ownerUUID)) {
                    this.trustedPlayers.add(playerToTrustName);
                    return true;
                }
            }
            else {
                if (!trustPlayer.equals(this.ownerUUID)) {
                    return false;
                }
                if (!trustPlayer.equals(playerToTrustData.getPlayerUUID())) {
                    this.trustedPlayers.add(playerToTrustName);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Remove a player from trusted players list allowed to interact with this claim
     * @param playerName the name of the player to un-trust in this claim
     * @param trustPlayer the UUID of the player asking to un-trust a player
     */
    public boolean removeTrustedPlayers(String playerName, UUID trustPlayer) {
        if (!Bukkit.getPlayer(trustPlayer).isOp()) {
            if (!trustPlayer.equals(this.ownerUUID)) {
                return false;
            }
        }
        this.trustedPlayers.remove(playerName);
        return true;
    }

    /**
     * Compute the claim's surface
     * @return the claim's surface
     */
    public int getClaimSurface() {
        return (Math.abs(this.corner2.getBlockX() - this.corner1.getBlockX()) + 1) * (Math.abs(this.corner2.getBlockZ()
                - this.corner1.getBlockZ()) + 1);
    }

    /**
     *
     * @return the claim ID
     */
    public long getClaimID() {
        return this.claimID;
    }

    /**
     * Verify if this claim is an admin claim
     * @return true if the claim is an admin claim false otherwise
     */
    public boolean isAdmin() {
        return this.isAdmin;
    }

    /**
     * Utility method which verify if a given point is in the surface delimited by point1 and point2
     * @param point the point to verify
     * @param point1 the first corner of the rectangular surface
     * @param point2 the second corner of the rectangular surface
     * @return true if point is in the surface delimited by point2 and point2, false otherwise
     */
    public static boolean isInSurface(Location point, Location point1, Location point2) {
        boolean isInSurface = false;

        int x = point.getBlockX();
        int z = point.getBlockZ();
        int xmax;
        int xmin;
        int zmax;
        int zmin;

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

    /**
     * Compute the width of the claim
     * @return the claim width
     */
    public int getWidth() {
       return Math.abs(corner2.getBlockX() - corner1.getBlockX());
    }

    /**
     * Compute the claim height
     * @return the claim height
     */
    public int getHeight() {
        return Math.abs(corner2.getBlockZ() - corner1.getBlockZ());
    }

    /**
     *
     * @return the first corner delimiting the claim
     */
    public Location getCorner1() {
        return this.corner1;
    }

    /**
     *
     * @return the second corner delimiting the claim
     */
    public Location getCorner2() {
        return this.corner2;
    }

    /**
     *
     * @return the owner UUID
     */
    public UUID getOwnerUUID() {
        return this.ownerUUID;
    }

    /**
     * Method to return all trusted players
     * @return return an unmodifiable list of all trusted players
     */
    public List<String> getTrustedPlayers() {
        return Collections.unmodifiableList(this.trustedPlayers);
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
        if (corner1.getWorld() != null) {
            return String.format(Messages.getMessages("claim_to_string") + "\n",corner1.getBlockX(),corner1.
                    getBlockZ(),corner2.getBlockX(),corner2.getBlockZ(),corner1.getWorld().getName());
        }
        else {
            return Messages.getMessages("world_unload");
        }
    }
}
