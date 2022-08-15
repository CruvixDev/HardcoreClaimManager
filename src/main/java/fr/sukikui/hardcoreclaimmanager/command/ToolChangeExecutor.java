package fr.sukikui.hardcoreclaimmanager.command;

import fr.sukikui.hardcoreclaimmanager.HardcoreClaimManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ToolChangeExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender.isOp()) {
            if (strings.length == 1) {
                Material material = Material.matchMaterial(strings[0]);
                if (material != null) {
                    HardcoreClaimManager.getProperties().setProperty("default-tool-selector",material.toString());
                    commandSender.sendMessage(ChatColor.GREEN + "Selector tool become " + material + " for all players.");
                    return true;
                }
                else {
                    commandSender.sendMessage(ChatColor.RED + "Material not valid!");
                    return false;
                }
            }
            else {
                commandSender.sendMessage(ChatColor.RED + "Invalid number of arguments!");
                return false;
            }
        }
        else {
            commandSender.sendMessage(ChatColor.RED + "You are not allowed to perform this command!");
            return false;
        }
    }
}
