package fr.sukikui.hardcoreclaimmanager;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class HardcoreClaimManager extends JavaPlugin {
    private static Properties properties = new Properties();
    private InputStream stream = this.getClassLoader().getResourceAsStream("hardcoreClaimManager.properties");

    @Override
    public void onEnable() {
        try {
            //TODO create the properties file in the plugin folder and read into it
            properties.load(stream);
            if (properties.getProperty("database-path").equals("")) {
                properties.setProperty("database-path",this.getDataFolder().getAbsolutePath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        try {
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Properties getProperties() {
        return properties;
    }
}
