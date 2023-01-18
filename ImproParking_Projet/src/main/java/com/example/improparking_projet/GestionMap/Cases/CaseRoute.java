package com.example.improparking_projet.GestionMap.Cases;

import com.example.improparking_projet.GestionMap.Coordonnee;

public class CaseRoute extends Case{
    public CaseRoute(Coordonnee coordonnee, int valeur) {
        super(coordonnee, valeur);
    }

    @Override
    public boolean estAccessible() {
        return true;
    }
}
