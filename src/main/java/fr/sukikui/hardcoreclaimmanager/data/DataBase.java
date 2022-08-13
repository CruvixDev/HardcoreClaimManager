package fr.sukikui.hardcoreclaimmanager.data;

import java.sql.*;
import java.util.UUID;

import fr.sukikui.hardcoreclaimmanager.joueur.Joueur;
import fr.sukikui.hardcoreclaimmanager.terrain.Monde;
import fr.sukikui.hardcoreclaimmanager.terrain.Terrain;
import org.bukkit.Bukkit;
import org.bukkit.World;

public class DataBase
{
    static private final String path = "jdbc:sqlite:PMC_Protection_DB.db";
    private Connection con = null;

    /**
     * Constructeur à utiliser la première fois.
     * Crée la base de données et les tables si ces dernières n'existent pas.
     * Se connecte à la base et reformate les tables si celles-ci sont incohérentes.
     */
    public DataBase(Object o)
    {
        try
        {
            this.con = DriverManager.getConnection(path);

            Statement st = this.con.createStatement();
            st.addBatch("CREATE TABLE IF NOT EXISTS Terrain (terrain_id INT NOT NULL, " +
                    "xmin INT NOT NULL, " +
                    "xmax INT NOT NULL, " +
                    "zmin INT NOT NULL, " +
                    "zmax INT NOT NULL, " +
                    "owner_uuid TEXT NOT NULL, " +
                    "monde_name TEXT NOT NULL)");

            st.addBatch("CREATE TABLE IF NOT EXISTS Joueur (uuid TEXT NOT NULL, " +
                    "name TEXT NOT NULL, " +
                    "blocks INT NOT NULL)");

            st.addBatch("CREATE TABLE IF NOT EXISTS Monde (name TEXT NOT NULL)");
            st.addBatch("CREATE TABLE IF NOT EXISTS Configuration (blocks_rate INT NOT NULL)");
            st.executeBatch();

            ResultSet rs = this.con.createStatement().executeQuery("SELECT COUNT(*) FROM Configuration");
            int count = rs.getInt(1);

            if(count != 1)
            {
                Statement config = this.con.createStatement();
                if(count > 1)
                    config.addBatch("DELETE FROM Configuration");
                config.addBatch("INSERT INTO Configuration (blocks_rate) VALUES (" + Configuration.blocks_rate + ")");
                config.executeBatch();
            }
        }
        catch (SQLException e) { e.printStackTrace(); }
    }

    /**
     * Constructeur à utiliser ensuite.
     */
    public DataBase()
    {
        try
        {
            this.con = DriverManager.getConnection(path);
        }
        catch (SQLException e) { e.printStackTrace(); }
    }

    /**
     * Importe toutes les données de la base.
     */
    public void importData()
    {
        if(this.con == null) return;

        try
        {
            ResultSet rs;

            // Importation de la configuration.
            // Vérifie qu'il n'y a qu'une unique occurence dans la table Configuration.
            rs = this.con.createStatement().executeQuery("SELECT COUNT(*) FROM Configuration");
            int count = rs.getInt(1);

            if(count != 1)
            {
                Statement config = this.con.createStatement();
                if(count > 1)
                    config.addBatch("DELETE FROM Configuration");
                config.addBatch("INSERT INTO Configuration (blocks_rate) VALUES (" + Configuration.blocks_rate + ")");
                config.executeBatch();
            }
            else
            {
                rs = this.con.createStatement().executeQuery("SELECT * FROM Configuration");
                Configuration.blocks_rate = rs.getInt("blocks_rate");
            }


            // Importation des mondes actifs.
            rs = this.con.createStatement().executeQuery("SELECT * FROM Monde");
            do
            {
                World world = Bukkit.getWorld(rs.getString("name"));
                if(world != null)
                    Configuration.worlds.add(world);

                rs.next();
            }
            while(!rs.isLast());

            // Importation des joueurs.
            rs = this.con.createStatement().executeQuery("SELECT * FROM Joueur");
            do
            {
                try
                {
                    UUID uuid = UUID.fromString(rs.getString("uuid"));
                    String name = rs.getString("name");
                    int blocks = rs.getInt("blocks");

                    Joueur.addFromDB(uuid, name, blocks);
                    rs.next();
                }
                catch (IllegalArgumentException ex) { ex.printStackTrace(); }

            }
            while(!rs.isLast());

            // Importation des terrains.
            rs = this.con.createStatement().executeQuery("SELECT * FROM Terrain");
            do
            {

            }
            while(true);
        }
        catch (SQLException e) { e.printStackTrace(); }
    }

