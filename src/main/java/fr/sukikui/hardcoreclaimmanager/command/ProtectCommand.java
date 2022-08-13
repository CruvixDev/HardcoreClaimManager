package fr.sukikui.hardcoreclaimmanager.command;

import fr.sukikui.hardcoreclaimmanager.data.Configuration;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProtectCommand implements CommandExecutor, TabCompleter
{
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
    {
        return false;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
    {
        /**
         *  /protect |
         *           |  liste
         *           |  creer
         *           |  supprimer
         *           |
         *           |  info |
         *           |       |  x
         *           |       |  <numero>
         *           |
         *           |  ajouter |
         *           |  retirer |
         *                      |  <joueur>
         */

        if(args.length == 0)    // /protect <...>
        {
            return Arrays.asList("info", "liste", "creer", "supprimer", "ajouter", "retirer");
        }
        else
        {
            String arg1 = args[0];

            if(arg1.equalsIgnoreCase("ajouter") || arg1.equalsIgnoreCase("retirer"))
            {
                if(args.length == 1)
                    return getOnlinePlayers();
            }
            else if(arg1.equalsIgnoreCase("info"))
            {
                if(args.length == 1)
                    return null;        // TODO numero du terrain par rapport au numero donné dans /protect liste
            }
        }

        return null;
    }


    private ArrayList<String> getOnlinePlayers()
    {
        ArrayList<String> list = new ArrayList<>();

        for(World world : Configuration.worlds)
        {
            for(Player player : world.getPlayers())
                list.add(player.getName());
        }
        return list;
    }
}
