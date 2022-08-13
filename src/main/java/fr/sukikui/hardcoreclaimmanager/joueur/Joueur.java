package fr.sukikui.hardcoreclaimmanager.joueur;

import fr.sukikui.hardcoreclaimmanager.data.DataBase;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Cette classe permet, à contrario de la classe Player, de pouvoir lier
 * un nom de joueur pour un UUID donné même lorsque le joueur est déconnecté.
 *
 * Cette classe a donc pour but d'être enregistrée dans une base de données et
 * permet de stocker les données des joueurs connectés et déconnectés : nombres
 * de blocks à disposition pour protéger ses terrains et dernier nom connu du joueur.
 */
public class Joueur
{
    static protected final ArrayList<Joueur> list = new ArrayList<>();

    /**
     * Toute instance de Joueur est obtenable uniquement par cette méthode static.
     *
     * @return l'instance de Joueur correspondante.
     */
    static public Joueur get(Player player)
    {
        UUID uuid = player.getUniqueId();

        for(Joueur joueur : list)
        {
            if(uuid == joueur.uuid)
                return joueur;
        }
        Joueur joueur = new Joueur(uuid, player.getName(), 0);
        new DataBase().updateJoueur(joueur);
        return joueur;
    }

    static public void addFromDB(UUID uuid, String name, int blocks)
    {
        new Joueur(uuid, name, blocks);
    }


    private final UUID uuid;
    protected String name;
    protected int blocks;

    // Tableau du type [Monde, Terrain] représentant le dernier terrain avec lequel le joueur à intéragit.
    public final Object[] last_interact_terrain = new Object[2];

    private Joueur(UUID uuid, String name, int blocks)
    {
        this.uuid = uuid;
        this.name = name;
        this.blocks = blocks;
        list.add(this);
    }

    public UUID getUUID()
    {
        return this.uuid;
    }

    public String getName()
    {
        return this.name;
    }

    public int getBlocks()
    {
        ConnexionListener inst = new ConnexionListener();
        inst.save(this);
        inst.record(this);
        return this.blocks;
    }
}
