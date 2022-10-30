package fr.sukikui.hardcoreclaimmanager.command;

import fr.sukikui.hardcoreclaimmanager.HardcoreClaimManager;
import fr.sukikui.hardcoreclaimmanager.Messages;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class SetBlockRateExecutor implements CommandExecutor {
    private HardcoreClaimManager hardcoreClaimManager;

    public SetBlockRateExecutor(HardcoreClaimManager hardcoreClaimManager) {
        this.hardcoreClaimManager = hardcoreClaimManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender.isOp()) {
            if (strings.length == 1) {
                try {
                    int blockRate = Integer.parseInt(strings[0]);
                    this.hardcoreClaimManager.getProperties().setProperty("block-rate-per-hour",strings[0]);
                    this.hardcoreClaimManager.storeProperties();
                    commandSender.sendMessage(ChatColor.GREEN + String.format(Messages.getMessages(
                            "block_rate_set"),blockRate));
                    HardcoreClaimManager.getInstance().storeProperties();
                    return true;
                }
                catch (NumberFormatException e) {
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
