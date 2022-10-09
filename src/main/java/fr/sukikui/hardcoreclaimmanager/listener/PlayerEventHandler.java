package fr.sukikui.hardcoreclaimmanager.listener;

import fr.sukikui.hardcoreclaimmanager.HardcoreClaimManager;
import fr.sukikui.hardcoreclaimmanager.Messages;
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
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * Class handling all events related to players
 */
public class PlayerEventHandler implements Listener {
    HardcoreClaimManager hardcoreClaimManager;
    private int claimID;

    public PlayerEventHandler(HardcoreClaimManager hardcoreClaimManager) {
        this.hardcoreClaimManager = hardcoreClaimManager;
        this.claimID = DatabaseManager.getInstance(hardcoreClaimManager).getLastID();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        PlayerData playerData = PlayerDataManager.getInstance().getPlayerDataByName(player.getName());
        if (playerData != null) {
            playerData.setLastJoinDate(System.currentTimeMillis());
        }
        else {
            PlayerData newPlayerData = PlayerDataManager.getInstance().addNewPlayerData(player.getName(),
                    player.getUniqueId(), System.currentTimeMillis());
            if (newPlayerData != null) {
                BukkitScheduler scheduler = Bukkit.getScheduler();
                scheduler.runTaskAsynchronously(this.hardcoreClaimManager,() -> {
                    DatabaseManager.getInstance(this.hardcoreClaimManager).insertPlayer(newPlayerData);
                });
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        int blockRate;
        int clockPeriod;
        try {
            blockRate = Integer.parseInt(this.hardcoreClaimManager.getProperties().getProperty("block-rate-per-hour"));
            clockPeriod = Integer.parseInt(this.hardcoreClaimManager.getProperties().getProperty(
                    "clock-block-gain-duration"));
            if (clockPeriod < 1) {
                clockPeriod = 1;
            }
        }
        catch (NumberFormatException numberFormatException) {
            blockRate = 100;
            clockPeriod = 1;
        }
        PlayerData playerData = PlayerDataManager.getInstance().getPlayerDataByName(e.getPlayer().getName());
        if (playerData != null) {
            long currentTime = System.currentTimeMillis();
            float blockEarn = (float) ((currentTime - playerData.getLastSaveBlocksGain()) * Math.pow(10,-3) / 60) *
                    (blockRate * clockPeriod) / 60;
            playerData.addClaimBlocks(blockEarn);
            playerData.setLastJoinDate(currentTime);
        }
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent e) {
        //handle stick interaction in worlds
        if (e.getItem() != null && e.getItem().getType().equals(Material.STICK) &&
                e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            Claim claim = PlayerDataManager.getInstance().getClaimAt(e.getClickedBlock().getLocation());
            if (claim != null) {
                PlayerData playerOwnerData = PlayerDataManager.getInstance().getPlayerDataByUUID(claim.getOwnerUUID());
                e.getPlayer().sendMessage(ChatColor.AQUA + String.format(Messages.getMessages(
                        "claim_by"),playerOwnerData.getPlayerName()));
            }
            return;
        }

        String defaultTool = this.hardcoreClaimManager.getProperties().getProperty("default-tool-selector");
        if (e.getItem() == null || !e.getItem().getType().equals(Material.matchMaterial(defaultTool))) {
            return;
        }

        //handle claim creation with the tool's selector
        PlayerData playerData = PlayerDataManager.getInstance().getPlayerDataByName(e.getPlayer().getName());
        if (playerData != null && this.claimID >= 0) {
            if (e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                if (playerData.getLastToolLocation() == null) {
                    playerData.setLastToolLocation(e.getClickedBlock().getLocation());
                    e.getPlayer().sendMessage(ChatColor.YELLOW + Messages.getMessages("first_corner_defined"));
                    e.setCancelled(true);
                }
                else {
                    playerData.setLastToolLocation(null);
                    e.getPlayer().sendMessage(ChatColor.YELLOW + Messages.getMessages("claim_cancel"));
                    e.setCancelled(true);
                }
            }
            if (playerData.getLastToolLocation() != null && e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                Location corner1 = playerData.getLastToolLocation();
                Location corner2 = e.getClickedBlock().getLocation();
                ClaimResults results;
                boolean isAdmin = false;

                if (Bukkit.getServer().getOperators().contains(e.getPlayer())) {
                    isAdmin = true;
                }
                results = PlayerDataManager.getInstance().createClaim(corner1,corner2,e.getPlayer().getUniqueId(),
                        isAdmin,this.claimID + 1L, ClaimCreationSource.PLAYER);
                if (results.claim != null) {
                    ClaimBoundariesVisualisation.getInstance().startVisualisationTask(e.getPlayer().getName(),
                            results.claim.getCorner1(),results.claim.getCorner2());
                    BukkitScheduler scheduler = Bukkit.getScheduler();
                    scheduler.runTaskAsynchronously(hardcoreClaimManager,() -> {
                        DatabaseManager.getInstance(hardcoreClaimManager).insertClaim(results.claim,playerData);
                        claimID = DatabaseManager.getInstance(hardcoreClaimManager).getLastID();
                    });
                }
                String message = results.message.getMessage();
                if (results.message.equals(ClaimCreationMessages.NotEnoughBlock)) {
                    message = String.format(results.message.getMessage(),playerData.getClaimBlocks(),
                            results.claimSurface);
                }
                if (results.message.equals(ClaimCreationMessages.ClaimTooShrink)) {
                    int claimMinWidth;
                    try {
                        claimMinWidth = Integer.parseInt(this.hardcoreClaimManager.getProperties().getProperty(
                                "min-claim-width"));
                    }
                    catch (NumberFormatException ex) {
                        claimMinWidth = 5;
                    }
                    message = String.format(results.message.getMessage(),claimMinWidth,results.claim.getWidth(),
                            results.claim.getHeight());
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
        Material defaultTool = Material.matchMaterial(this.hardcoreClaimManager.getProperties().
                getProperty("default-tool-selector"));
        if (e.getPlayer().getInventory().getItem(e.getNewSlot()).getType().equals(defaultTool)) {
            Claim claim = PlayerDataManager.getInstance().getClaimAt(e.getPlayer().getLocation());
            if (claim != null) {
                ClaimBoundariesVisualisation.getInstance().startVisualisationTask(e.getPlayer().getName(),
                        claim.getCorner1(),claim.getCorner2());
            }
        }
    }

    @EventHandler
    public void onPlayerEmptyBucketInClaim(PlayerBucketEmptyEvent e) {
        Claim claim = PlayerDataManager.getInstance().getClaimAt(e.getBlock().getLocation());
        if (claim != null) {
            PlayerData playerData = PlayerDataManager.getInstance().getPlayerDataByName(e.getPlayer().getName());
            if (playerData != null && !playerData.isOwned(claim) && !claim.isAllowed(playerData.getPlayerName())) {
                e.setCancelled(true);
                PlayerData claimOwnerData = PlayerDataManager.getInstance().getPlayerDataByUUID(claim.getOwnerUUID());
                e.getPlayer().sendMessage(ChatColor.RED + String.format(Messages.getMessages(
                        "bucket_place"),claimOwnerData.getPlayerName()));
            }
        }
    }
}
