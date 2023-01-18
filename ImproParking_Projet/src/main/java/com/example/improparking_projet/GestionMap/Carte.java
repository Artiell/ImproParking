package com.example.improparking_projet.GestionMap;

import com.example.improparking_projet.GestionMap.Cases.Case;
import com.example.improparking_projet.GestionMap.Cases.FabriqueCase;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;

/*
* Classe qui permet de gérer la carte
*/
public class Carte {
    private HashMap<Coordonnee, Case> cases;

    private int longueur;
    private int hauteur;

    /**
     * Constructeur de la classe Carte
     * @param chemin Chemin du fichier contenant la map
     */
    public Carte(String chemin){
        this.cases= new HashMap<>();
        longueur= 55;
        hauteur= 35;
        try {
            // On récupère le fichier et charge la carte
            InputStream is = getClass().getResourceAsStream(chemin);
            BufferedReader br = new BufferedReader(new InputStreamReader(Objects.requireNonNull(is)));

            // On lit le fichier ligne par ligne pour ajouter les coordonnées et les cases
            for(int i=0; i<hauteur; i++){
                String line = br.readLine(); // Lecture de la ligne
                String[] pixel = line.split(" "); // On sépare les pixels
                for(int j=0; j<longueur; j++){
                    //On crée la coordonnée
                    Coordonnee coordonneeActu = new Coordonnee(i,j);
                    //On ajoute la case
                    this.ajouterCase(coordonneeActu, Integer.parseInt(pixel[j]));
                }
            }

            //Ajout des voisins
            for(int i=0; i<this.hauteur; i++) {
                for (int j = 0; j < this.longueur; j++) {
                    Coordonnee cooCase = new Coordonnee(i, j);
                    for (TypeMouvement mouvement : TypeMouvement.values()) {
                        Coordonnee cooVoisin = cooCase.getVoisin(mouvement);
                        if (this.cases.get(cooVoisin) != null) {
                            this.cases.get(cooCase).ajouterVoisin(this.cases.get(cooVoisin));
                        }
                    }
                }
            }
            br.close(); // Fermeture du buffer
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Permet d'ajouter une case à la carte
     * @param coordonnee Coordonnée de la case
     * @param valeur Valeur de la case
     */
    private void ajouterCase(Coordonnee coordonnee, int valeur){
        cases.put(coordonnee, FabriqueCase.creer(coordonnee, valeur));
    }

    /**
     * Permet de récupérer une case à partir de ses coordonnées
     * @param coordonnee Coordonnée de la case
     * @return La case
     */
    public Case getCase(Coordonnee coordonnee){
        return cases.get(coordonnee);
    }

    /**
     * Permet de récupérer toutes les cases de la carte
     * @return Collection de toutes les cases
     */
    public Collection<Case> getCases(){
        return cases.values();
    }

    /**
     * Permet de récupérer la taille de la carte
     * @return La taille de la carte
     */
    public int getTaille(){
        return this.hauteur*this.longueur;
    }

    /**
     * Permet de récupérer la longueur de la carte
     * @return La longueur de la carte
     */
    public int getLongueur(){return this.longueur;}
    /**
     * Permet de récupérer la hauteur de la carte
     * @return La hauteur de la carte
     */
    public int getHauteur(){return this.hauteur;}

}
