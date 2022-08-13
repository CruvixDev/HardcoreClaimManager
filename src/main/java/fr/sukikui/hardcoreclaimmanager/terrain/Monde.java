package fr.sukikui.hardcoreclaimmanager.terrain;

import sun.awt.Mutex;

import java.util.ArrayList;

/**
 * Classe permettant de représenter un monde et plus particulièrement d'avoir accès
 * au terrains protégés par les joueurs en différençiant les terrains chargés dans
 * un chunk des non chargés dans le but d'améliorer les performances.
 */
public class Monde
{
    static public final ArrayList<Monde> list = new ArrayList<>();

    public final String name;
    private final ArrayList<Terrain> terrains;

    protected final transient Mutex mutex = new Mutex();
    protected final transient ArrayList<Terrain> loaded_terrains;
    protected final transient ArrayList<Terrain> unloaded_terrains;


    public Monde(String name, ArrayList<Terrain> terrains)
    {
        this.name = name;
        this.terrains = terrains;
        this.loaded_terrains = new ArrayList<>();
        this.unloaded_terrains = terrains;
        list.add(this);
    }
}
