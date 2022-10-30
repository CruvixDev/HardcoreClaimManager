package fr.sukikui.hardcoreclaimmanager;

import fr.sukikui.hardcoreclaimmanager.claim.PlayerAddBlocksTask;
import fr.sukikui.hardcoreclaimmanager.command.*;
import fr.sukikui.hardcoreclaimmanager.data.DatabaseManager;
import fr.sukikui.hardcoreclaimmanager.listener.ClaimEventHandler;
import fr.sukikui.hardcoreclaimmanager.listener.PlayerEventHandler;
import fr.sukikui.hardcoreclaimmanager.player.PlayerData;
import fr.sukikui.hardcoreclaimmanager.player.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

/**
 * The plugin main class
 */
public final class HardcoreClaimManager extends JavaPlugin {
    private Properties properties = new Properties();
    private JSONArray adminsArray;
    private File propertyFile = new File(this.getDataFolder().getAbsolutePath() +
            "\\hardcoreClaimManager.properties");
    private File adminsFile = new File(this.getDataFolder().getAbsolutePath() + "\\admins.json");
    private static HardcoreClaimManager hardcoreClaimManager;

    @Override
    public void onEnable() {
        hardcoreClaimManager = this;

        File pluginFolder = new File(this.getDataFolder().getAbsolutePath());
        if (!pluginFolder.exists()) {
            pluginFolder.mkdirs();
        }
        this.loadProperties();
        this.loadMessages();
        this.readAdmins();
        this.storeProperties();
        DatabaseManager.getInstance(this).createDatabase();
        DatabaseManager.getInstance(this).getAll();

        this.getCommand("addClaimBlocks").setExecutor(new AddClaimBlocksExecutor());
        this.getCommand("removeClaimBlocks").setExecutor(new RemoveClaimBlocksExecutor());
        this.getCommand("setBlockRate").setExecutor(new SetBlockRateExecutor(this));
        this.getCommand("showClaimBlocks").setExecutor(new ShowClaimBlocksExecutor());
        this.getCommand("showClaim").setExecutor(new ShowClaimsExecutor());
        this.getCommand("showTool").setExecutor(new ShowToolExecutor(this));
        this.getCommand("showTrustedPlayers").setExecutor(new ShowTrustedPlayersExecutor());
        this.getCommand("changeTool").setExecutor(new ToolChangeExecutor(this));
        this.getCommand("trustPlayers").setExecutor(new TrustPlayerExecutor(this));
        this.getCommand("unTrustPlayers").setExecutor(new UnTrustPlayerExecutor(this));
        this.getCommand("removeClaim").setExecutor(new UnregisterClaimExecutor(this));
        this.getCommand("seen").setExecutor(new SeenExecutor());
        this.getCommand("addAdmin").setExecutor(new AddAdminExecutor());
        this.getCommand("removeAdmin").setExecutor(new RemoveAdminExecutor());
        this.getCommand("resetClaimBlocks").setExecutor(new ResetClaimBlocks());
        this.getCommand("showAdmin").setExecutor(new ShowAdminExecutor());

        this.getServer().getPluginManager().registerEvents(new PlayerEventHandler(this), this);
        this.getServer().getPluginManager().registerEvents(new ClaimEventHandler(), this);

        long currentTime = System.currentTimeMillis();
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerData playerData = PlayerDataManager.getInstance().getPlayerDataByName(player.getName());
            playerData.setLastSaveBlocksGain(currentTime);
        }

