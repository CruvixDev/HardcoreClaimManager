package fr.sukikui.hardcoreclaimmanager.command;

import fr.sukikui.hardcoreclaimmanager.player.PlayerData;
import fr.sukikui.hardcoreclaimmanager.player.PlayerDataManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ShowClaimsExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length == 0) {
            String commandSenderName = commandSender.getName();
            PlayerData playerData = PlayerDataManager.getInstance().getPlayerDataByName(commandSenderName);
            if (playerData != null) {
                commandSender.sendMessage(ChatColor.AQUA + playerData.toString());
                return true;
            }
            else {
                commandSender.sendMessage(ChatColor.RED + "Player not found!");
                return false;
            }
        }
        else {
            commandSender.sendMessage(ChatColor.RED + "Arguments given!");
            return false;
        }
    }
}
