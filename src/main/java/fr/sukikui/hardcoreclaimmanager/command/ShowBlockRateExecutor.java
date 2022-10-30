package fr.sukikui.hardcoreclaimmanager.command;

import fr.sukikui.hardcoreclaimmanager.HardcoreClaimManager;
import fr.sukikui.hardcoreclaimmanager.Messages;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ShowBlockRateExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length == 0) {
            try {
                int blockRate = Integer.parseInt(HardcoreClaimManager.getInstance().getProperties().getProperty(
                        "block-rate-per-hour"));
                commandSender.sendMessage(ChatColor.AQUA + String.format(Messages.getMessages(
                        "block_rate"),blockRate));
                return true;
            }
            catch (NumberFormatException e) {
                commandSender.sendMessage(ChatColor.RED + Messages.getMessages("invalid_properties"));
                return false;
            }
        }
        else {
            commandSender.sendMessage(ChatColor.RED + Messages.getMessages("too_many_arguments"));
            return false;
        }
    }
}
