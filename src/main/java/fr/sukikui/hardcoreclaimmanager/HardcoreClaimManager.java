package fr.sukikui.hardcoreclaimmanager;

import fr.sukikui.hardcoreclaimmanager.claim.PlayerAddBlocksTask;
import fr.sukikui.hardcoreclaimmanager.command.*;
import fr.sukikui.hardcoreclaimmanager.listener.ClaimEventHandler;
import fr.sukikui.hardcoreclaimmanager.listener.PlayerEventHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.*;
import java.util.Properties;

/**
 * The plugin main class
 */
public final class HardcoreClaimManager extends JavaPlugin {
    private Properties properties = new Properties();
    private File propertyFile = new File(this.getDataFolder().getAbsolutePath() +
            "\\hardcoreClaimManager.properties");
    private OutputStream outputStream;

    @Override
    public void onEnable() {
        this.getCommand("addClaimBlocks").setExecutor(new AddClaimBlocksExecutor());
        this.getCommand("removeClaimBlocks").setExecutor(new RemoveClaimBlocksExecutor());
        this.getCommand("setBlockRate").setExecutor(new SetBlockRateExecutor(this));
        this.getCommand("showClaimBlocks").setExecutor(new ShowClaimBlocksExecutor());
        this.getCommand("showClaim").setExecutor(new ShowClaimsExecutor());
        this.getCommand("showTool").setExecutor(new ShowToolExecutor(this));
        this.getCommand("showTrustedPlayers").setExecutor(new ShowTrustedPlayersExecutor());
        this.getCommand("changeTool").setExecutor(new ToolChangeExecutor(this));
        this.getCommand("trustPlayers").setExecutor(new TrustPlayerExecutor());
        this.getCommand("unTrustPlayers").setExecutor(new UnTrustPlayerExecutor());
        this.getCommand("unregisterClaim").setExecutor(new UnregisterClaimExecutor());

        this.getServer().getPluginManager().registerEvents(new PlayerEventHandler(this), this);
        this.getServer().getPluginManager().registerEvents(new ClaimEventHandler(), this);

        BukkitScheduler scheduler = Bukkit.getScheduler();
        int period;
        try {
            period = Integer.parseInt(properties.getProperty("clock-block-gain-duration"));
        }
        catch (NumberFormatException e) {
            period = 1;
        }
        scheduler.scheduleSyncRepeatingTask(this, new PlayerAddBlocksTask(this),
                (long) (period * 60 / 0.05), (long) (period * 60 / 0.05));

        loadProperties();
        storeProperties();
    }

    @Override
    public void onDisable() {
        storeProperties();
    }

    /**
     *
     * @return the properties object containing all plugin configurations
     */
    public Properties getProperties() {
        return this.properties;
    }

    /**
     * Load all the configurations in the hardcoreClaimManager.properties file and created it, if it does not exist in
     * the plugin folder
     */
    private void loadProperties() {
        try {
            InputStream inputStream;
            if (!propertyFile.exists()) {
                inputStream = this.getClassLoader().getResourceAsStream("hardcoreClaimManager.properties");
                properties.load(inputStream);
                File pluginFolder = new File(this.getDataFolder().getAbsolutePath());
                pluginFolder.mkdirs();
                propertyFile.createNewFile();
            }
            else {
                inputStream = new FileInputStream(propertyFile);
            }
            properties.load(inputStream);
            properties.setProperty("database-path",this.getDataFolder().getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Store all configurations in the hardcoreClaimManager.properties file
     */
    public void storeProperties() {
        try {
            if (propertyFile.exists()) {
                outputStream = new FileOutputStream(propertyFile);
                properties.store(outputStream,null);
                outputStream.flush();
                outputStream.close();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
