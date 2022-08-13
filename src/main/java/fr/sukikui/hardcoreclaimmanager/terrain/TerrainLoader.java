package fr.sukikui.hardcoreclaimmanager.terrain;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.util.ArrayList;

public class TerrainLoader implements Listener
{
    /**
     * Ajoute les terrains situés dans un chunk chargé à la liste des terrains chargés.
     */
    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event)
    {
        Chunk chunk = event.getChunk();
        World world = chunk.getWorld();
        permute(chunk, true);
    }

    /**
     * Retire les terrains situés dans un chunk déchargé de la liste des terrains chargés.
     */
    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event)
    {
        Chunk chunk = event.getChunk();
        World world = chunk.getWorld();
        permute(chunk,false);
    }


    /**
     * Méthode qui s'occupe de passer un chunk d'une liste à l'autre.
     *
     * Pour éviter toute interférence entre les listes des chunks chargés et déchargés,
     * l'attribut mutex propre à chaque monde doit être vérouillé puis dévérouillé dés
     * lors que l'on souhaite manipuler les listes.
     *
     * @param chunk             chunk qui s'est chargé ou déchargé
     * @param to_loaded_list    true si le chunk vient de se charger sinon false
     */
    private void permute(Chunk chunk, boolean to_loaded_list)
    {
        World world = chunk.getWorld();

        for(Monde monde : Monde.list)
        {
            if (monde.name == world.getName())
            {
                monde.mutex.lock();
                try
                {
                    ArrayList<Terrain> old_list = to_loaded_list ? monde.unloaded_terrains : monde.loaded_terrains;
                    ArrayList<Terrain> new_list = to_loaded_list ? monde.loaded_terrains : monde.unloaded_terrains;
                    ArrayList<Terrain> clone_list = (ArrayList<Terrain>) old_list.clone();

                    for (Terrain terrain : clone_list)
                    {
                        int xmin = chunk.getX() * 16;
                        int xmax = xmin + 15;
                        int zmin = chunk.getZ() * 16;
                        int zmax = zmin + 15;

                        if (itr(xmin, xmax, terrain.xmin, terrain.xmax) && itr(zmin, zmax, terrain.zmin, terrain.zmax))
                        {
                            old_list.remove(terrain);
                            new_list.add(terrain);
                        }
                    }
                }
                finally { monde.mutex.unlock(); }
                return;
            }
        }
    }

    /**
     * @return  true si les deux segments 1 et 2 possèdent une partie en commun
     *          respectivement délimité par leurs minimums et leurs maximums.
     */
    private boolean itr(int min1, int max1, int min2, int max2)
    {
        return ((min1 >= min2 && min1 <= max2)
                || (max1 >= min2 && max1 <= max2)
                || (min2 >= min1 && min2 <= max1)
                || (max2 >= min1 && max2 <= max1));
    }
}
