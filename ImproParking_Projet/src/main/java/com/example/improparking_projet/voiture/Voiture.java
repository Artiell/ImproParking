package com.example.improparking_projet.voiture;

import com.example.improparking_projet.Algorithme.Dijkstra;
import com.example.improparking_projet.Date.Date;
import com.example.improparking_projet.GestionMap.*;
import com.example.improparking_projet.GestionMap.Cases.Case;
import com.example.improparking_projet.communication.*;
import com.example.improparking_projet.MVC.SimulationController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

// Classe qui gère une voiture, un thread
public class Voiture extends Thread{
    private String immatriculation = ""; // Immatriculation du véhicule permettant de l'identifier
    private Date dateArrivee; // Date d'arrivée demandée par le véhicule
    private int dureeStationnement; // Durée de stationnement demandée
    private EtatVoiture etatVoiture; // Etat de la voiture
    private String gareParking = "X"; // Parking dans lequel la voiture est garée
    private boolean running= true; //Boolean permettant d'arrêter le thread
    private final Communication comm; // Communication avec le serveur
    public static SimulationController controller; // Controller de la simulation

    private final int couleur; // Couleur de la voiture           //  0: Gris //  1: Orange  // 2: Vert

    private final Carte carte; // Carte de la simulation
    private Coordonnee coord; // Coordonnées de la voiture
    private TypeMouvement dernierMouvement; // Dernier mouvement effectué par la voiture
    private SpriteVoiture spriteVoiture; // Sprite de la voiture


    /**
     * Constructeur
     * @param carte de la simulation
     * @param coordonnee de la voiture
     */
    public Voiture(Carte carte, Coordonnee coordonnee)
    {
        this.carte = carte;
        this.coord = coordonnee;

        this.immatriculation = creerImmatriculation();
        this.etatVoiture = EtatVoiture.AttenteCom;
        this.comm = new Communication(5000);

        this.couleur = new Random().nextInt(3);
    }


    /**
     * @return l'immatriculation du véhicule
     */
    public String getImmatriculation() {
        return immatriculation;
    }

    /**
     * @return la date d'arrivée du véhicule au parking
     */
    public Date getDateArrivee()
    {
        return dateArrivee;
    }

    /**
     * @return la durée de stationnement du véhicule
     */
    public int getDureeStationnement()
    {
        return dureeStationnement;
    }

    /**
     * @return l'état de la voiture
     */
    public EtatVoiture getEtatVoiture(){return etatVoiture;}

    /**
     * @return le parking dans lequel la voiture est garée
     */
    public String getGareParking(){return gareParking;}

    /**
     * @return les coordonnées de la voiture
     */
    public Coordonnee getCoord() {
        return coord;
    }

    /**
     * @return le dernier mouvement effectué par la voiture
     */
    public TypeMouvement getDernierMouvement() {
        return dernierMouvement;
    }

    /**
     * @return la couleur de la voiture
     */
    public int getCouleur() {
        return couleur;
    }

    /**
     * Définit le sprite de la voiture
     * @param sprite le sprite de la voiture
     */
    public void setSprite(SpriteVoiture sprite) {
        this.spriteVoiture = sprite;
    }

    /**
     * Création de l'immatriculation d'une voiture
     * @return l'immatriculation
     */
    public String creerImmatriculation()
    {
        Random rand = new Random();
        StringBuilder sb = new StringBuilder();

        for(int i=0; i<2; i++){
            char lettre = (char)(rand.nextInt(26) + 'A'); // Choix d'une lettre
            sb.append(lettre); // Concaténation
        }
        sb.append('-');
        for(int i=0; i<3; i++){
            int chiffre = rand.nextInt(10); // Choix d'un chiffre
            sb.append(chiffre); // Concaténation
        }
        sb.append('-');
        for(int i=0; i<2; i++){
            char lettre = (char)(rand.nextInt(26) + 'A'); // Choix d'une lettre
            sb.append(lettre); // Concaténation
        }

        return sb.toString();
    }

