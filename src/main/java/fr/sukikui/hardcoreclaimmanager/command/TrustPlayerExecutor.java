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

public class TrustPlayerExecutor implements CommandExecutor {
    private HardcoreClaimManager hardcoreClaimManager;

    public TrustPlayerExecutor(HardcoreClaimManager hardcoreClaimManager) {
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
                        if (playerData.isOwned(claim) || player.isOp()) {
                            ArrayList<String> trustedPlayers = new ArrayList<>();
                            for (String playerToTrust : strings) {
                                boolean isTrusted = claim.addTrustedPlayers(playerToTrust,player.getUniqueId());
                                if (isTrusted) {
                                    trustedPlayers.add(playerToTrust);
                                }
                            }
                            if (trustedPlayers.size() > 0) {
                                BukkitScheduler scheduler = Bukkit.getScheduler();
                                scheduler.runTaskAsynchronously(hardcoreClaimManager,() -> {
                                    for (String playerName : trustedPlayers) {
                                        PlayerData playerToTrustData = PlayerDataManager.getInstance().
                                                getPlayerDataByName(playerName);
                                        DatabaseManager.getInstance(hardcoreClaimManager).insertTrustedPlayer(
                                                playerToTrustData,claim);
                                    }
                                });
                                commandSender.sendMessage(ChatColor.GREEN + Messages.getMessages("players_successfully_trusted"));
                            }
                            else {
                                commandSender.sendMessage(ChatColor.RED + Messages.getMessages("no_players_add"));
                            }
                            return true;
                        }
                        else {
                            commandSender.sendMessage(ChatColor.RED + Messages.getMessages("cannot_trust_in_other_claims"));
                            return false;
                        }
                    }
                    else {
                        commandSender.sendMessage(ChatColor.RED + Messages.getMessages("not_in_registered_claim"));
                        return false;
                    }
                }
                else {
                    commandSender.sendMessage(ChatColor.RED + Messages.getMessages("not_registered_in_plugin"));
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
