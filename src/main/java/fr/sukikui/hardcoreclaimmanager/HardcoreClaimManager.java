package fr.sukikui.hardcoreclaimmanager;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.Properties;

public final class HardcoreClaimManager extends JavaPlugin {
    private static Properties properties = new Properties();

    @Override
    public void onEnable() {
        //TODO create a property file
        //TODO read properties file before ...
        properties.setProperty("databasePath",this.getDataFolder().getAbsolutePath());
    }

    @Override
    public void onDisable() {

    }

    public static Properties getProperties() {
        return properties;
    }
}
