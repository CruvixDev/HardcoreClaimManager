package fr.sukikui.hardcoreclaimmanager.command;

import fr.sukikui.hardcoreclaimmanager.HardcoreClaimManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class SetBlockRateExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender.isOp()) {
            if (strings.length == 1) {
                try {
                    Integer.parseInt(strings[0]);
                    HardcoreClaimManager.getProperties().setProperty("block-rate-per-hour",strings[0]);
                    return true;
                }
                catch (NumberFormatException e) {
                    commandSender.sendMessage(ChatColor.RED + "The number format is not valid!");
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
