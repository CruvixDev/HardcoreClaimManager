package fr.sukikui.hardcoreclaimmanager.command;

import fr.sukikui.hardcoreclaimmanager.HardcoreClaimManager;
import fr.sukikui.hardcoreclaimmanager.Messages;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ToolChangeExecutor implements CommandExecutor {
    private HardcoreClaimManager hardcoreClaimManager;

    public ToolChangeExecutor(HardcoreClaimManager hardcoreClaimManager) {
        this.hardcoreClaimManager = hardcoreClaimManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender.isOp()) {
            if (strings.length == 1) {
                Material material = Material.matchMaterial(strings[0]);
                if (material != null) {
                    this.hardcoreClaimManager.getProperties().setProperty("default-tool-selector",material.toString());
                    this.hardcoreClaimManager.storeProperties();
                    commandSender.sendMessage(ChatColor.GREEN + String.format(Messages.getMessages(
                            "tool_change"),material));
                    HardcoreClaimManager.getInstance().storeProperties();
                    return true;
                }
                else {
                    commandSender.sendMessage(ChatColor.RED + Messages.getMessages("material_not_valid"));
                    return false;
                }
            }
            else {
                commandSender.sendMessage(ChatColor.RED + Messages.getMessages(
                        "parameter_not_valid"),strings[0]);
                return false;
            }
        }
        else {
            commandSender.sendMessage(ChatColor.RED + Messages.getMessages("not_allow"));
            return false;
        }
    }
}
