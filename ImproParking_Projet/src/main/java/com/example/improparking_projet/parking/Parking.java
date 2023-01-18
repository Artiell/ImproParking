package com.example.improparking_projet.parking;

import com.example.improparking_projet.Date.Date;

import com.example.improparking_projet.GestionMap.Carte;
import com.example.improparking_projet.GestionMap.Coordonnee;
import com.example.improparking_projet.communication.Communication;
import com.example.improparking_projet.communication.Message;
import com.example.improparking_projet.communication.TypeMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

// Classe qui gère le parking, un thread
public class Parking extends Thread{

    private final int ID; // Identifiant du parking permettant de le définir
    private double tarif; // Tarif du parking

    private int nbPlaces; // Nombre de places disponibles dans le parking

    private final int nbPlaceMax; // Nombre de places maximales du parking

    private Communication comm;

    private String parkingIdentification; //"Parking suivi de l'id du parking"

    private ArrayList<Coordonnee> coord; // Coordonnées du parking

    private int compteurPropositions = 0; // Compteur de propositions de tarifs

    private double revenu; // Revenu généré par le parking

    /**
     * Constructeur
     * @param id Identifiant du parking
     * @param nb Nombre de places du parking
     * @param coordonnee Coordonnées du parking
     */
    public Parking(int id, int nb, ArrayList<Coordonnee> coordonnee) {
        this.ID = id;
        this.nbPlaces = nb;
        this.nbPlaceMax = nb;
        this.comm = new Communication(5000);
        this.parkingIdentification = "Parking" + this.ID;
        this.coord = coordonnee;
        this.revenu = 0.00;
    }

    /**
     * @return Le nombre de places actuel du parking
     */
    public int getNbPlaces() {return nbPlaces;}

    /**
     * Définit le nombre de places du parking
     * @param nombrePlaces Nombre de places du parking
     */
    public void setNbPlaces(int nombrePlaces) {this.nbPlaces = nombrePlaces;}

    /**
     * @return Le nombre de places maximum du parking
     */
    public int getNbPlaceMax() {return nbPlaceMax;}
    /**
     * @return Le revenu généré par le parking
     */
    public double getRevenu(){return revenu;}

    /**
     * @return Vrai ou faux si le parking est rempli ou non
     */
    public boolean estRempli()
    {
        return (this.nbPlaces == 0);
    }

    /**
     * Permet de faire une négociation du tarif avec la voiture
     * Ne prend pour l'instant pas en compte les tarifs spéciaux
     * @param emmeteur Le parking emmeteur du message
     * @param typeMessage Le type de message
     * @param contenu Le contenu du message
     */
    public void negocierTarif(String emmeteur, TypeMessage typeMessage, String contenu) throws IOException {

        String chaine;
        Message reponse;

        //S'il s'agit d'une demande de place
        if(typeMessage == TypeMessage.DemandePlace){
            //On vérifie si le parking est plein
            if(this.estRempli()){
                //Si oui, on envoie un message de place non disponible
                chaine = this.parkingIdentification + "|" + emmeteur + "|" + TypeMessage.PlusDePlace + "|" + "Désolé, plus de place";
            }else {
                //Si non, on envoie son premier prix
                this.premierTarif(Integer.parseInt(contenu.split(";")[1]), new Date(contenu.split(";")[0]));
                chaine = this.parkingIdentification + "|" + emmeteur + "|" + TypeMessage.PremierTarif + "|" + tarif;
            }
        }
        //S'il s'agit d'une contre-proposition ou d'une dernière proposition
        else if(typeMessage == TypeMessage.ContreProposition){
            double tarifMin = 0.5;
            if(Double.parseDouble(contenu) < tarifMin && !(Double.parseDouble(contenu) ==0.01)){
                //Si le tarif proposé est inférieur au tarif minimum, on envoie un message de refus
                chaine = this.parkingIdentification + "|" + emmeteur + "|" + TypeMessage.Refus + "|" + "Désolé pas possible";
            }
            //On vérifie si le prix proposé n'est pas égal au tarif
            else if(Double.parseDouble(contenu) == this.tarif){
                //si les tarifs sont les mêmes on envoie un message d'acceptation
                String coordContenu = "";
                for(Coordonnee coord : this.coord){
                    coordContenu += coord.toString() + ";";
                }
                //On ajoute le tarif au revenu
                chaine = this.parkingIdentification + "|" + emmeteur + "|" + TypeMessage.Acceptation + "|" + tarif;
                //On pense à bien enlever un place du parking
            }else if(compteurPropositions ==3){
                //On fait une moyenne du prix proposé et du notre pour calculer un nouveau tarif
                tarif = this.calculerTarif(Math.round(((Double.parseDouble(contenu)+ tarif)/2)*100.0)/100.0);
                chaine = this.parkingIdentification + "|" + emmeteur + "|" + TypeMessage.DerniereProposition + "|" + tarif;
            }else{
                //Si le tarif proposé est supérieur au tarif, on envoie une contre-proposition
                tarif = this.calculerTarif((Double.parseDouble(contenu)+ tarif)/2);
                chaine = this.parkingIdentification + "|" + emmeteur + "|" + TypeMessage.ContreProposition + "|" + tarif;
                compteurPropositions++;
            }
        }
        else{
            chaine = this.parkingIdentification + "|" + emmeteur + "|" + TypeMessage.MauvaisMessage + "|" + "J'ai pas compris";
        }
        //Envoie du message
        reponse = new Message(chaine);
        this.comm.envoyerMessage(reponse);
    }

