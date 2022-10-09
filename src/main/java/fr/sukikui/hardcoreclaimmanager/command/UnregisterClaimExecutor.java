package fr.sukikui.hardcoreclaimmanager.command;

import fr.sukikui.hardcoreclaimmanager.HardcoreClaimManager;
import fr.sukikui.hardcoreclaimmanager.Messages;
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
                        commandSender.sendMessage(ChatColor.GREEN + Messages.getMessages("claim_removed"));
                        if (isRemoved) {
                            BukkitScheduler scheduler = Bukkit.getScheduler();
                            scheduler.runTaskAsynchronously(hardcoreClaimManager,() -> {
                                DatabaseManager.getInstance(hardcoreClaimManager).deleteClaim(claim);
                            });
                        }
                    }
                    else {
                        commandSender.sendMessage(ChatColor.RED + Messages.getMessages("cannot_remove_claim"));
                    }
                }
                else {
                    commandSender.sendMessage(ChatColor.RED + Messages.getMessages("not_registered_in_plugin"));
                    return false;
                }
            }
            else {
                commandSender.sendMessage(ChatColor.RED + Messages.getMessages("not_registered_in_plugin"));
            }
        }
        else {
            commandSender.sendMessage(ChatColor.RED + Messages.getMessages("not_player"));
            return false;
        }
        return false;
    }
}
