package fr.sukikui.hardcoreclaimmanager.terrain;

import fr.sukikui.hardcoreclaimmanager.ActionBar;
import fr.sukikui.hardcoreclaimmanager.joueur.Joueur;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

public class TerrainProtecter implements Listener
{
    /**
     * Détecte si un bloc est posé.
     */
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event)
    {
        interractTerrain(event, event.getPlayer(), event.getBlock());
    }

    /**
     * Détecte si un bloc est détruit.
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event)
    {
        interractTerrain(event, event.getPlayer(), event.getBlock());
    }

    /**
     * Détecte si un joueur interagit.
     */
    @EventHandler
    public void onInteract(PlayerInteractEvent event)
    {
        Block block = event.getClickedBlock();
        if(block != null)
            interractTerrain(event, event.getPlayer(), block);
    }


    /**
     * Annule l'évènement si le bloc considéré est contenu dans un terrain qui dont le
     * joueur n'est pas autorisé à intéragir dessus.
     *
     * @param event     instance de l'évènement à annuler
     * @param player    joueur ayant effectué l'action
     * @param block     bloc ayant reçu l'action
     */
    private void interractTerrain(@NotNull Cancellable event, @NotNull Player player, @NotNull Block block)
    {
        Material type = block.getType();

        if(type == Material.TNT || type == Material.END_CRYSTAL) return;

        Location loc = block.getLocation();
        World world = loc.getWorld();
        Joueur joueur = Joueur.get(player);


        // Vérification préalable pour savoir si le terrain est le même qu'à la dernière interraction.
        // Cela permet d'optimiser le traitement sans devoor itérer tous les terrains.

        Object mde = joueur.last_interact_terrain[0];

        if(mde != null && ((Monde) mde).name.equalsIgnoreCase(world.getName()))
        {
            if(joueur.last_interact_terrain[1] != null)
            {
                Terrain ter = (Terrain) joueur.last_interact_terrain[1];

                if(loc.getBlockX() >= ter.xmin && loc.getBlockX() <= ter.xmax
                        && loc.getBlockZ() >= ter.zmin && loc.getBlockZ() <= ter.zmax)
                {
                    if(ter.owner != joueur && !ter.joueurs.contains(joueur))
                    {
                        event.setCancelled(true);
                        ActionBar.send(player, "§cTerrain protégé par " + ter.owner.getName());
                    }
                    return;
                }
            }
        }

        // Itère la liste des mondes puis la liste des terrains chargés sur le monde idéntifié ou non.

        for(Monde monde : Monde.list)
        {
            if(monde.name == world.getName())
            {
                for(Terrain ter : monde.loaded_terrains)
                {
                    if(loc.getBlockX() >= ter.xmin && loc.getBlockX() <= ter.xmax
                            && loc.getBlockZ() >= ter.zmin && loc.getBlockZ() <= ter.zmax)
                    {
                        joueur.last_interact_terrain[0] = monde;
                        joueur.last_interact_terrain[1] = ter;

                        if(ter.owner != joueur && !ter.joueurs.contains(joueur))
                        {
                            event.setCancelled(true);
                            ActionBar.send(player, "§cTerrain protégé par " + ter.owner.getName());
                        }
                        return;
                    }
                }
                return;
            }
        }
    }



}
