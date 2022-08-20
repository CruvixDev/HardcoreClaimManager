package fr.sukikui.hardcoreclaimmanager.command;

import fr.sukikui.hardcoreclaimmanager.player.PlayerData;
import fr.sukikui.hardcoreclaimmanager.player.PlayerDataManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ShowClaimBlocksExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        String commandSenderName = commandSender.getName();
        PlayerData playerData = PlayerDataManager.getInstance().getPlayerDataByName(commandSenderName);
        if (playerData != null) {
            commandSender.sendMessage(ChatColor.AQUA + "The player " + playerData.getPlayerName() + " has " +
                    playerData.getClaimBlocks() + " block of claim.");
            return true;
        }
        else {
            commandSender.sendMessage(ChatColor.RED + "Player not found!");
            return false;
        }
    }
}
