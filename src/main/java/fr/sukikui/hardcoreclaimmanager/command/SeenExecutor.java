package fr.sukikui.hardcoreclaimmanager.command;

import fr.sukikui.hardcoreclaimmanager.Messages;
import fr.sukikui.hardcoreclaimmanager.player.PlayerData;
import fr.sukikui.hardcoreclaimmanager.player.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.Period;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class SeenExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length == 1) {
            PlayerData playerData = PlayerDataManager.getInstance().getPlayerDataByName(strings[0]);
            if (playerData != null) {
                String message = getLastConnection(playerData);
                commandSender.sendMessage(ChatColor.AQUA + playerData.getPlayerName() + message);
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

    /**
     * Create a message with the number of years, months, days, hours, minutes and seconds since the player is connected
     * or disconnected
     * @param playerData the player's data
     * @return the message
     */
    private String getLastConnection(PlayerData playerData) {
        Date lastJoinDate = new Date(playerData.getLastJoinDate());
        Date now = new Date();
        long differenceInTime = now.getTime() - lastJoinDate.getTime();
        long years = TimeUnit.MILLISECONDS.toDays(differenceInTime) / 365;
        long months = TimeUnit.MILLISECONDS.toDays(differenceInTime) % 30;
        long days = TimeUnit.MILLISECONDS.toDays(differenceInTime) % 365 - 30 * months;
        long hours = TimeUnit.MILLISECONDS.toHours(differenceInTime) % 24;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(differenceInTime) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(differenceInTime) % 60;
        if (Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(playerData.getPlayerName()))) {
            return String.format(Messages.getMessages("connected"), hours, minutes, seconds);
        }
        else {
            return String.format(Messages.getMessages("not_connected"),years,months,days,hours,minutes,seconds);
        }
    }
}
