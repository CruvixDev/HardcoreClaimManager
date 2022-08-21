package fr.sukikui.hardcoreclaimmanager.command;

import fr.sukikui.hardcoreclaimmanager.claim.Claim;
import fr.sukikui.hardcoreclaimmanager.player.PlayerData;
import fr.sukikui.hardcoreclaimmanager.player.PlayerDataManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TrustPlayerExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length > 0) {
            if (commandSender instanceof Player) {
                Player player = (Player) commandSender;
                PlayerData playerData = PlayerDataManager.getInstance().getPlayerDataByName(player.getName());
                if (playerData != null) {
                    Claim claim = PlayerDataManager.getInstance().getClaimAt(player.getLocation());
                    if (claim != null) {
                        for (String playerToTrust : strings) {
                            claim.addTrustedPlayers(playerToTrust,player.getUniqueId());
                        }
                        commandSender.sendMessage(ChatColor.GREEN + "Players successfully trusted!");
                        return true;
                    }
                    else {
                        commandSender.sendMessage(ChatColor.RED + "You are not in a registered claim!");
                        return false;
                    }
                }
                else {
                    commandSender.sendMessage(ChatColor.RED + "This player does not exists!");
                    return false;
                }
            }
            else {
                commandSender.sendMessage(ChatColor.RED + "Only players can execute this command in their claims!");
                return false;
            }
        }
        else {
            commandSender.sendMessage(ChatColor.RED + "No arguments given!");
            return false;
        }
    }
}
