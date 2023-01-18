package com.example.improparking_projet.GestionMap.Cases;

import com.example.improparking_projet.GestionMap.Coordonnee;
import com.example.improparking_projet.GestionMap.TypeMouvement;

import java.util.ArrayList;

/**
 * Classe abstraite Case permettant de gerer les cases
 */

public abstract class Case {
    private Coordonnee coordonnee;
    private ArrayList<Case> voisins;
    private int valeur;

    /**
     * Constructeur de la classe Case
     * @param coordonnee Coordonnee de la case
     * @param valeur Valeur de la case
     */
    public Case(Coordonnee coordonnee, int valeur) {
        this.coordonnee = coordonnee;
        voisins= new ArrayList<>();
        this.valeur = valeur;
    }

    /**
     *Permet de connaître les coordonnees d'une case
     * @return coordonnee
     */
    public Coordonnee getCoordonnee() {
        return coordonnee;
    }

    /**
     * Permet de connaître les voisins d'une case
     * @return
     */
    public ArrayList<Case> getVoisins() {
        return voisins;
    }

    /**
     * Permet d'ajouter un voisin à une case
     * @param voisin de type Case
     */
    public void ajouterVoisin(Case voisin){
        this.voisins.add(voisin);
    }

    /**
     * Permet de connaitre la valeur de la case
     * @return
     */
    public int getValeur() {
        return valeur;
    }

    /**
     * Permet d'ajouter un voisin à une case
     * @param voisin de type Case
     */
    public void addVoisin(Case voisin) {
        voisins.add(voisin);
    }

    /**
     * Permet de savoir si une case est accessible ou non
     * @return boolean
     */
    public abstract boolean estAccessible();

    /**
     * Permet de connaître les mouvements qu'il faut réaliser pour aller à une case
     * @param arrivee de type Case
     * @return TypeMouvement
     */
    public TypeMouvement getMouvementPourAller(Case arrivee){
        return this.coordonnee.getMouvementPourAller(arrivee.coordonnee);
    }


}