    public void premierTarif(int dureeStationnement, Date heurearrive){
        double tarif = 0.0;

        //On fixe le prix selon le temps de stationnement
        if(dureeStationnement <= 30)
            tarif = 1.5;
        else if(dureeStationnement <= 60)
            tarif = 3.0;
        else if(dureeStationnement <= 120)
            tarif = 4.5;
        else
            tarif = 7.0;

        //On le fait évoluer selon l'heure de la journée
        //Gratuit entre 1 et 6h
        if(heurearrive.getHeure() >= 1 && heurearrive.getHeure() <= 6)
            tarif = 0.01;
        //Tarif augmenter de 50% entre 12 et 17
        else if(heurearrive.getHeure() >= 12 && heurearrive.getHeure() <= 17)
            tarif = Math.round((tarif * 1.5)*100.0)/100.0;
        //Tarif normal sinon
        this.tarif = tarif;
    }
    /**
     * Permet de calculer le tarif en fonction du tarif proposé
     * @param tarif Tarif proposé par la voiture
     * @return le tarif calculé
     */
    public double calculerTarif(double tarif){
        double tarifNegocie;
        Random rand = new Random();
        double txNegociation = rand.nextDouble();
        //1 chance sur 2 de baisser le tarif de 5%
        if(txNegociation < 0.5){
            tarifNegocie = Math.round((tarif * 0.95)*100.0)/100.0;
        }
        //1 chance sur 10 de le baisser de 15%
        else if(txNegociation < 0.9){
            tarifNegocie = Math.round((tarif * 0.85)*100.0)/100.0;
        }
        //Sinon on renvoie le meme tarif
        else{
            tarifNegocie = tarif;
        }
        return tarifNegocie;
    }

    /**
     * Fonction contenant les éléments à éxécuter
     */
    public void run()
    {
        Message rcvMess = null;
        System.out.println("Lancement du thread du "+ this.parkingIdentification);
        //connexion au serveur réalisé à l'initialisation du module comm du parking

        try {rcvMess = this.comm.recevoirMessage();}                     catch (IOException e) {throw new RuntimeException(e);}
        if(!(rcvMess.getContenu().equals("Enter your client name :"))){
            while(!(rcvMess.getContenu().equals("Enter your client name :"))){
                try {rcvMess = this.comm.recevoirMessage();}                     catch (IOException e) {throw new RuntimeException(e);}
            }
        }
        //le if précédent est bloquant si le bon message n'est pas reçu
        //identification au serveur du parking après avoir reçu le bon message
        this.comm.envoyerMessage(new Message(this.parkingIdentification+"|"+"server"+"|"+ TypeMessage.IdentificationServeur+"|"+this.parkingIdentification));

        while(true)
        {
            //Liste contenant les heures de départ prévue par les voitures
            ArrayList<Integer> departVoiture = new ArrayList<>();
            //Tant que le parking n'a pas fait un maximum de 3 contre-propositions
            while(compteurPropositions <= 3)
            {
                try {
                    rcvMess = this.comm.recevoirMessage();
                    switch (rcvMess.getType())
                    {
                        //Dans le cas d'une demande de place
                        case DemandePlace :
                            //On remet les int à leurs valeur de départ
                            compteurPropositions = 0;
                            tarif=5;
                            //On lance la négociation
                            negocierTarif(rcvMess.getEmmeteur(), rcvMess.getType(), rcvMess.getContenu());
                        break;
                        //Dans le cas d'une contre-proposition
                        case ContreProposition:
                            //On ajoute une proposition
                            compteurPropositions++;
                            negocierTarif(rcvMess.getEmmeteur(), rcvMess.getType(), rcvMess.getContenu());
                        break;
                        //Dans le cas d'une acceptation
                        case Acceptation:
                            //On renvoie une acceptation à la voiture
                            this.comm.envoyerMessage(new Message(this.parkingIdentification, rcvMess.getEmmeteur(), TypeMessage.Acceptation, rcvMess.getContenu()));
                        break;
                        //Dans le cas d'une confirmation
                        case Confirmation:
                            //On ajoute le prix d'acceptation au revenu du parking
                            this.revenu = Math.round((this.revenu + Double.parseDouble(rcvMess.getContenu()))*100.0)/100.0;
                            //On envoie les coordonnées du parking
                            String coordContenu = "";
                            for(Coordonnee coord : this.coord){
                                coordContenu += coord.toString() + ";";
                            }
                            this.comm.envoyerMessage(new Message(this.parkingIdentification, rcvMess.getEmmeteur(), TypeMessage.Confirmation, coordContenu));
                            //On enlève une place
                            nbPlaces--;
                        break;
                        //Dans le cas d'un refus de la part de la voiture
                        case Refus:
                            //On confirme ce refus
                            this.comm.envoyerMessage(new Message(this.parkingIdentification+"|"+rcvMess.getEmmeteur()+"|"+ TypeMessage.Refus+"|"+"Bah viens pas alors"));
                            //On remet le compteur de proposition à 0 et le tarif à 5
                            compteurPropositions=0;
                            tarif=5;
                        break;
                        //Dans le cas d'un désistement de la part de la voiture
                        case Desistement:
                            //On remet une place de disponible et le compteur et tarif à sa valeur de base
                            nbPlaces++;
                            compteurPropositions=0;
                            tarif=5;
                        break;
                        //Dans le cas d'un message inconnu
                        case MauvaisMessage:
                            this.comm.envoyerMessage(new Message(this.parkingIdentification+"|"+rcvMess.getEmmeteur()+"|"+ TypeMessage.MauvaisMessage+"|"+"J'ai pas compris"));
                        break;
                        default:
                            System.out.println("Message inconnu : "+ rcvMess);
                        break;
                    }

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}