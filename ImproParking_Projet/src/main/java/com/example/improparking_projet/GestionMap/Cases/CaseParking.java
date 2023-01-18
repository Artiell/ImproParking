package com.example.improparking_projet.GestionMap.Cases;

import com.example.improparking_projet.GestionMap.Coordonnee;

public class CaseParking extends Case{
    public CaseParking(Coordonnee coordonnee, int valeur) {
        super(coordonnee, valeur);
    }

    @Override
    public boolean estAccessible() {
        return true;
    }
}
