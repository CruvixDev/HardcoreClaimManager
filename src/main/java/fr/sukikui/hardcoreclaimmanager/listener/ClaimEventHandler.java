package fr.sukikui.hardcoreclaimmanager.listener;

import fr.sukikui.hardcoreclaimmanager.claim.Claim;
import fr.sukikui.hardcoreclaimmanager.player.PlayerData;
import fr.sukikui.hardcoreclaimmanager.player.PlayerDataManager;
import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.Arrays;
import java.util.List;

/**
 * Class handling all events related to claim
 */
public class ClaimEventHandler implements Listener {
    private List<Material> SHRIEKER_TOOL = Arrays.asList(
            Material.WOODEN_HOE,
            Material.STONE_HOE,
            Material.IRON_HOE,
            Material.GOLDEN_HOE,
            Material.DIAMOND_HOE,
            Material.NETHERITE_HOE
    );

    private List<Material> AUTHORIZED_BLOCKS = Arrays.asList(
            Material.SCULK_SHRIEKER,
            Material.TNT
    );

    @EventHandler
    public void onBlockPlaced(BlockPlaceEvent e) {
        Location blockLocation = e.getBlockPlaced().getLocation();
        PlayerData playerData = PlayerDataManager.getInstance().getPlayerDataByName(e.getPlayer().getName());

        Claim claimConcerned = PlayerDataManager.getInstance().getClaimAt(blockLocation);
        if (claimConcerned == null || playerData ==  null) {
            return;
        }
        if (!playerData.isOwned(claimConcerned) && !claimConcerned.isAllowed(playerData.getPlayerUUID()) &&
        !AUTHORIZED_BLOCKS.contains(e.getBlock().getType())) {
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
            if (AUTHORIZED_BLOCKS.contains(e.getBlock().getType())) {
                if (e.getBlock().getType() == Material.SCULK_SHRIEKER
                        && !SHRIEKER_TOOL.contains(e.getPlayer().getInventory().getItemInMainHand().getType())) {
                    e.setCancelled(true);
                    return;
                }
                return;
            }
            e.setCancelled(true);
            OfflinePlayer player = (Bukkit.getPlayer(claimConcerned.getOwnerUUID()) == null) ? Bukkit.getOfflinePlayer
                    (claimConcerned.getOwnerUUID()) : Bukkit.getPlayer(claimConcerned.getOwnerUUID());
            e.getPlayer().sendMessage(ChatColor.RED + "You are not allowed to break block here (claim is owned by " +
                    player.getName());
        }
    }
}
