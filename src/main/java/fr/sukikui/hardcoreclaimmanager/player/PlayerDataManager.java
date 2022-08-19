package fr.sukikui.hardcoreclaimmanager.player;

import fr.sukikui.hardcoreclaimmanager.HardcoreClaimManager;
import fr.sukikui.hardcoreclaimmanager.claim.Claim;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.UUID;

public class PlayerDataManager {
    private static PlayerDataManager playerDataManager;
    private ArrayList<PlayerData> playersData;
    private ArrayList<Claim> claims;

    private PlayerDataManager() {
        this.playersData = new ArrayList<>();
        this.claims = new ArrayList<>();
    }

    public static PlayerDataManager getInstance() {
        if (playerDataManager == null) {
            playerDataManager = new PlayerDataManager();
        }
        return playerDataManager;
    }

    public String createClaim(Location corner1, Location corner2, UUID playerUUID, boolean isAdmin) {
        Claim claim = new Claim(corner1,corner2,playerUUID,isAdmin);
        PlayerData playerData = getPlayerDataByUUID(playerUUID);
        String reason = "";
        if (playerData == null) {
            reason = ChatColor.RED + "The player does not exists!";
            return reason;
        }
        if (!claim.getCorner1().getWorld().equals(claim.getCorner2().getWorld())) {
            reason = ChatColor.RED + "The two corners are not in the same world!";
            return reason;
        }
        if (isRiding(claim)) {
            reason = ChatColor.RED + "The claim is riding another claim!";
            return reason;
        }
        HardcoreClaimManager hardcoreClaimManager = (HardcoreClaimManager) Bukkit.getPluginManager().getPlugin("HardcoreClaimManager");
        int claimMinSurface = 0;
        try {
            claimMinSurface = Integer.parseInt(hardcoreClaimManager.getProperties().getProperty("min-claim-size"));
        }
        catch (NumberFormatException e) {
            reason = ChatColor.RED + "The parameter min-claim-size is not valid!";
            return reason;
        }
        if (claim.getCorner1().getBlockX() - claim.getCorner2().getBlockX() == 0 || claim.getCorner1().getBlockZ() - claim.getCorner2().getBlockZ() == 0) {
            reason = ChatColor.RED + "The claim is not valid!";
            return reason;
        }
        if (claim.getClaimSurface() < claimMinSurface) {
            reason = ChatColor.RED + "The claim is too small! Min size is: " + claimMinSurface;
            return reason;
        }
        if (!this.claims.contains(claim)) {
            if (isAdmin) {
                this.claims.add(claim);
                playerData.updateClaims();
                reason = ChatColor.GREEN + "Claim successfully added! (admin)";
            }
            else if (claim.getClaimSurface() < playerData.getClaimBlocks()){
                this.claims.add(claim);
                playerData.updateClaims();
                playerData.removeClaimBlocks(claim.getClaimSurface());
                reason = ChatColor.GREEN + "Claim successfully added!";
            }
        }
        return reason;
    }

    public void removeClaim(Claim claim, String playerName) {
        PlayerData playerData = getPlayerDataByName(playerName);
        if (playerData != null && playerData.isOwned(claim)) {
            this.claims.remove(claim);
        }
    }

    public void addNewPlayerData(String playerName, UUID playerUUID) {
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
            PlayerData playerData = new PlayerData(playerName,playerUUID);
            if (!playersData.contains(playerData)) {
                playersData.add(playerData);
            }
        }
    }

    public PlayerData getPlayerDataByName(String playerName) {
        for (PlayerData playerData : playersData) {
            if (playerData.getPlayerName().equals(playerName)) {
                return playerData;
            }
        }
        return null;
    }

    public PlayerData getPlayerDataByUUID(UUID playerUUID) {
        for (PlayerData playerData : playersData) {
            if (playerData.getPlayerUUID().equals(playerUUID)) {
                return playerData;
            }
        }
        return null;
    }

    public ArrayList<Claim> getClaims() {
        return this.claims;
    }

    public Claim getClaimAt(Location location) {
        for (Claim claim : claims) {
            if (Claim.isInSurface(location,claim.getCorner1(),claim.getCorner2())) {
                return claim;
            }
        }
        return null;
    }

    public boolean isRiding(Claim claimToVerify) {
        boolean isRiding = false;
        for (Claim claim : this.claims) {
            if (Claim.isInSurface(claimToVerify.getCorner1(),claim.getCorner1(),claim.getCorner2()) ||
            Claim.isInSurface(claimToVerify.getCorner2(),claim.getCorner1(),claim.getCorner2())) {
                isRiding = true;
            }
        }
        return isRiding;
    }
}
