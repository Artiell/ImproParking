package com.example.improparking_projet.voiture;

/**
 * Enum permettant de gérer les états de la voiture
 */
public enum EtatVoiture {
    //La voiture attend d'etre mis en communication
    AttenteCom,
    //Communication en cours
    EnCommunication,
    //La communication a été coupée à cause d'un problème
    StopCom,
    //La voiture se déplace
    Deplacement,
    //La voiture est garée
    Garee,
    //Quand la voiture attend 20s pour chercher un nouveau parking
    EnAttente,
    //Désistement de la voiture
    Desistement
}
