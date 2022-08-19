package fr.sukikui.hardcoreclaimmanager.claim;

import fr.sukikui.hardcoreclaimmanager.HardcoreClaimManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;

public class ClaimBoundariesVisualisation {
    private static ClaimBoundariesVisualisation claimBoundariesVisualisation;
    private ArrayList<String> playerSources = new ArrayList<>();
    private Location corner1;
    private Location corner2;

    private ClaimBoundariesVisualisation(Location corner1, Location corner2) {
        this.corner1 = corner1;
        this.corner2 = corner2;
    }

    public static ClaimBoundariesVisualisation getInstance(Location corner1, Location corner2) {
        if (claimBoundariesVisualisation == null) {
            claimBoundariesVisualisation = new ClaimBoundariesVisualisation(corner1,corner2);
        }
        return claimBoundariesVisualisation;
    }

    public void startVisualisationTask(String playerSource) {
        Player player = Bukkit.getPlayer(playerSource);
        if (!this.playerSources.contains(playerSource)) {
            if (player != null) {
                playerSources.add(playerSource);
                player.sendBlockChange(this.corner1, Material.GOLD_BLOCK.createBlockData());
                player.sendBlockChange(this.corner2, Material.GOLD_BLOCK.createBlockData());
                BukkitScheduler scheduler = Bukkit.getScheduler();
                HardcoreClaimManager hardcoreClaimManager = (HardcoreClaimManager) Bukkit.getPluginManager().getPlugin("HardcoreClaimManager");
                Runnable task = () -> {
                    player.sendBlockChange(corner1,Material.AIR.createBlockData());
                    player.sendBlockChange(corner2,Material.AIR.createBlockData());
                    playerSources.remove(playerSource);
                };
                scheduler.scheduleSyncDelayedTask(hardcoreClaimManager,task,20L * 15);
            }
        }
        else {
            player.sendMessage(ChatColor.RED + "You already visualise a claim!");
        }
    }
}
