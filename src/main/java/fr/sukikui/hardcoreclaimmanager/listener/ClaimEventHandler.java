package fr.sukikui.hardcoreclaimmanager.listener;

import fr.sukikui.hardcoreclaimmanager.Messages;
import fr.sukikui.hardcoreclaimmanager.claim.Claim;
import fr.sukikui.hardcoreclaimmanager.player.PlayerData;
import fr.sukikui.hardcoreclaimmanager.player.PlayerDataManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Dispenser;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;

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
            Material.SCULK_SHRIEKER
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
            e.getPlayer().sendMessage(ChatColor.RED + String.format(Messages.getMessages(
                    "cannot_place_blocks"),player.getName()));
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

        if (e.getBlock().getType() == Material.SCULK_SHRIEKER
                && !SHRIEKER_TOOL.contains(e.getPlayer().getInventory().getItemInMainHand().getType())) {
            e.setCancelled(true);
            return;
        }

        if (!playerData.isOwned(claimConcerned) && !claimConcerned.isAllowed(playerData.getPlayerUUID())) {
            if (AUTHORIZED_BLOCKS.contains(e.getBlock().getType())) {
                return;
            }
            e.setCancelled(true);
            OfflinePlayer player = (Bukkit.getPlayer(claimConcerned.getOwnerUUID()) == null) ? Bukkit.getOfflinePlayer
                    (claimConcerned.getOwnerUUID()) : Bukkit.getPlayer(claimConcerned.getOwnerUUID());
            e.getPlayer().sendMessage(ChatColor.RED + String.format(Messages.getMessages(
                    "cannot_break_blocks"),player.getName()));
        }
    }

    @EventHandler
    public void onWaterFlowInClaim(BlockFromToEvent e) {
        if (e.getFace() == BlockFace.DOWN) return;

        Location sourceLocation = e.getBlock().getLocation();
        Location toLocation = e.getToBlock().getLocation();
        Claim sourceClaim = PlayerDataManager.getInstance().getClaimAt(sourceLocation);
        Claim toClaim = PlayerDataManager.getInstance().getClaimAt(toLocation);
        if (sourceClaim == null && toClaim != null) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockDispensed(BlockDispenseEvent e) {
        BlockData blockData = e.getBlock().getBlockData();
        if (blockData instanceof Dispenser) {
            Block block = e.getBlock().getRelative(((Dispenser) blockData).getFacing());
            Claim claim = PlayerDataManager.getInstance().getClaimAt(block.getLocation());
            if (claim != null && !Claim.isInSurface(e.getBlock().getLocation(),claim.getCorner1(),claim.getCorner2())
                    && (e.getItem().getType().equals(Material.WATER_BUCKET) || e.getItem().getType().equals(
                            Material.LAVA_BUCKET))) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e) {
        if (e.getEntity().getType().equals(EntityType.PRIMED_TNT)) {
            for (Block block : e.blockList()) {
                if (PlayerDataManager.getInstance().getClaimAt(block.getLocation()) != null) {
                    e.setCancelled(true);
                    return;
                }
            }
        }
    }
}
