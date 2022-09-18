package fr.sukikui.hardcoreclaimmanager.listener;

import fr.sukikui.hardcoreclaimmanager.HardcoreClaimManager;
import fr.sukikui.hardcoreclaimmanager.claim.Claim;
import fr.sukikui.hardcoreclaimmanager.claim.ClaimBoundariesVisualisation;
import fr.sukikui.hardcoreclaimmanager.claim.ClaimResults;
import fr.sukikui.hardcoreclaimmanager.data.DatabaseManager;
import fr.sukikui.hardcoreclaimmanager.enums.ClaimCreationMessages;
import fr.sukikui.hardcoreclaimmanager.enums.ClaimCreationSource;
import fr.sukikui.hardcoreclaimmanager.player.PlayerData;
import fr.sukikui.hardcoreclaimmanager.player.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * Class handling all events related to players
 */
public class PlayerEventHandler implements Listener {
    HardcoreClaimManager hardcoreClaimManager;
    private int claimID;

    public PlayerEventHandler(HardcoreClaimManager hardcoreClaimManager) {
        this.hardcoreClaimManager = hardcoreClaimManager;
        claimID = DatabaseManager.getInstance(hardcoreClaimManager).getLastID();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        PlayerData playerData = PlayerDataManager.getInstance().getPlayerDataByName(player.getName());
        if (playerData != null) {
            playerData.setJoinDate(System.currentTimeMillis());
        }
        else {
            PlayerData newPlayerData = PlayerDataManager.getInstance().addNewPlayerData(player.getName(),
                    player.getUniqueId(), System.currentTimeMillis());
            if (newPlayerData != null) {
                BukkitScheduler scheduler = Bukkit.getScheduler();
                scheduler.runTaskAsynchronously(hardcoreClaimManager,() -> {
                    DatabaseManager.getInstance(hardcoreClaimManager).insertPlayer(newPlayerData);
                });
            }
        }
        if (Bukkit.getOnlinePlayers().size() == 1) {
            hardcoreClaimManager.runBlockGainTask();
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        int blockRate;
        try {
            blockRate = Integer.parseInt(hardcoreClaimManager.getProperties().getProperty("block-rate-per-hour"));
        }
        catch (NumberFormatException numberFormatException) {
            return;
        }
        PlayerData playerData = PlayerDataManager.getInstance().getPlayerDataByName(e.getPlayer().getName());
        if (playerData != null) {
            long currentTime = System.currentTimeMillis();
            float blockEarn = (float) ((currentTime - playerData.getLastSaveBlocksGain()) * Math.pow(10,-3) / 60) *
                    blockRate / 60;
            playerData.addClaimBlocks(blockEarn);
            System.out.println("Current time : " + currentTime);
            System.out.println("last save date : " + playerData.getLastSaveBlocksGain());
            System.out.println("block rate : " + blockRate);
            System.out.println("block earned : " + blockEarn);
        }
        if (Bukkit.getOnlinePlayers().size() == 0) {
            Bukkit.getScheduler().cancelTasks(hardcoreClaimManager);
        }
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent e) {
        String defaultTool = this.hardcoreClaimManager.getProperties().getProperty("default-tool-selector");
        if (e.getItem() == null || !e.getItem().getType().equals(Material.matchMaterial(defaultTool))) {
            return;
        }
        //handle claim creation with the tool's selector
        PlayerData playerData = PlayerDataManager.getInstance().getPlayerDataByName(e.getPlayer().getName());
        if (playerData != null && claimID >= 0) {
            if (e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                if (playerData.getLastToolLocation() == null) {
                    playerData.setLastToolLocation(e.getClickedBlock().getLocation());
                    e.getPlayer().sendMessage(ChatColor.YELLOW + "First corner defined, right click on the other corner!");
                    e.setCancelled(true);
                }
                else {
                    playerData.setLastToolLocation(null);
                    e.getPlayer().sendMessage(ChatColor.YELLOW + "Claim creation cancel!");
                    e.setCancelled(true);
                }
            }
            if (playerData.getLastToolLocation() != null && e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                Location corner1 = playerData.getLastToolLocation();
                Location corner2 = e.getClickedBlock().getLocation();
                ClaimResults results;
                if (Bukkit.getServer().getOperators().contains(e.getPlayer())) {
                    results = PlayerDataManager.getInstance().createClaim(corner1,corner2,e.getPlayer().getUniqueId(),
                            true,claimID + 1L, ClaimCreationSource.PLAYER);
                    if (results.claim != null) {
                        ClaimBoundariesVisualisation.getInstance().startVisualisationTask(e.getPlayer().getName(),
                                results.claim.getCorner1(),results.claim.getCorner2());
                        BukkitScheduler scheduler = Bukkit.getScheduler();
                        scheduler.runTaskAsynchronously(hardcoreClaimManager,() -> {
                            DatabaseManager.getInstance(hardcoreClaimManager).insertClaim(results.claim,playerData);
                            claimID = DatabaseManager.getInstance(hardcoreClaimManager).getLastID();
                        });
                    }
                }
                else {
                    results = PlayerDataManager.getInstance().createClaim(corner1,corner2,e.getPlayer().getUniqueId(),
                            false,claimID + 1L,ClaimCreationSource.PLAYER);
                    ClaimBoundariesVisualisation.getInstance().startVisualisationTask(e.getPlayer().getName(),corner1,
                            corner2);
                    if (results.claim != null) {
                        BukkitScheduler scheduler = Bukkit.getScheduler();
                        scheduler.runTaskAsynchronously(hardcoreClaimManager,() -> {
                            DatabaseManager.getInstance(hardcoreClaimManager).insertClaim(results.claim,playerData);
                            claimID = DatabaseManager.getInstance(hardcoreClaimManager).getLastID();
                        });
                    }
                }
                String message = results.message.getMessage();
                if (results.message.equals(ClaimCreationMessages.NotEnoughBlock)) {
                    message = String.format(results.message.getMessage(),playerData.getClaimBlocks(),
                            results.claimSurface);
                }
                e.getPlayer().sendMessage(message);
                playerData.setLastToolLocation(null);
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerHeldTool(PlayerItemHeldEvent e) {
        if (e.getPlayer().getInventory().getItem(e.getNewSlot()) == null) {
            return;
        }
        Material defaultTool = Material.matchMaterial(hardcoreClaimManager.getProperties().
                getProperty("default-tool-selector"));
        if (e.getPlayer().getInventory().getItem(e.getNewSlot()).getType().equals(defaultTool)) {
            Claim claim = PlayerDataManager.getInstance().getClaimAt(e.getPlayer().getLocation());
            if (claim != null) {
                ClaimBoundariesVisualisation.getInstance().startVisualisationTask(e.getPlayer().getName(),
                        claim.getCorner1(),claim.getCorner2());
            }
        }
    }
}
