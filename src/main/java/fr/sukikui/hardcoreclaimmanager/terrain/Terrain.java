package fr.sukikui.hardcoreclaimmanager.terrain;

import fr.sukikui.hardcoreclaimmanager.joueur.Joueur;

import java.util.ArrayList;

public class Terrain
{
    private int id;

    // Données des joueurs
    public final Joueur owner;
    public final ArrayList<Joueur> joueurs;

    // Coordonnées du terrain
    public int xmin;
    public int xmax;
    public int zmin;
    public int zmax;


    public Terrain(int id, Joueur owner, ArrayList<Joueur> joueurs, int xmin, int xmax, int zmin, int zmax)
    {
        this.owner = owner;
        this.joueurs = joueurs;
        this.xmin = xmin;
        this.xmax = xmax;
        this.zmin = zmin;
        this.zmax = zmax;
    }



}
