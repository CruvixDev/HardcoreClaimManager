package fr.sukikui.hardcoreclaimmanager.listener;

import fr.sukikui.hardcoreclaimmanager.claim.Claim;
import fr.sukikui.hardcoreclaimmanager.player.PlayerData;
import fr.sukikui.hardcoreclaimmanager.player.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * Class handling all events related to claim
 */
public class ClaimEventHandler implements Listener {

    @EventHandler
    public void onBlockPlaced(BlockPlaceEvent e) {
        Location blockLocation = e.getBlockPlaced().getLocation();
        PlayerData playerData = PlayerDataManager.getInstance().getPlayerDataByName(e.getPlayer().getName());

        Claim claimConcerned = PlayerDataManager.getInstance().getClaimAt(blockLocation);
        if (claimConcerned == null || playerData ==  null) {
            return;
        }
        if (!playerData.isOwned(claimConcerned) && !claimConcerned.isAllowed(playerData.getPlayerUUID())) {
            e.setCancelled(true);
            OfflinePlayer player = (Bukkit.getPlayer(claimConcerned.getOwnerUUID()) == null) ? Bukkit.getOfflinePlayer
                    (claimConcerned.getOwnerUUID()) : Bukkit.getPlayer(claimConcerned.getOwnerUUID());
            e.getPlayer().sendMessage(ChatColor.RED + "You are not allowed to place block here (claim is owned by " +
                    player.getName());
        }
    }

    @EventHandler
    public void onBlockBroken(BlockBreakEvent e) {
        Location blockLocation = e.getBlock().getLocation();
        PlayerData playerData = PlayerDataManager.getInstance().getPlayerDataByName(e.getPlayer().getName());

        Claim claimConcerned = PlayerDataManager.getInstance().getClaimAt(blockLocation);
        if (claimConcerned == null || playerData ==  null) {
            return;
        }
        if (!playerData.isOwned(claimConcerned) && !claimConcerned.isAllowed(playerData.getPlayerUUID())) {
            e.setCancelled(true);
            OfflinePlayer player = (Bukkit.getPlayer(claimConcerned.getOwnerUUID()) == null) ? Bukkit.getOfflinePlayer
                    (claimConcerned.getOwnerUUID()) : Bukkit.getPlayer(claimConcerned.getOwnerUUID());
            e.getPlayer().sendMessage(ChatColor.RED + "You are not allowed to break block here (claim is owned by " +
                    player.getName());
        }
    }
}
