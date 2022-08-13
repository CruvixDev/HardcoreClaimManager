package fr.sukikui.hardcoreclaimmanager.joueur;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoueurListener implements Listener
{
    /**
     * Détecte lorsqu'un joueur rejoint le serveur.
     * Permet d'actualiser les noms des joueurs.
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();

        for(Joueur joueur : Joueur.list)
        {
            if(joueur.getUUID() == player.getUniqueId())
                joueur.name = player.getName();
        }
    }
}
