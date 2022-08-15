package fr.sukikui.hardcoreclaimmanager.command;

import fr.sukikui.hardcoreclaimmanager.claim.Claim;
import fr.sukikui.hardcoreclaimmanager.player.PlayerDataManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class UnregisterClaimExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            Claim claim = PlayerDataManager.getInstance().getClaimAt(player.getLocation());
            if (claim != null) {
                PlayerDataManager.getInstance().removeClaim(claim,player.getName());
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
