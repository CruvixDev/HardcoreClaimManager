package fr.sukikui.hardcoreclaimmanager.player;

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

    public String createClaim(Location corner1, Location corner2, UUID playerUUID) {
        Claim claim = new Claim(corner1,corner2,playerUUID,null);
        PlayerData playerData = getPlayerDataByUUID(playerUUID);
        String reason = "";
        if (claim.getCorner1().getWorld().equals(claim.getCorner2().getWorld())) {
            if (!isRiding(claim)) {
                if (claim.getClaimSurface() < playerData.getClaimBlocks()) {
                    claims.add(claim);
                    reason = ChatColor.GREEN + "Claim successfully added!";
                }
                else {
                    reason = ChatColor.RED + "You have not enough claim blocks to claim this area";
                }
            }
            else {
                reason = ChatColor.RED + "The claim is riding another claim!";
            }
        }
        else {
            reason = ChatColor.RED + "The two corners are not in the same world!";
        }
        return reason;
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
            if (playerData.getPlayerName().equals(playerName));
            return playerData;
        }
        return null;
    }

    public PlayerData getPlayerDataByUUID(UUID playerUUID) {
        for (PlayerData playerData : playersData) {
            if (playerData.getPlayerUUID().equals(playerUUID));
            return playerData;
        }
        return null;
    }

    public PlayerData getPlayerDataByClaim(Claim claim) {
        for (PlayerData playerData : playersData) {
            if (playerData.getPlayerUUID().equals(claim.getOwnerUUID())) {
                return playerData;
            }
        }
        return null;
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
