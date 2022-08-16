package fr.sukikui.hardcoreclaimmanager;

import fr.sukikui.hardcoreclaimmanager.command.*;
import fr.sukikui.hardcoreclaimmanager.listener.ClaimEventHandler;
import fr.sukikui.hardcoreclaimmanager.listener.PlayerEventHandler;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.Properties;

public final class HardcoreClaimManager extends JavaPlugin {
    private static Properties properties = new Properties();
    private String pluginFolderPath = this.getDataFolder().getAbsolutePath();
    private InputStream inputStream;
    private OutputStream outputStream;
    private File propertyFile;

    @Override
    public void onEnable() {
        this.getCommand("addClaimBlocks").setExecutor(new AddClaimBlocksExecutor());
        this.getCommand("removeClaimBlocks").setExecutor(new RemoveClaimBlocksExecutor());
        this.getCommand("setBlockRate").setExecutor(new SetBlockRateExecutor());
        this.getCommand("showClaimBlocks").setExecutor(new ShowClaimBlocksExecutor());
        this.getCommand("showClaim").setExecutor(new ShowClaimsExecutor());
        this.getCommand("showTool").setExecutor(new ShowToolExecutor());
        this.getCommand("changeTool").setExecutor(new ToolChangeExecutor());
        this.getCommand("trustPlayers").setExecutor(new TrustPlayerExecutor());
        this.getCommand("unregisterClaim").setExecutor(new UnregisterClaimExecutor());
        this.getServer().getPluginManager().registerEvents(new PlayerEventHandler(), this);
        this.getServer().getPluginManager().registerEvents(new ClaimEventHandler(), this);

        try {
            propertyFile = new File(pluginFolderPath + "hardcoreClaimManager.properties");
            if (!propertyFile.exists()) {
                inputStream = this.getClassLoader().getResourceAsStream("hardcoreClaimManager.properties");
                propertyFile.createNewFile();
            }
            else {
                inputStream = new FileInputStream(propertyFile);
            }
            properties.load(inputStream);
            properties.setProperty("database-path",this.getDataFolder().getAbsolutePath());
            outputStream = new FileOutputStream(propertyFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        try {
            properties.store(outputStream,null);
            outputStream.flush();
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Properties getProperties() {
        return properties;
    }
}
