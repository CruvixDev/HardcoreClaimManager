package fr.sukikui.hardcoreclaimmanager.claim;

import fr.sukikui.hardcoreclaimmanager.HardcoreClaimManager;
import fr.sukikui.hardcoreclaimmanager.player.PlayerData;
import fr.sukikui.hardcoreclaimmanager.player.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * A task which attributes claim blocks according to player played time
 */
public class PlayerAddBlocksTask implements Runnable{
    private HardcoreClaimManager hardcoreClaimManager;
    private int blockRate;

    public PlayerAddBlocksTask(HardcoreClaimManager hardcoreClaimManager) {
        this.hardcoreClaimManager = hardcoreClaimManager;
        try {
            this.blockRate = Integer.parseInt(hardcoreClaimManager.getProperties().getProperty("block-rate-per-hour"));
        }
        catch (NumberFormatException e) {
            this.blockRate = 100;
            return;
        }
    }

    @Override
    public void run() {
        ArrayList<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        for (Player player : players) {
            PlayerData playerData = PlayerDataManager.getInstance().getPlayerDataByName(player.getName());
            if (playerData != null) {
                long currentTime = System.currentTimeMillis();
                float blockEarn = (float) (((currentTime - playerData.getLastSaveBlocksGain()) * Math.pow(10,-3) / 60) *
                        blockRate / 60);
                playerData.addClaimBlocks(blockEarn);
                System.out.println(blockEarn);
                playerData.setLastSaveBlocksGain(currentTime);
            }
        }
    }
}
