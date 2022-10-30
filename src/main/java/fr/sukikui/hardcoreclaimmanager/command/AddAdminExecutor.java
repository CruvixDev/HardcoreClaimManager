package fr.sukikui.hardcoreclaimmanager.command;

import fr.sukikui.hardcoreclaimmanager.HardcoreClaimManager;
import org.bukkit.entity.Player;
import fr.sukikui.hardcoreclaimmanager.Messages;
import fr.sukikui.hardcoreclaimmanager.player.PlayerData;
import fr.sukikui.hardcoreclaimmanager.player.PlayerDataManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class AddAdminExecutor implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender.isOp()) {
            if (strings.length == 1) {
                PlayerData playerData = PlayerDataManager.getInstance().getPlayerDataByName(strings[0]);
                if (playerData != null) {
                    boolean isAdded = HardcoreClaimManager.getInstance().addAdmins(playerData);
                    if (isAdded) {
                        commandSender.sendMessage(ChatColor.GREEN + String.format(Messages.getMessages(
                                "become_admin_plugin"),strings[0]));
                        return true;
                    }
                    else {
                        commandSender.sendMessage(ChatColor.RED + String.format(Messages.getMessages(
                                "already_admin"),strings[0]));
                        return false;
                    }
                }
                else {
                    commandSender.sendMessage(ChatColor.RED + Messages.getMessages("player_not_exist"));
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
