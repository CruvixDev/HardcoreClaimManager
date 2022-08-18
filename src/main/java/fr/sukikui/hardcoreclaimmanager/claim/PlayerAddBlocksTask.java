package fr.sukikui.hardcoreclaimmanager.claim;

import fr.sukikui.hardcoreclaimmanager.HardcoreClaimManager;
import fr.sukikui.hardcoreclaimmanager.player.PlayerData;
import fr.sukikui.hardcoreclaimmanager.player.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class PlayerAddBlocksTask implements Runnable{
    HardcoreClaimManager hardcoreClaimManager;

    public PlayerAddBlocksTask(HardcoreClaimManager hardcoreClaimManager) {
        this.hardcoreClaimManager = hardcoreClaimManager;
    }

    @Override
    public void run() {
        ArrayList<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        int blockRate = 0;
        try {
            blockRate = Integer.parseInt(hardcoreClaimManager.getProperties().getProperty("block-rate-per-hour"));
        }
        catch (NumberFormatException e) {}
        for (Player player : players) {
            PlayerData playerData = PlayerDataManager.getInstance().getPlayerDataByName(player.getName());
            if (playerData != null) {
                long currentTime = System.currentTimeMillis();
                int blockEarn = (int) ((currentTime - playerData.getJoinDate()) * Math.pow(10,-3) / 60) * blockRate / 60;
                playerData.addClaimBlocks(blockEarn);
                playerData.setJoinDate(currentTime);
            }
        }
    }
}
