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
        int xMax;
        int xMin;
        int zMax;
        int zMin;

        if (point1.getBlockX() >= point2.getBlockX()){
            xMax = point1.getBlockX();
            xMin = point2.getBlockX();
        }
        else{
            xMax = point2.getBlockX();
            xMin = point1.getBlockX();
        }

        if (point1.getBlockZ() >= point2.getBlockZ()){
            zMax = point1.getBlockZ();
            zMin = point2.getBlockZ();
        }
        else{
            zMax = point2.getBlockZ();
            zMin = point1.getBlockZ();
        }

        if ((xMin <= x && x <= xMax) && (zMin <= z && z <= zMax)){
            isInSurface = true;
        }
        return isInSurface;
    }

    public static boolean isRiding(Claim claim1, Claim claim2) {
        Claim littleClaim;
        Claim bigClaim;
        ArrayList<Location> points = new ArrayList<>();
        int xMax;
        int xMin;
        int zMax;
        int zMin;

        if (claim1.getClaimSurface() <= claim2.getClaimSurface()) {
            littleClaim = claim1;
            bigClaim = claim2;
        }
        else {
            littleClaim = claim2;
            bigClaim = claim1;
        }

        Location point1 = littleClaim.getCorner1();
        Location point2 = littleClaim.getCorner2();
        Location point3 = new Location(point1.getWorld(),point2.getBlockX(),point1.getBlockY(),point1.getBlockZ());
        Location point4 = new Location(point1.getWorld(),point1.getBlockX(),point1.getBlockY(),point2.getBlockZ());

        points.add(point1);
        points.add(point2);
        points.add(point3);
        points.add(point4);

        if (bigClaim.getCorner1().getBlockX() >= bigClaim.getCorner2().getBlockX()){
            xMax = bigClaim.getCorner1().getBlockX();
            xMin = bigClaim.getCorner2().getBlockX();
        }
        else{
            xMax = bigClaim.getCorner2().getBlockX();
            xMin = bigClaim.getCorner1().getBlockX();
        }

        if (bigClaim.getCorner1().getBlockZ() >= bigClaim.getCorner2().getBlockZ()){
            zMax = bigClaim.getCorner1().getBlockZ();
            zMin = bigClaim.getCorner2().getBlockZ();
        }
        else{
            zMax = bigClaim.getCorner2().getBlockZ();
            zMin = bigClaim.getCorner1().getBlockZ();
        }

        for (Location point :points) {
            if ((xMin <= point.getBlockX() && point.getBlockX() <= xMax) && (zMin <= point.getBlockZ() &&
                    point.getBlockZ() <= zMax)){
                return true;
            }
        }
        return false;
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
