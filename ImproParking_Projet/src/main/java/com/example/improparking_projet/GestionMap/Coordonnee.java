package com.example.improparking_projet.GestionMap;

/**
 * Classe permettant de gerer les coordonnées
 */
public class Coordonnee {
    private int ligne;
    private int colonne;

    /**
     * Constructeur de la classe
     * @param ligne de la coordonnee
     * @param colonne de la coordonnee
     */
    public Coordonnee(int ligne, int colonne) {
        this.ligne = ligne;
        this.colonne = colonne;
    }

    /**
     *Permet de connaitre une ligne de la carte
     * @return entier
     */
    public int getLigne() {
        return ligne;
    }

    /**
     *Permet de connaitre une colonne de la carte
     * @return entier
     */
    public int getColonne() {
        return colonne;
    }

    /**
     * Permet de connaitre les coordonnées d'une case voisine selon le mouvement fait
     * @param mouvement fait par la voiture
     * @return La coordonnée de la case voisine
     */
    public Coordonnee getVoisin(TypeMouvement mouvement){
        Coordonnee coord= new Coordonnee(this.ligne, this.colonne);
        //Si le mouvement est haut alors on enlève une ligne aux coordonnées
        if(mouvement.equals(TypeMouvement.HAUT)) coord.ligne-=1;
        //Si le mouvement est bas alors on ajoute une ligne aux coordonnées
        if(mouvement.equals(TypeMouvement.BAS)) coord.ligne+=1;
        //Si le mouvement est gauche alors on enlève une colonne aux coordonnées
        if(mouvement.equals(TypeMouvement.GAUCHE)) coord.colonne-=1;
        //Si le mouvement est droit alors on ajoute une colonne aux coordonnées
        if(mouvement.equals(TypeMouvement.DROITE)) coord.colonne+=1;

        return coord;
    }

    /**
     * Formate l'affichage des coordonnées
     * @return les coordonnées
     */
    public String toString(){
        return this.ligne+","+this.colonne;
    }

    /**
     * Permet de connaitre le mouvement à faire pour aller d'une case à une autre
     * @param destination la case d'arrivée
     * @return le mouvement à faire
     */
    public TypeMouvement getMouvementPourAller(Coordonnee destination){
        TypeMouvement mouvAFaire = null;
        //Si notre colonne- la colonne destination est égale à -1 alors le mouvement est droit
        if((this.colonne-destination.colonne)== -1) mouvAFaire = TypeMouvement.DROITE;
        //Si notre colonne- la colonne destination est égale à 1 alors le mouvement est gauche
        if((this.colonne-destination.colonne)== 1) mouvAFaire = TypeMouvement.GAUCHE;
        //Si notre ligne- la ligne destination est égale à -1 alors le mouvement est haut
        if((this.ligne-destination.ligne)== -1) mouvAFaire = TypeMouvement.BAS;
        //Si notre ligne- la ligne destination est égale à 1 alors le mouvement est bas
        if((this.ligne-destination.ligne)== 1) mouvAFaire = TypeMouvement.HAUT;

        return mouvAFaire;
    }

    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + this.ligne;
        hash = 89 * hash + this.colonne;
        return hash;
    }

    /**
     * Définit si deux coordonnées sont égales
     * @param obj l'objet à comparer
     * @return vrai si les coordonnées sont égales
     */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Coordonnee other = (Coordonnee) obj;
        if (this.ligne != other.ligne) {
            return false;
        }
        if (this.colonne != other.colonne) {
            return false;
        }
        return true;
    }
}
