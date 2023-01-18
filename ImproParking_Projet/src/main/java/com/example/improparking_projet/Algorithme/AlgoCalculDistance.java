package com.example.improparking_projet.Algorithme;

import com.example.improparking_projet.GestionMap.Carte;
import com.example.improparking_projet.GestionMap.Cases.Case;
import com.example.improparking_projet.GestionMap.TypeMouvement;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class AlgoCalculDistance {
    private Carte carte;
    private HashMap<Case,Integer> distances;

    /**
     * Constructeur de la classe
     * @param carte correspond à la carte de l'application
     */
    public AlgoCalculDistance(Carte carte){
        this.carte= carte;
        this.distances= new HashMap<>();
    }

    /**
     * Méthode permettant de récupérer la carte du jeu
     * @return Carte
     */
    protected Carte getCarte(){
        return this.carte;
    }

    /**
     * Méthode permettant d'insert le couple (position,valeur)dans la HashMap distances
     * @param position correspond à la position (case) d'un élément
     * @param valeur correspond à la distance de l'élément
     */
    protected void setDistance(Case position, int valeur){
        this.distances.put(position, valeur);
    }

    /**
     * Méthode permettant de récupérer le distance de la case donner en paramètre
     * @param arrivee correspond à une case
     * @return la distance
     */
    public Integer getDistance(Case arrivee){
        return this.distances.get(arrivee);
    }

    /**
     * Méthode permettant de vider la HashMap
     */
    protected void reinitialisationDistances(){
        this.distances.clear();
    }

    /**
     * Méthode permettant de calculer la distance depuis une case donnée en paramètre
     * @param depart correspond à une case
     */
    public abstract void calculerDistancesDepuis(Case depart);

    /**
     * Méthode qui détermine le plus court chemin de la case départ à celle donnée en paramètre
     * @param arrivee correspond à une case
     * @return une liste de Mouvement
     */
    public abstract ArrayList<TypeMouvement> getChemin(Case arrivee);

}
