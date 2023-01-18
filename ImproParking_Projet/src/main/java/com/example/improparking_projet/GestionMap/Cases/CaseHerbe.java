package com.example.improparking_projet.GestionMap.Cases;

import com.example.improparking_projet.GestionMap.Coordonnee;

public class CaseHerbe extends Case{
    public CaseHerbe(Coordonnee coordonnee, int valeur) {
        super(coordonnee, valeur);
    }

    @Override
    public boolean estAccessible() {
        return false;
    }
}
