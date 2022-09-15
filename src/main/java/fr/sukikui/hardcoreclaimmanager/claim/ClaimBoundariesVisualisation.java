package fr.sukikui.hardcoreclaimmanager.claim;

import fr.sukikui.hardcoreclaimmanager.HardcoreClaimManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;

/**
 * Class handling boundaries visualisation tasks
 */
public class ClaimBoundariesVisualisation {
    private static ClaimBoundariesVisualisation claimBoundariesVisualisation;
    private ArrayList<String> playerSources = new ArrayList<>();

    private ClaimBoundariesVisualisation() {}

    public static ClaimBoundariesVisualisation getInstance() {
        if (claimBoundariesVisualisation == null) {
            claimBoundariesVisualisation = new ClaimBoundariesVisualisation();
        }
        return claimBoundariesVisualisation;
    }

    /**
     * Method starting boundaries visualisation task for a specific player and specific locations
     * @param playerSource the player who want to start the visualisation task
     * @param corner1 the first corner of the claim
     * @param corner2 the second corner of the claim
     */
    public void startVisualisationTask(String playerSource, Location corner1, Location corner2) {
        Player player = Bukkit.getPlayer(playerSource);
        Location corner3 = new Location(corner1.getWorld(),corner2.getBlockX(),corner2.getBlockY(),corner1.getBlockZ());
        Location corner4 = new Location(corner1.getWorld(),corner1.getBlockX(),corner2.getBlockY(),corner2.getBlockZ());
        ArrayList<Location> locations = new ArrayList<>();
        locations.add(corner1);
        locations.add(corner2);
        locations.add(corner3);
        locations.add(corner4);
        if (!this.playerSources.contains(playerSource) && player != null) {
            playerSources.add(playerSource);
            player.sendBlockChange(corner1, Material.GLOWSTONE.createBlockData());
            player.sendBlockChange(corner2, Material.GLOWSTONE.createBlockData());
            player.sendBlockChange(corner3, Material.GLOWSTONE.createBlockData());
            player.sendBlockChange(corner4, Material.GLOWSTONE.createBlockData());
            BukkitScheduler scheduler = Bukkit.getScheduler();
            HardcoreClaimManager hardcoreClaimManager = (HardcoreClaimManager) Bukkit.getPluginManager().
                    getPlugin("HardcoreClaimManager");
            Runnable task = () -> {
                player.sendBlockChange(corner1, corner1.getBlock().getBlockData());
                player.sendBlockChange(corner2, corner2.getBlock().getBlockData());
                player.sendBlockChange(corner3, corner3.getBlock().getBlockData());
                player.sendBlockChange(corner4, corner4.getBlock().getBlockData());
                playerSources.remove(playerSource);
            };
            scheduler.scheduleSyncDelayedTask(hardcoreClaimManager,task,20L * 15);
        }
    }
}
