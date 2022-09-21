package fr.sukikui.hardcoreclaimmanager.command;

import fr.sukikui.hardcoreclaimmanager.player.PlayerData;
import fr.sukikui.hardcoreclaimmanager.player.PlayerDataManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class SeenExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length == 1) {
            PlayerData playerData = PlayerDataManager.getInstance().getPlayerDataByName(strings[0]);
            if (playerData != null) {
                String message = getLastConnection(playerData.getJoinDate());
                commandSender.sendMessage(ChatColor.AQUA + playerData.getPlayerName() + message);
                return true;
            }
            else {
                commandSender.sendMessage(ChatColor.RED + "The player given does not exist!");
                return false;
            }
        }
        else {
            commandSender.sendMessage(ChatColor.RED + "Too many arguments given!");
            return false;
        }
    }

    private String getLastConnection(long playerJoinDate) {
        Date lastJoinDate = new Date(playerJoinDate);
        Date now = new Date();
        long differenceInTime = now.getTime() - lastJoinDate.getTime();
        long years = TimeUnit.MILLISECONDS.toDays(differenceInTime) / 365;
        long months = TimeUnit.MILLISECONDS.toDays(differenceInTime) / (365/12);
        long days = TimeUnit.MILLISECONDS.toDays(differenceInTime) % 365;
        long hours = TimeUnit.MILLISECONDS.toHours(differenceInTime) % 24;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(differenceInTime) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(differenceInTime) % 60;
        return String.format(" last connection: %d year(s) %d month(s) %d day(s) %d hour(s) %d minute(s) %d " +
                "second(s)", years, months, days, hours, minutes, seconds);
    }
}
