package fr.sukikui.hardcoreclaimmanager.command;

import fr.sukikui.hardcoreclaimmanager.player.PlayerData;
import fr.sukikui.hardcoreclaimmanager.player.PlayerDataManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class AddClaimBlocksExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender.isOp()) {
            if (strings.length == 2) {
                PlayerData playerData = PlayerDataManager.getInstance().getPlayerDataByName(strings[0]);
                if (playerData != null) {
                    try {
                        playerData.addClaimBlocks(Integer.parseInt(strings[1]));
                        commandSender.sendMessage(ChatColor.GREEN + "Successfully added [" + strings[1] + "] blocks to " + strings[0] + "!");
                        return true;
                    }
                    catch (NumberFormatException e) {
                        commandSender.sendMessage(ChatColor.RED + "The parameter " + strings[1] + " is not valid!");
                        return false;
                    }
                }
                else {
                    commandSender.sendMessage(ChatColor.RED + "The player " + strings[0] + " does not exists!");
                    return false;
                }
            }
            else {
                commandSender.sendMessage(ChatColor.RED + "Not enough arguments!");
                return false;
            }
        }
        else {
             commandSender.sendMessage(ChatColor.RED + "You are not allowed to perform this command!");
             return false;
        }
    }
}
