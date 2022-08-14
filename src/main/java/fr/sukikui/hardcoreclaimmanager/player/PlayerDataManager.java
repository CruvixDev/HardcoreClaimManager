package fr.sukikui.hardcoreclaimmanager.player;

import fr.sukikui.hardcoreclaimmanager.claim.Claim;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;

public class PlayerDataManager {
    private static PlayerDataManager playerDataManager;
    private ArrayList<PlayerData> playersData;

    private PlayerDataManager() {
        this.playersData = new ArrayList<>();
    }

    public static PlayerDataManager getInstance() {
        if (playerDataManager == null) {
            playerDataManager = new PlayerDataManager();
        }
        return playerDataManager;
    }

    public String createClaim(Claim claim, PlayerData playerData) {
        String reason = "";
        if (claim.getCorner1().getWorld().equals(claim.getCorner2().getWorld())) {
            if (!isRiding(claim)) {
                if (claim.claimSurface() < playerData.getClaimBlocks()) {
                    playerData.addClaim(claim);
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

    public void addNewPlayerData(PlayerData playerData) {
        boolean exists = false;
        if (Bukkit.getServer().getPlayer(playerData.getPlayerName()) != null) {
            exists = true;
        }
        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            if (offlinePlayer.getName().equals(playerData.getPlayerName())) {
                exists = true;
                break;
            }
        }
        //TODO verify if we don't put claim completely false
        if (exists) {
            if (playersData.contains(playerData)) {
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

    public PlayerData getPlayerDataByClaim(Claim claim) {
        for (PlayerData playerData : playersData) {
            for (Claim cl : playerData.getClaims()) {
                if (cl.equals(claim)) {
                    return playerData;
                }
            }
        }
        return null;
    }

    public boolean isRiding(Claim claimToVerify) {
        boolean isRiding = false;
        ArrayList<Claim> claims = getAllClaims();
        for (Claim claim : claims) {
            if (Claim.isInSurface(claimToVerify.getCorner1(),claim.getCorner1(),claim.getCorner2()) ||
            Claim.isInSurface(claimToVerify.getCorner2(),claim.getCorner1(),claim.getCorner2())) {
                isRiding = true;
            }
        }
        return isRiding;
    }

    public Claim getClaimAt(Location location) {
        ArrayList<Claim> claims = getAllClaims();
        for (Claim claim : claims) {
            if (Claim.isInSurface(location,claim.getCorner1(),claim.getCorner2())) {
                return claim;
            }
        }
        return null;
    }

    private ArrayList<Claim> getAllClaims() {
        ArrayList<Claim> claims = new ArrayList<>();
        for (PlayerData playerData : playersData) {
            claims.addAll(playerData.getClaims());
        }
        return claims;
    }
}