    /**
     * Permet de faire une négociation du tarif avec le parking
     * @param emmeteur : l'emmeteur du message
     * @param typeMessage : le type de message
     * @param contenu : le contenu du message
     */
    public void negocierTarif(String emmeteur, TypeMessage typeMessage, String contenu){
        String chaine;
        Message reponse;
        boolean acceptation;

        // Si on reçoit un premier tarif
        if(typeMessage == TypeMessage.PremierTarif){
            acceptation = this.estAccepte(0.3);
            // Si on accepte, on envoie un message d'acceptation
            if(acceptation){
                chaine = this.immatriculation + "|" + emmeteur + "|" + TypeMessage.Acceptation + "|" + contenu;
            }
            // Sinon on renvoie un nouveau prix en contre proposition
            else {
                chaine = this.immatriculation + "|" + emmeteur + "|" + TypeMessage.ContreProposition + "|" + calculerTarif(Double.parseDouble(contenu));
            }
        }
        // Si on reçoit une contre proposition
        else if(typeMessage == TypeMessage.ContreProposition){
            acceptation = this.estAccepte(0.4);
            // Si on accepte, on envoie un message acceptation
            if(acceptation){
                chaine = this.immatriculation + "|" + emmeteur + "|" + TypeMessage.Acceptation + "|" + contenu;
            }
            // Sinon on renvoie un nouveau prix en contre proposition
            else {
                chaine = this.immatriculation + "|" + emmeteur + "|" + TypeMessage.ContreProposition + "|" + calculerTarif(Double.parseDouble(contenu));
            }
        }
        //Si c'est un message de dernière proposition, on accepte ou on refuse
        else if(typeMessage == TypeMessage.DerniereProposition){
            acceptation = this.estAccepte(0.7);
            if(acceptation){
                chaine = this.immatriculation + "|" + emmeteur + "|" + TypeMessage.Acceptation + "|" + contenu;
            }
            else {
                chaine = this.immatriculation + "|" + emmeteur + "|" + TypeMessage.Refus + "|" + contenu;
            }
        }
        //Sinon, on envoie un message d'erreur
        else{
            chaine = immatriculation + "|" + emmeteur + "|" + TypeMessage.MauvaisMessage + "|" + "Je n'ai pas compris";
        }
        //On envoie le message
        reponse = new Message(chaine);
        this.comm.envoyerMessage(reponse);
    }

    /**
     * Permet de choisir aléatoirement si la voiture accepte ou non la proposition du parking
     * @param txAcceptation pour la négociation
     * @return boolean
     */
    public boolean estAccepte(double txAcceptation){
        boolean accepte = false;
        int alea = (int)(Math.random()*10);
        if(alea < 10 * txAcceptation){
            accepte = true;
        }
        return accepte;
    }

    /**
     * Calcul du tarif à proposer dans la négociation
     * @param tarif envoyé par le parking
     * @return le nouveau tarif proposé par la voiture
     */
    public double calculerTarif(double tarif){
        double tarifNegocie;
        Random rand = new Random();
        double txNegociation = rand.nextDouble();
        //1 chance sur 2 de baisser le tarif de 10%
        if(txNegociation < 0.5){
            tarifNegocie = Math.round((tarif * 0.9)*100.0)/100.0;
        }
        //2 chance sur 10 de le baisser le 20%
        else if(txNegociation < 0.8){
            tarifNegocie = Math.round((tarif * 0.8)*100.0)/100.0;
        }
        else{
            tarifNegocie = tarif;
        }
        return tarifNegocie;
    }

