package fr.sukikui.hardcoreclaimmanager.command;

import fr.sukikui.hardcoreclaimmanager.Messages;
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
                        commandSender.sendMessage(ChatColor.GREEN + String.format(Messages.getMessages(
                                "blocks_added"),strings[1],strings[0]));
                        return true;
                    }
                    catch (NumberFormatException e) {
                        commandSender.sendMessage(ChatColor.RED + String.format(Messages.getMessages(
                                "parameter_not_valid"),strings[1]));
                        return false;
                    }
                }
                else {
                    commandSender.sendMessage(ChatColor.RED + Messages.getMessages("player_not_exist"));
                    return false;
                }
            }
            else {
                commandSender.sendMessage(ChatColor.RED + Messages.getMessages("not_enough_arguments"));
                return false;
            }
        }
        else {
             commandSender.sendMessage(ChatColor.RED + Messages.getMessages("not_allow"));
             return false;
        }
    }
}
