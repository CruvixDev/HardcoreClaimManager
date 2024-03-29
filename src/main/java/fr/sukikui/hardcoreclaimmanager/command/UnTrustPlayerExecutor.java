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

import java.util.ArrayList;

public class UnTrustPlayerExecutor implements CommandExecutor {
    private HardcoreClaimManager hardcoreClaimManager;

    public UnTrustPlayerExecutor(HardcoreClaimManager hardcoreClaimManager) {
        this.hardcoreClaimManager = hardcoreClaimManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length > 0) {
            if (commandSender instanceof Player) {
                Player player = (Player) commandSender;
                PlayerData playerData = PlayerDataManager.getInstance().getPlayerDataByName(player.getName());
                if (playerData != null) {
                    Claim claim = PlayerDataManager.getInstance().getClaimAt(player.getLocation());
                    if (claim != null) {
                        if (playerData.isOwned(claim) || Bukkit.getPlayer(player.getUniqueId()).isOp()) {
                            ArrayList<String> untrustedPlayers = new ArrayList<>();
                            for (String playerToUnTrust : strings) {
                                boolean isUntrusted = claim.removeTrustedPlayers(playerToUnTrust,player.getUniqueId());
                                if (isUntrusted) {
                                    untrustedPlayers.add(playerToUnTrust);
                                }
                            }
                            if (untrustedPlayers.size() > 0) {
                                BukkitScheduler scheduler = Bukkit.getScheduler();
                                scheduler.runTaskAsynchronously(hardcoreClaimManager,() -> {
                                    for (String playerToUnTrust : untrustedPlayers) {
                                        PlayerData playerToUnTrustData = PlayerDataManager.getInstance().
                                                getPlayerDataByName(playerToUnTrust);
                                        DatabaseManager.getInstance(hardcoreClaimManager).deleteTrustedPlayers(
                                                playerToUnTrustData,claim);
                                    }
                                });
                            }
                            commandSender.sendMessage(ChatColor.GREEN + Messages.getMessages("players_successfully_removed"));
                            return true;
                        }
                        else {
                            commandSender.sendMessage(ChatColor.RED + Messages.getMessages("cannot_untrust_in_other_claims"));
                            return false;
                        }
                    }
                    else {
                        commandSender.sendMessage(ChatColor.RED + Messages.getMessages("not_in_registered_claim"));
                        return false;
                    }
                }
                else {
                    commandSender.sendMessage(ChatColor.RED + Messages.getMessages("player_not_exist"));
                    return false;
                }
            }
            else {
                commandSender.sendMessage(ChatColor.RED + Messages.getMessages("not_player"));
                return false;
            }
        }
        else {
            commandSender.sendMessage(ChatColor.RED + Messages.getMessages("no_arguments"));
            return false;
        }
    }
}
