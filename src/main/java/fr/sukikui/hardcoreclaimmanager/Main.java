package fr.sukikui.hardcoreclaimmanager;

import fr.sukikui.hardcoreclaimmanager.command.ProtectAdminCommand;
import fr.sukikui.hardcoreclaimmanager.command.ProtectCommand;
import fr.sukikui.hardcoreclaimmanager.data.DataBase;
import fr.sukikui.hardcoreclaimmanager.joueur.ConnexionListener;
import fr.sukikui.hardcoreclaimmanager.joueur.JoueurListener;
import fr.sukikui.hardcoreclaimmanager.terrain.TerrainLoader;
import fr.sukikui.hardcoreclaimmanager.terrain.TerrainProtecter;
import fr.sukikui.hardcoreclaimmanager.terrain.TerrainResizer;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin
{
    @Override
    public void onEnable()
    {
        new DataBase(null).importData();

        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents(new ConnexionListener(), this);
        pm.registerEvents(new JoueurListener(), this);
        pm.registerEvents(new TerrainLoader(), this);
        pm.registerEvents(new TerrainProtecter(), this);
        pm.registerEvents(new TerrainResizer(), this);

        PluginCommand cmd = this.getCommand("protect");
        assert cmd != null;
        cmd.setExecutor(new ProtectCommand());
        cmd.setTabCompleter(new ProtectCommand());

        PluginCommand cmd_admin = this.getCommand("protectadmin");
        assert cmd_admin != null;
        cmd_admin.setExecutor(new ProtectAdminCommand());
        cmd_admin.setTabCompleter(new ProtectAdminCommand());
    }
}
