package fr.sukikui.hardcoreclaimmanager.command;

import fr.sukikui.hardcoreclaimmanager.claim.Claim;
import fr.sukikui.hardcoreclaimmanager.player.PlayerData;
import fr.sukikui.hardcoreclaimmanager.player.PlayerDataManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ShowTrustedPlayersExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            PlayerData playerData = PlayerDataManager.getInstance().getPlayerDataByName(player.getName());
            Claim claim = PlayerDataManager.getInstance().getClaimAt(player.getLocation());
            if (claim != null) {
                if (playerData != null && playerData.isOwned(claim)) {
                    commandSender.sendMessage(ChatColor.GREEN + claim.getTrustedPlayers().toString());
                    return true;
                }
                else {
                    commandSender.sendMessage(ChatColor.RED + "It is not your claim!");
                    return false;
                }
            }
            else {
                commandSender.sendMessage(ChatColor.RED + "You are not in a registered claim!");
                return false;
            }
        }
        else {
            commandSender.sendMessage(ChatColor.RED + "Only players can execute this command in their claims!");
            return false;
        }
    }
}
