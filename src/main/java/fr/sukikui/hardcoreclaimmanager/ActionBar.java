package fr.sukikui.hardcoreclaimmanager;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ActionBar
{
    public static void send(@NotNull Player player, @NotNull String mess)
    {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(mess));
    }
}