        this.runBlockGainTask();
    }

    @Override
    public void onDisable() {
        storeProperties();
        for (PlayerData playerData : PlayerDataManager.getInstance().getPlayersData()) {
            DatabaseManager.getInstance(this).updatePlayerClaimBlocks(playerData);
            DatabaseManager.getInstance(this).updateLastJoinDate(playerData);
        }
        Bukkit.getScheduler().cancelTasks(this);
    }

    /**
     * Method to get the unique instance of this plugin
     * @return the unique instance of this plugin
     */
    public static HardcoreClaimManager getInstance() {
        return hardcoreClaimManager;
    }

    /**
     *
     * @return the properties object containing all plugin configurations
     */
    public Properties getProperties() {
        return this.properties;
    }

    public JSONArray getAdminsArray() {
        return (JSONArray) this.adminsArray.clone();
    }

    /**
     * Load all the configurations in the hardcoreClaimManager.properties file and created it, if it does not exist in
     * the plugin folder
     */
    private void loadProperties() {
        InputStream inputStream = null;
        try {
            if (!propertyFile.exists()) {
                inputStream = this.getClassLoader().getResourceAsStream("hardcoreClaimManager.properties");
                properties.load(inputStream);
                propertyFile.createNewFile();
            }
            else {
                inputStream = new FileInputStream(propertyFile);
                properties.load(inputStream);
            }
            properties.setProperty("database-path",this.getDataFolder().getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                }
                catch (IOException e) {}
            }
        }
    }

    /**
     * Load the strings.xml files into the plugin's data folder, to load all messages handled by the plugin.
     */
    private void loadMessages() {
        InputStream sourceStream = null;
        try {
            sourceStream = this.getClass().getResourceAsStream("/strings.xml");
            File dest = new File(this.getDataFolder().getAbsolutePath() + "/strings.xml");
            Files.copy(sourceStream,dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (sourceStream != null) sourceStream.close();
            }
            catch (IOException e) {}
        }
    }

    /**
     * Method to get the list of operators for this plugin
     * @return the array in JSON format of all operators
     */
    private void readAdmins() {
        InputStream inputStream = null;
        FileReader fileReader = null;
        try {
            if (!this.adminsFile.exists()) {
                this.adminsFile.createNewFile();
                inputStream = this.getClassLoader().getResourceAsStream("admins.json");
                Files.copy(inputStream,adminsFile.toPath(),StandardCopyOption.REPLACE_EXISTING);
            }
            JSONParser parser = new JSONParser();
            fileReader = new FileReader(this.adminsFile);
            this.adminsArray = (JSONArray) parser.parse(fileReader);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (inputStream != null) inputStream.close();
                if (fileReader != null) fileReader.close();
            }
            catch (IOException e) {}
        }
    }

    /**
     * Store all configurations in the hardcoreClaimManager.properties file
     */
    public void storeProperties() {
        OutputStream outputStream = null;
        try {
            if (propertyFile.exists()) {
                outputStream = new FileOutputStream(propertyFile);
                properties.store(outputStream,null);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (outputStream != null) {
                    outputStream.flush();
                    outputStream.close();
                }
            }
            catch (IOException e) {}
        }
    }

    /**
     * Store the list of operators for this plugin
     */
    private void storeAdmins() {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(adminsFile);
            fileWriter.write(this.adminsArray.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (fileWriter != null) {
                    fileWriter.flush();
                    fileWriter.close();
                }
            }
            catch (IOException e) {}
        }
    }

    /**
     * Run the block gain task
     */
    public void runBlockGainTask() {
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
    }

    /**
     * Verify if a player is admin or not for this plugin
     * @param playerData the player's data to verify
     * @return the object concerning the admin player or null if it is not an admin
     */
    public JSONObject isAdmin(PlayerData playerData) {
        JSONObject jsonObject;
        this.readAdmins();
        for (int i = 0; i < this.adminsArray.size(); i++) {
            jsonObject = (JSONObject) this.adminsArray.get(i);
            if (jsonObject.get("name").equals(playerData.getPlayerName())) {
                return jsonObject;
            }
        }
        return null;
    }

    /**
     * Add admin for this plugin
     * @param playerData the player's data to verify
     * @return true is the player become admin, false if it is already admin
     */
    public boolean addAdmins(PlayerData playerData) {
        if (this.isAdmin(playerData) == null) {
            this.readAdmins();
            JSONObject newAdmin = new JSONObject();
            newAdmin.put("name",playerData.getPlayerName());
            newAdmin.put("uuid",playerData.getPlayerUUID().toString());
            this.adminsArray.add(newAdmin);
            this.storeAdmins();
            return true;
        }
        return false;
    }

    /**
     * Remove an admin for this plugin
     * @param playerData the player's data to verify
     * @return true if the player is no longer admin for this plugin, false if it is already not an admin
     */
    public boolean removeAdmin(PlayerData playerData) {
        JSONObject admin = this.isAdmin(playerData);
        if (admin != null) {
            this.readAdmins();
            this.adminsArray.remove(admin);
            this.storeAdmins();
            return true;
        }
        return false;
    }
}
