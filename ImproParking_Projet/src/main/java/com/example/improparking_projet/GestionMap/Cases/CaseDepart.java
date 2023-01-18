package com.example.improparking_projet.GestionMap.Cases;

import com.example.improparking_projet.GestionMap.Coordonnee;

public class CaseDepart extends Case{
    public CaseDepart(Coordonnee coordonnee, int valeur) {
        super(coordonnee, valeur);
    }

    @Override
    public boolean estAccessible() {
        return false;
    }
}
