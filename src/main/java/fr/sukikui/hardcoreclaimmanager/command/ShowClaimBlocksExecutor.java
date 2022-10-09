package fr.sukikui.hardcoreclaimmanager.command;

import fr.sukikui.hardcoreclaimmanager.Messages;
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
        if (strings.length == 0) {
            String commandSenderName = commandSender.getName();
            PlayerData playerData = PlayerDataManager.getInstance().getPlayerDataByName(commandSenderName);
            if (playerData != null) {
                commandSender.sendMessage(ChatColor.AQUA + String.format(Messages.getMessages(
                        "show_claim_blocks"),playerData.getPlayerName(),playerData.getClaimBlocks()));
                return true;
            }
            else {
                commandSender.sendMessage(ChatColor.RED + Messages.getMessages("player_not_exist"));
                return false;
            }
        }
        else if (strings.length == 1 && commandSender.isOp()) {
            PlayerData playerData = PlayerDataManager.getInstance().getPlayerDataByName(strings[0]);
            if (playerData != null) {
                commandSender.sendMessage(ChatColor.AQUA + String.format(Messages.getMessages(
                        "show_claim_blocks"),playerData.getPlayerName(),playerData.getClaimBlocks()));
                return true;
            }
            else {
                commandSender.sendMessage(ChatColor.RED + Messages.getMessages("player_not_exist"));
                return false;
            }
        }
        else {
            commandSender.sendMessage(ChatColor.RED + Messages.getMessages("too_many_arguments"));
            return false;
        }
    }
}
