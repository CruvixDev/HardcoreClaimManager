package fr.sukikui.hardcoreclaimmanager.command;

import fr.sukikui.hardcoreclaimmanager.Messages;
import fr.sukikui.hardcoreclaimmanager.claim.Claim;
import fr.sukikui.hardcoreclaimmanager.player.PlayerData;
import fr.sukikui.hardcoreclaimmanager.player.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ShowTrustedPlayersExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length == 0) {
            if (commandSender instanceof Player) {
                Player player = (Player) commandSender;
                PlayerData playerData = PlayerDataManager.getInstance().getPlayerDataByName(player.getName());
                Claim claim = PlayerDataManager.getInstance().getClaimAt(player.getLocation());
                if (claim != null) {
                    if (playerData != null && playerData.isOwned(claim) || Bukkit.getPlayer(player.getUniqueId()).
                            isOp()) {
                        commandSender.sendMessage(ChatColor.GREEN + claim.getTrustedPlayers().toString());
                        return true;
                    }
                    else {
                        commandSender.sendMessage(ChatColor.RED + Messages.getMessages("not_allow_in_other_claims"));
                        return false;
                    }
                }
                else {
                    commandSender.sendMessage(ChatColor.RED + Messages.getMessages("not_in_registered_claim"));
                    return false;
                }
            }
            else {
                commandSender.sendMessage(ChatColor.RED + Messages.getMessages("not_player"));
                return false;
            }
        }
        else {
            commandSender.sendMessage(ChatColor.RED + Messages.getMessages("too_many_arguments"));
            return false;
        }
    }
}
