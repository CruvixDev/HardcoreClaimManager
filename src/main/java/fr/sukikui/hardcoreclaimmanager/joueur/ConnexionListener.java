package fr.sukikui.hardcoreclaimmanager.joueur;

import fr.sukikui.hardcoreclaimmanager.data.Configuration;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import sun.awt.Mutex;

import java.time.LocalDateTime;
import java.util.HashMap;

public class ConnexionListener implements Listener
{
    // Date de connexion de chaque joueur connecté.
    static private final HashMap<Joueur, LocalDateTime> player_connections = new HashMap<>();

    // Mutex à utiliser dés lors que l'on souhaite accéder à la HashMap ci-dessus
    static private final Mutex mutex = new Mutex();



    // ===================================== EVENTS ===================================== //

    /**
     * Détecte lorsqu'un joueur rejoint le serveur.
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();

        if(Configuration.worlds.contains(player.getWorld()))
            record(Joueur.get(player));
    }

    /**
     * Détecte lorsqu'un joueur quitte le serveur.
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();

        if(Configuration.worlds.contains(player.getWorld()))
            save(Joueur.get(player));
    }

    /**
     * Détecte lorsqu'un joueur change de monde.
     */
    @EventHandler
    public void onChangeWorld(PlayerChangedWorldEvent event)
    {
        Player player = event.getPlayer();
        World new_world = player.getWorld();
        World old_world = event.getFrom();

        if(Configuration.worlds.contains(old_world) && !Configuration.worlds.contains(new_world))
            save(Joueur.get(player));
        else if(!Configuration.worlds.contains(old_world) && Configuration.worlds.contains(new_world))
            record(Joueur.get(player));
    }



    // ================================= PRIVATE METHODS ================================ //

    /**
     * Enregistre la date de connexion du joueur.
     *
     * le joueur est ajouté à player_connections
     */
    protected void record(Joueur joueur)
    {
        try
        {
            mutex.lock();
            player_connections.put(joueur, LocalDateTime.now());
        }
        finally { mutex.unlock(); }
    }

    /**
     * Met à jour le nombre de blocs de terrains que le joueur possède.
     *
     * player_blocks est màj et le joueur est retiré de player_connections
     * si celui-ci y était présent.
     */
    protected void save(Joueur joueur)
    {
        try
        {
            mutex.lock();
            LocalDateTime ldt = player_connections.get(joueur);
            LocalDateTime now = LocalDateTime.now();

            if(ldt != null)
            {
                int days = now.getDayOfYear() - ldt.getDayOfYear();
                int hours = now.getHour() - ldt.getHour();
                int minutes = now.getMinute() - ldt.getMinute();

                int blocks = 0;
                int rate = Configuration.blocks_rate;
                blocks += rate * minutes / 60;
                blocks += rate * hours;
                blocks += rate * days * 24;

                joueur.blocks += blocks;
                player_connections.remove(joueur);
            }
        }
        finally { mutex.unlock(); }
    }
}
