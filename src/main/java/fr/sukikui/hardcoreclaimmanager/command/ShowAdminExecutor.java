package fr.sukikui.hardcoreclaimmanager.command;

import fr.sukikui.hardcoreclaimmanager.HardcoreClaimManager;
import fr.sukikui.hardcoreclaimmanager.Messages;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ShowAdminExecutor implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender.isOp()) {
            JSONArray adminsArray = HardcoreClaimManager.getInstance().getAdminsArray();
            String adminsList = Messages.getMessages("admins_list") + "\n";
            for (int i = 0; i < adminsArray.size(); i++) {
                adminsList += ((JSONObject) adminsArray.get(i)).get("name") + "\n";
            }
            commandSender.sendMessage(ChatColor.AQUA + adminsList);
            return true;
        }
        else {
            commandSender.sendMessage(ChatColor.RED + Messages.getMessages("not_allow"));
            return false;
        }
    }
}
