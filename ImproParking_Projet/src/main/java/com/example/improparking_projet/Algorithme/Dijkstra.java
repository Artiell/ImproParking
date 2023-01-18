package com.example.improparking_projet.Algorithme;

import com.example.improparking_projet.GestionMap.Carte;
import com.example.improparking_projet.GestionMap.Cases.Case;
import com.example.improparking_projet.GestionMap.TypeMouvement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Dijkstra extends AlgoCalculDistance{
    private HashMap<Case, Boolean> estVisite;
    private HashMap<Case, Case> predecesseur;
    private int infini;

    /**
     * Constructeur de la class
     * @param carte correspond à la carte de l'application
     */
    public Dijkstra(Carte carte) {
        super(carte);
        estVisite = new HashMap<>();
        predecesseur= new HashMap<>();
        infini = 1+16*getCarte().getTaille()*getCarte().getTaille();
    }

    /**
     * Méthode permettant de connaitre le nombre d'actions nécessaire pour se rendre sur
     * la case ”destination” depuis une case voisine
     * @param destination correspond à une case
     * @return le cout
     */
    public int coutMouvementVers(Case destination) {
        int cout = this.infini;
        if(destination.estAccessible()){
            cout=1;
        }
        return cout;
    }

    /**
     * Méthode correspondant à l'initialisation de Dijkstra
     * @param depart correspond à une case
     */
    public void initialisation(Case depart){
        for(Case v: this.getCarte().getCases()){
            super.setDistance(v, infini);
            this.estVisite.put(v, false);
            this.predecesseur.put(v, null);
        }
        super.setDistance(depart, 0);
    }

    /**
     * Méthode correspondant au relachement de Dijkstra
     * @param a correspond à une case
     * @param b correspond à une case
     */
    public void relachement(Case a, Case b){
        if(super.getDistance(b)> super.getDistance(a)+ coutMouvementVers(b) ){
            super.setDistance(b, super.getDistance(a)+ coutMouvementVers(b));
            this.predecesseur.put(b, a);
        }
    }

    /**
     * Méthode permettant d'avoir la case la plus proche
     * @return la case la plus proche
     */
    public Case getCaseLaPlusProche(){
        int distanceMin= this.infini;
        Case res= null;
        //On regarde toutes les cases de la carte
        for(Case c: this.getCarte().getCases()){
            //Si la case n'est pas visitée et que la distance jusqu'à la case est plus petite que la distance mini
            if(!this.estVisite.get(c) && super.getDistance(c)<distanceMin){
                distanceMin= super.getDistance(c);      //On donne à distanceMin la distance jusqu'à la case
                res= c;
            }
        }
        return res;
    }

    /**
     * Méthode permettant de calculer la distance depuis une case
     * @param depart case de départ
     */
    @Override
    public void calculerDistancesDepuis(Case depart) {
        initialisation(depart);
        Case caseLaPlusProche = this.getCaseLaPlusProche();
        //Tant qu'il y a une case plus proche
        while(caseLaPlusProche!=null){
            //On visite la case
            this.estVisite.put(caseLaPlusProche, true);
            //On effectue un relachement entre caseLaPlusProche et tous ses voisins
            for(int i=0; i<caseLaPlusProche.getVoisins().size(); i++)
                this.relachement(caseLaPlusProche, caseLaPlusProche.getVoisins().get(i));
            caseLaPlusProche = getCaseLaPlusProche();
        }
    }

    /**
     * Méthode permettant de calculer le chemin le plus court pour aller à une case
     * @param arrivee case d'arrivée
     * @return la liste de mouvements à effectuer
     */
    @Override
    public ArrayList<TypeMouvement> getChemin(Case arrivee) {
        ArrayList<TypeMouvement> res = new ArrayList<>();
        Case caseEnCours = arrivee;

        //Tant que caseEnCours à des predecesseurs
        while(this.predecesseur.get(caseEnCours)!=null){
            res.add(this.predecesseur.get(caseEnCours).getMouvementPourAller(caseEnCours));
            caseEnCours = this.predecesseur.get(caseEnCours);
        }
        Collections.reverse(res);

        //On vide les hashmaps pour un peu d'optimisation
        this.predecesseur.clear();
        this.estVisite.clear();
        super.reinitialisationDistances();

        return res;
    }
}
