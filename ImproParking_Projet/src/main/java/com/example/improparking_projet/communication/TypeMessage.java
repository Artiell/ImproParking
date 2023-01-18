package com.example.improparking_projet.communication;

/*
 * Contient les différents types de messages possibles
 */
public enum TypeMessage {
    //Pour demander une place au parking
    DemandePlace,
    //Pour envoyer un premier tarif
    PremierTarif,
    //Pour envoyer une contre proposition au tarif proposé
    ContreProposition,
    //Pour envoyer un tarif final
    DerniereProposition,
    //Pour envoyer un message de confirmation
    Acceptation,
    //Pour envoyer un message de refus
    Refus,
    //Pour notifier la voiture que le parking est complet
    PlusDePlace,
    //Pour dire au parking que la voiture se désiste de sa réservation
    Desistement,
    //Si le message reçu ne correspond à rien
    MauvaisMessage,
    //Pour s'identifier au près du serveur
    IdentificationServeur,
    //Type message utilisé pour obtenir une liste de parking
    DemandeListeParking,
    //Type message utilisé pour obtenir les coordonnées d'un parking
    DemandeCoordPark,
    //Confirmation de la venu de la voite
    Confirmation

}