    /**
     * Actualise les données du joueur.
     * Supprime les doublons s'il y en a.
     */
    public void updateJoueur(Joueur joueur)
    {
        if(this.con == null) return;

        try
        {
            int count = this.con.createStatement()
                    .executeQuery("SELECT COUNT(*) FROM Joueur WHERE uuid = " + joueur.getUUID())
                    .getInt(1);

            Statement st = this.con.createStatement();

            // Vérifie si le joueur existe déjà ou non.
            if(count == 1)
            {
                st.addBatch("UPDATE Joueur SET name = " + joueur.getName() +
                        ", blocks = " + joueur.getBlocks() +
                        "WHERE uuid = " + joueur.getUUID());
            }
            else
            {
                if(count > 1)
                    st.addBatch("DELETE FROM Joueur WHERE uuid = " + joueur.getUUID());
                st.addBatch("INSERT INTO Joueur (uuid, name, blocks) VALUES (" +
                        joueur.getUUID() + ", " +
                        joueur.getName() + ", " +
                        joueur.getBlocks() + ")");
            }

            st.executeBatch();
        }
        catch(SQLException e) { e.printStackTrace(); }
    }

    /**
     * Actualise les données du terrain.
     * Supprime les doublons incohérents s'il y en a.
     */
    public void updateTerrain(Terrain terrain)
    {

    }

    /**
     * Supprime le terrain, y compris ses doublons incohérents s'il y en a.
     */
    public void removeTerrain(Terrain terrain)
    {

    }

    /**
     * Ajoute le monde.
     * Supprime les doublons s'il y en a.
     */
    public void addMonde(Monde monde)
    {
        if(this.con == null) return;

        try
        {
            int count = this.con.createStatement()
                    .executeQuery("SELECT COUNT(*) FROM Monde WHERE name = " + monde.name)
                    .getInt(1);

            Statement st = this.con.createStatement();

            // Vérifie que le monde n'est déjà pas enregistré
            if(count != 1)
            {
                if(count > 1)
                    st.addBatch("DELETE FROM Monde WHERE name = " + monde.name);
                st.addBatch("INSERT INTO Monde (name) VALUES (" + monde.name + ")");
            }

            st.executeBatch();
        }
        catch(SQLException e) { e.printStackTrace(); }
    }

    /**
     * Supprime le monde, y compris ses doublons incohérents s'il y en a.
     */
    public void removeMonde(Monde monde)
    {
        if(this.con == null) return;

        try
        {
            Statement st = this.con.createStatement();
            st.addBatch("DELETE FROM Monde WHERE name = " + monde.name);
            st.executeBatch();
        }
        catch(SQLException e) { e.printStackTrace(); }
    }

    /**
     * Actualise la configuration.
     * Vérifie s'il y a bien qu'une unique occurence dans la table, dans le cas contraire, toutes les occurences
     * sont supprimées et une nouvelle est créée à partir des données du plugin.
     */
    public void updateConfiguration()
    {
        if(this.con == null) return;

        try
        {
            int count = this.con.createStatement().executeQuery("SELECT COUNT(*) FROM Configuration").getInt(1);
            Statement config = this.con.createStatement();

            // Vérifie qu'il y a bien une seule occurence puis la modifie
            if(count != 1)
            {
                if(count > 1)
                    config.addBatch("DELETE FROM Configuration");
                config.addBatch("INSERT INTO Configuration (blocks_rate) VALUES (" + Configuration.blocks_rate + ")");
            }
            else
            {
                config.addBatch("UPDATE Configuration SET blocks_rate = " + Configuration.blocks_rate);
            }

            config.executeBatch();
        }
        catch(SQLException e) { e.printStackTrace(); }
    }

    /**
     * Ferme la base de données lorsque l'objet est jeté à la poubelle.
     */
    @Override
    protected void finalize() throws Throwable
    {
        try
        {
            if(con != null)
                con.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
}
