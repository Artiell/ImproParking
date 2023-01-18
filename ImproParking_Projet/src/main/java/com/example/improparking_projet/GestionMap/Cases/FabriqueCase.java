package com.example.improparking_projet.GestionMap.Cases;

import com.example.improparking_projet.GestionMap.Coordonnee;

/**
 * Classe permettant de creer les cases
 */
public class FabriqueCase {
    /**
     * Methode permettant de creer une case
     * @param coordonnee Coordonnee de la case
     * @param valeur Valeur de la case
     * @return Case cree
     */
    public static Case creer(Coordonnee coordonnee, int valeur){
        Case caseAConstruire = null;
        switch (valeur){
            case 100, 101, 102 ,103, 133 :
                caseAConstruire = new CaseRoute(coordonnee, valeur);
                break;
            case 130, 131:
                caseAConstruire = new CaseHerbe(coordonnee, valeur);
                break;
            case 140:
                caseAConstruire = new CaseDepart(coordonnee, valeur);
                break;
            case 134 :
                caseAConstruire = new CaseParking(coordonnee, valeur);
                break;
            case 110, 111, 112, 113, 120,121, 122, 123:
                caseAConstruire = new CaseRouteInacessible(coordonnee, valeur);
                break;
        }
        return caseAConstruire;
    }
}
