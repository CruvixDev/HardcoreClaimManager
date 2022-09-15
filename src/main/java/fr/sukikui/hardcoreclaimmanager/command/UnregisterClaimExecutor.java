package fr.sukikui.hardcoreclaimmanager.command;

import fr.sukikui.hardcoreclaimmanager.HardcoreClaimManager;
import fr.sukikui.hardcoreclaimmanager.claim.Claim;
import fr.sukikui.hardcoreclaimmanager.data.DatabaseManager;
import fr.sukikui.hardcoreclaimmanager.player.PlayerData;
import fr.sukikui.hardcoreclaimmanager.player.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

public class UnregisterClaimExecutor implements CommandExecutor {
    private HardcoreClaimManager hardcoreClaimManager;

    public UnregisterClaimExecutor(HardcoreClaimManager hardcoreClaimManager) {
        this.hardcoreClaimManager = hardcoreClaimManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            Claim claim = PlayerDataManager.getInstance().getClaimAt(player.getLocation());
            if (claim != null) {
                PlayerData playerData = PlayerDataManager.getInstance().getPlayerDataByName(player.getName());
                if (playerData != null) {
                    if (playerData.isOwned(claim) || commandSender.isOp()) {
                        boolean isRemoved = PlayerDataManager.getInstance().removeClaim(claim,player.getUniqueId());
                        commandSender.sendMessage(ChatColor.GREEN + "Claim successfully unregistered!");
                        if (isRemoved) {
                            BukkitScheduler scheduler = Bukkit.getScheduler();
                            scheduler.runTaskAsynchronously(hardcoreClaimManager,() -> {
                                DatabaseManager.getInstance(hardcoreClaimManager).deleteClaim(claim);
                            });
                        }
                    }
                    else {
                        commandSender.sendMessage(ChatColor.RED + "You cannot unregister a claim that is not your!");
                    }
                }
                else {
                    commandSender.sendMessage(ChatColor.RED + "This player does not exists!");
                    return false;
                }
            }
            else {
                commandSender.sendMessage(ChatColor.RED + "No claim was found at this location!");
            }
        }
        else {
            commandSender.sendMessage(ChatColor.RED + "Only players can execute this command in their claims!");
            return false;
        }
        return false;
    }
}
