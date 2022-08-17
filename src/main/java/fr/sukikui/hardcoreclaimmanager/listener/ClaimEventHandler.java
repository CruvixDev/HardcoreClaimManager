package fr.sukikui.hardcoreclaimmanager.listener;

import fr.sukikui.hardcoreclaimmanager.claim.Claim;
import fr.sukikui.hardcoreclaimmanager.player.PlayerData;
import fr.sukikui.hardcoreclaimmanager.player.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

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
            e.getPlayer().sendMessage(ChatColor.RED + "You are not allowed to place block here (claim is owned by " +
                    Bukkit.getPlayer(claimConcerned.getOwnerUUID()).getName());
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
            e.getPlayer().sendMessage(ChatColor.RED + "You are not allowed to break block here (claim is owned by " +
                    Bukkit.getPlayer(claimConcerned.getOwnerUUID()).getName());
        }
    }
}