    /**
     * Permet de déplacer la voiture
     * @param coordDest : les coordonnées de destination
     */
    public void allerADest(Coordonnee coordDest)
    {
        //On crée un Dijkstra selon la carte
        Dijkstra dijkstra = new Dijkstra(this.carte);
        //On calcule la distance des cases par rapport à la case de la voiture
        dijkstra.calculerDistancesDepuis(this.carte.getCase(this.coord));
        Case caseDest = this.carte.getCase(coordDest);
        //On récupère le chemin à suivre pour aller à destination
        List<TypeMouvement> chemins = dijkstra.getChemin(caseDest);
        etatVoiture = EtatVoiture.Deplacement;
        //On déplace la voiture selon le mouvement à effectuer
        for(TypeMouvement tpm : chemins){
            dernierMouvement = tpm; // On stocke le dernier mouvement effectué
            this.coord = this.coord.getVoisin(tpm);
            try {
                //Pour que l'affichage soit plus fluide et moins instantané on dort 100ms entre chaque déplacement
                sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        etatVoiture = EtatVoiture.Garee;
    }


    /**
     * Méthode qui gère l'envoi des informations lors d'une demande de place
     * @param destinataire parking qui receiver le message
     * @param dateArrivee date demandée par le véhicule
     * @param dureeStationnement durée de stationnement demandée
     */
    public void demandePlace(String destinataire,Date dateArrivee,int dureeStationnement)
    {
        String chaine = immatriculation + "|" + destinataire + "|" + TypeMessage.DemandePlace + "|";
        chaine += dateArrivee.getDateString() + ";" + dureeStationnement;
        Message message = new Message(chaine); // Création d'un message avec les informations données
        this.comm.envoyerMessage(message); // Envoi du message
    }

    /**
     * Méthode qui vérifie si le temps de stationnement est dépassé
     * @return true si le temps est écoulé, false sinon
     */
    public boolean tempsEcoule()
    {
        Date dateActuelle = new Date(); // Date actuelle
        if(dateArrivee !=null) {
            int temps = dateArrivee.convertTempsVersMinute() + dureeStationnement; // Durée de stationnement en minutes
            return temps < dateActuelle.convertTempsVersMinute();
        }
        else {
            return false;
        }
    }

    /**
     * Fonction contenant les éléments à exécuter
     */
    public void run() {
        // Connection au serveur a l'initialisation du module connection
        Message rcvMess = null;
        System.out.println("Lancement du thread de la voiture " + immatriculation);
        etatVoiture = EtatVoiture.EnCommunication;
        //Contient le parking associé à son tarif
        HashMap<String, Double> listeAcceptation = new HashMap<String, Double>();

        try {rcvMess = this.comm.recevoirMessage();}                                catch (IOException e) {throw new RuntimeException(e);}
        if(!(rcvMess.getContenu().equals("Enter your client name :"))){
            while(!(rcvMess.getContenu().equals("Enter your client name :"))){
                try {rcvMess = this.comm.recevoirMessage();}                        catch (IOException e) {throw new RuntimeException(e);}
            }
        }
        //le if précédent est bloquant si le bon message n'est pas reçu
        //identification au serveur de la voiture après avoir reçu le bon message
        this.comm.envoyerMessage(new Message(this.immatriculation+"|"+"server"+"|"+ TypeMessage.IdentificationServeur+"|"+this.immatriculation));

        //création de la demande de place
        this.dateArrivee = new Date(); // Récupération de la date
        Random rand = new Random();
        dureeStationnement = rand.nextInt(5) * 60 + 5 * rand.nextInt(12); // Génération d'une durée de stationnement multiple de 5 minutes
        dureeStationnement += 15; // 15 minutes minimum

        //Demande de la liste des parkings connectés au serveur
        this.comm.envoyerMessage(new Message(this.immatriculation,"Server",TypeMessage.DemandeListeParking,"&"));
        try {rcvMess = this.comm.recevoirMessage();}                                catch (IOException e) {throw new RuntimeException(e);}

        String[] listeParkings = null;
        //Si aucun parking n'est connecté au serveur on arrête le thread de la voiture
        if(rcvMess.getContenu().equals("Aucun parking")){
            running = false;
            etatVoiture = EtatVoiture.StopCom;
            System.out.println(this.immatriculation + '#' + "Aucun parking connecté au serveur");
        }else{
            listeParkings = rcvMess.getContenu().split("/");
        }

        boolean Accepte = false; // TANT QUE LA VOITURE N'EST PAS ACCEPTEE DANS UN PARKING
        boolean toutParcouru = false; // TANT QUE LA LISTE DES PARKINGS N'A PAS ETE PARCOURUE
        boolean discussionVoitureParking = true; // TANT QUE LA VOITURE EST EN NEGOCIATION AVEC UN PARKING
        int parkingParcours = 0; // nb de PARKING EN COURS DE NEGOCIATION
        boolean voitureGaree = false; // TANT QUE LA VOITURE N'EST PAS GAREE

        while(running) // RUNNING SERA MIS A FAUX LORSQUE LA VOITURE AURA ATTEINT LE PARKING POUR SE RANGER
        {
            //Tant que la voiture n'a pas été acceptée dans un parking et que la liste des parkings n'a pas été parcourue
            while(!Accepte && !toutParcouru) {
                try {

                    if(parkingParcours < listeParkings.length){  // si l'indice de parcours de la liste des parkings est inférieur à la taille de la liste des parking

                        this.demandePlace(listeParkings[parkingParcours],dateArrivee ,dureeStationnement); // ENVOI DE LA DEMANDE DE PLACE AU PARKING
                        discussionVoitureParking = true;

                        while(discussionVoitureParking){  // tant qu'on parle avec un parking

                        rcvMess = this.comm.recevoirMessage(); // On attend un message

                            switch (rcvMess.getType()) {
                                // Si le message reçu est un premier tarif, une contre proposition ou une dernière proposition
                                case PremierTarif, ContreProposition, DerniereProposition:
                                    //On envoie les information à la négociation
                                    negocierTarif(rcvMess.getEmmeteur(), rcvMess.getType(), rcvMess.getContenu());
                                break;

                                // Si le message reçu est une acceptation, un refus ou un plus de place
                                case PlusDePlace, Refus, Acceptation:
                                    //On met le tarif final au max
                                    double tarifFinale = Double.MAX_VALUE;
                                    //S'il s'git d'une acceptation on met à jour le tarif final
                                    if(rcvMess.getType()== TypeMessage.Acceptation)
                                        tarifFinale = Double.parseDouble(rcvMess.getContenu());
                                    //On met à jour la liste d'acceptation avec le tarif final
                                    listeAcceptation.put(rcvMess.getEmmeteur(), tarifFinale);
                                    //Si la liste d'acceptation contient tous les parking
                                    if(listeAcceptation.size() == listeParkings.length){
                                        //On met à jour le boolean de parcours
                                        toutParcouru = true;
                                        //On récupère le minimum dans la liste d'acceptation
                                        double parkChoisie = Math.min(listeAcceptation.get("Parking1"),Math.min(listeAcceptation.get("Parking2"),listeAcceptation.get("Parking3")));
                                        //On parcours la liste
                                        for(String park : listeParkings){
                                            //Si le tarif du parking est égal au tarif minimum mais que ce n'est pas un max
                                            if(listeAcceptation.get(park) == parkChoisie && listeAcceptation.get(park) != Double.MAX_VALUE){
                                                //On envoie un message de confirmation au parking avec le tarif
                                                this.comm.envoyerMessage(new Message(this.immatriculation,park,TypeMessage.Confirmation,parkChoisie + ""));
                                                //On met à jour le boolean d'acceptation
                                                Accepte = true;
                                                break;
                                            }
                                        }
                                    }
                                    //On met à jour le boolean de discussion avec le parking
                                    discussionVoitureParking = false;
                                break;

                                default:
                                    System.out.println("Message inconnue :"+ rcvMess);
                                break;
                            }
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                parkingParcours++;
            }
            // Si aucun parking n'a voulu de la voiture mais que celle-ci a parcouru la liste des parkings
            if(!Accepte && toutParcouru){
                System.out.println("Aucun parking n'a accepté la voiture elle va donc attendre 10s et recommencer voir si un parking est disponible" + this.immatriculation);
                try {
                    etatVoiture = EtatVoiture.EnAttente;
                    //On attend 10s
                    Thread.sleep(10000);
                }
                catch (InterruptedException e) {throw new RuntimeException(e);
                }

                toutParcouru = false;
                discussionVoitureParking = true;
                parkingParcours = 0;

                //Si on est accepte dans un parking, que la la voiture à discuter avec tous mais n'est pas encore garée
            }if(Accepte && toutParcouru && !voitureGaree){
                try{
                    //On récupère le message de confirmation
                    rcvMess = this.comm.recevoirMessage();
                    if(rcvMess.getType() == TypeMessage.Confirmation){
                        boolean desistement = new Random().nextInt(500) == 0; // 0.2% de se désister
                        if(desistement) // Si le conducteur se désiste
                        {
                            String chaine = this.immatriculation + "|" + rcvMess.getEmmeteur() + "|" + TypeMessage.Desistement + "|"+ "Finalement, je ne viendrai pas désolé.";
                            this.comm.envoyerMessage(new Message(chaine)); // Envoi du message
                            this.etatVoiture = EtatVoiture.Desistement;
                            //On stop la voiture
                            running= false;
                        }else{
                            //On récupère les coordonnées du parking
                            String[] coordParkString= rcvMess.getContenu().split(";");
                            ArrayList<Coordonnee> coordPark = new ArrayList<Coordonnee>();
                            for(String coord : coordParkString){
                                String[] coordSplit = coord.split(",");
                                coordPark.add(new Coordonnee(Integer.parseInt(coordSplit[0]),Integer.parseInt(coordSplit[1])));
                            }
                            //Pour mettre à jour l'affichage
                            if(rcvMess.getEmmeteur().equals("Parking1")) {
                                this.gareParking = "P1";
                            }
                            else if(rcvMess.getEmmeteur().equals("Parking2")){
                                this.gareParking = "P2";
                            }
                            else {
                                this.gareParking = "P3";
                            }
                            //On selectionne la case de droite
                            this.allerADest(coordPark.get(1));
                            this.dateArrivee = new Date();
                            //On met à jour le boolean pour dire que la voiture est garee
                            voitureGaree = true;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if(voitureGaree){
                //on termine de thread
                running = false;
            }
        }
    }
}

