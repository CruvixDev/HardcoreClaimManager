package fr.sukikui.hardcoreclaimmanager.listener;

import fr.sukikui.hardcoreclaimmanager.player.PlayerData;
import fr.sukikui.hardcoreclaimmanager.player.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerEventHandler implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        if (!player.hasPlayedBefore()) {
            PlayerDataManager.getInstance().addNewPlayerData(player.getName(),player.getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        //TODO here save blocks claim according to playing time (if the schedule has not attribute him before quit)
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent e) {
        //handle claim creation with the tool's selector
        PlayerData playerData = PlayerDataManager.getInstance().getPlayerDataByName(e.getPlayer().getName());
        if (playerData != null) {
            if (playerData.getLastToolLocation() == null && e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                playerData.setLastToolLocation(e.getClickedBlock().getLocation());
            }
            if (playerData.getLastToolLocation() != null && e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                Location corner1 = playerData.getLastToolLocation();
                Location corner2 = e.getClickedBlock().getLocation();
                String reason;
                if (Bukkit.getServer().getOperators().contains(e.getPlayer())) {
                    reason = PlayerDataManager.getInstance().createClaim(corner1,corner2,e.getPlayer().getUniqueId(),true);
                }
                else {
                    reason = PlayerDataManager.getInstance().createClaim(corner1,corner2,e.getPlayer().getUniqueId(),false);

                }
                e.getPlayer().sendMessage(reason);
            }
        }
    }

    @EventHandler
    public void onPlayerHeldTool(PlayerItemHeldEvent e) {
        //TODO boundaries visualisation (need a schedule to cancel false block)
    }
}
