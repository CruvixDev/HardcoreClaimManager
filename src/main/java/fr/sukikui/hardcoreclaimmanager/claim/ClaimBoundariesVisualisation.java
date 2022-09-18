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
import java.util.Arrays;

/**
 * Class handling boundaries visualisation tasks
 */
public class ClaimBoundariesVisualisation {
    private static ClaimBoundariesVisualisation claimBoundariesVisualisation;
    private ArrayList<String> playerSources = new ArrayList<>();
    private ArrayList<BlockFace> cardinalPoints = new ArrayList<>(Arrays.asList(
            BlockFace.NORTH,
            BlockFace.SOUTH,
            BlockFace.EAST,
            BlockFace.WEST
    ));

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
        ArrayList<Location> corners = new ArrayList<>();

        corners.add(corner1);
        corners.add(corner2);
        corners.add(corner3);
        corners.add(corner4);

        locations.addAll(addLocations2(corner1,corner3));
        locations.addAll(addLocations2(corner3,corner2));
        locations.addAll(addLocations2(corner2,corner4));
        locations.addAll(addLocations2(corner4,corner1));

        if (!this.playerSources.contains(playerSource) && player != null) {
            playerSources.add(playerSource);
            for (Location corner : corners) {
                for (BlockFace blockFace : cardinalPoints) {
                    if (Claim.isInSurface(corner.getBlock().getRelative(blockFace).getLocation(), corner1, corner2)) {
                        locations.add(corner.getBlock().getRelative(blockFace).getLocation());
                    }
                    player.sendBlockChange(corner, Material.GLOWSTONE.createBlockData());
                }
            }

            for (Location location : locations) {
                player.sendBlockChange(location, Material.GOLD_BLOCK.createBlockData());
            }
            BukkitScheduler scheduler = Bukkit.getScheduler();
            HardcoreClaimManager hardcoreClaimManager = (HardcoreClaimManager) Bukkit.getPluginManager().
                    getPlugin("HardcoreClaimManager");
            Runnable task = () -> {
                for (Location location : locations) {
                    player.sendBlockChange(location,location.getBlock().getBlockData());
                }
                for (Location corner : corners) {
                    player.sendBlockChange(corner,corner.getBlock().getBlockData());
                }
                playerSources.remove(playerSource);
            };
            scheduler.scheduleSyncDelayedTask(hardcoreClaimManager,task,20L * 15);
        }
    }

    public ArrayList<Location> addLocations(Location point1, Location point2) {
        ArrayList<Location> locations = new ArrayList<>();
        Location pointM = point2;
        Location pointM2 = point1;
        double distance = distance(point1,pointM);
        while (distance > 5) {
            pointM = new Location(point1.getWorld(),(point1.getBlockX() + pointM.getBlockX()) / 2,
                    point1.getBlockY(),(point1.getBlockZ() + pointM.getBlockZ()) / 2);
            pointM2 = new Location(point2.getWorld(),(point2.getBlockX() + pointM2.getBlockX()) / 2,
                    point1.getBlockY(),(point2.getBlockZ() + pointM2.getBlockZ()) / 2);
            if (distance(point1,pointM) >= 5 && distance(point2,pointM2) >= 5) {
                locations.add(pointM);
                locations.add(pointM2);
            }
            distance = distance(point1,pointM);
        }
        return locations;
    }

    public ArrayList<Location> addLocations2(Location point1, Location point2) {
        ArrayList<Location> locations = new ArrayList<>();
        double distance = distance(point1,point2);
        if (distance > 8) {
            Location pointM = new Location(point1.getWorld(),(point1.getBlockX() + point2.getBlockX()) / 2,
                    point1.getBlockY(), (point1.getBlockZ() + point2.getBlockZ()) / 2);
            locations.add(pointM);
            int n = (int) (distance / 6);
            for (int i = 0; i < n/2 + 1; i++) {
                Location newPoint1 = new Location(pointM.getWorld(), 6 * i / distance * (point2.getBlockX() -
                        point1.getBlockX()) + pointM.getBlockX(), pointM.getBlockY(), 6 * i / distance *
                        (point2.getBlockZ() - point1.getBlockZ()) + pointM.getBlockZ());
                Location newPoint2 = new Location(pointM.getWorld(), 2 * pointM.getBlockX() - newPoint1.getBlockX(),
                        pointM.getBlockY(), 2 * pointM.getBlockZ() - newPoint1.getBlockZ());
                locations.add(newPoint1);
                locations.add(newPoint2);
            }
        }
        return locations;
    }

    public double distance(Location point1, Location point2) {
        return Math.sqrt(Math.pow(point2.getBlockX() - point1.getBlockX(),2) + Math.pow(point2.getBlockZ() -
                point1.getBlockZ(),2));
    }
}
