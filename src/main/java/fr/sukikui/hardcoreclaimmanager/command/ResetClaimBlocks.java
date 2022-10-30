package fr.sukikui.hardcoreclaimmanager.command;

import fr.sukikui.hardcoreclaimmanager.HardcoreClaimManager;
import fr.sukikui.hardcoreclaimmanager.Messages;
import fr.sukikui.hardcoreclaimmanager.player.PlayerData;
import fr.sukikui.hardcoreclaimmanager.player.PlayerDataManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ResetClaimBlocks implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender.isOp()) {
            if (strings.length == 1) {
                PlayerData playerData = PlayerDataManager.getInstance().getPlayerDataByName(strings[0]);
                if (playerData != null) {
                    try {
                        int claimBlocks = Integer.parseInt(HardcoreClaimManager.getInstance().getProperties().getProperty(
                                "default-claim-blocks"));
                        playerData.setClaimBlocks(claimBlocks);
                        commandSender.sendMessage(ChatColor.GREEN + String.format(Messages.getMessages(
                                "claim_blocks_reset"),strings[0]));
                        return true;
                    }
                    catch (NumberFormatException e) {
                        commandSender.sendMessage(ChatColor.RED + Messages.getMessages(
                                "invalid_properties"));
                        return false;
                    }
                }
                else {
                    commandSender.sendMessage(ChatColor.RED + Messages.getMessages(
                            "not_registered_in_plugin"));
                    return false;
                }
            }
            else if (strings.length > 1) {
                commandSender.sendMessage(ChatColor.RED + Messages.getMessages("too_many_arguments"));
                return false;
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
