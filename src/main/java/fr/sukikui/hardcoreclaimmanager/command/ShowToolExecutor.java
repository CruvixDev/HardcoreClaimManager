package fr.sukikui.hardcoreclaimmanager.command;

import fr.sukikui.hardcoreclaimmanager.HardcoreClaimManager;
import fr.sukikui.hardcoreclaimmanager.Messages;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ShowToolExecutor implements CommandExecutor {
    private HardcoreClaimManager hardcoreClaimManager;

    public ShowToolExecutor(HardcoreClaimManager hardcoreClaimManager) {
        this.hardcoreClaimManager = hardcoreClaimManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        String toolUses = this.hardcoreClaimManager.getProperties().getProperty("default-tool-selector");
        commandSender.sendMessage(ChatColor.GREEN + String.format(Messages.getMessages("show_tool"),toolUses));
        return true;
    }
}
