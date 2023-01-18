package com.example.improparking_projet.MVC;

import com.example.improparking_projet.GestionMap.Carte;
import com.example.improparking_projet.GestionMap.Coordonnee;
import com.example.improparking_projet.voiture.EtatVoiture;
import com.example.improparking_projet.voiture.Voiture;
import com.example.improparking_projet.parking.Parking;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Model extends Observable {

    private final ObservableList<Voiture> voitures = FXCollections.observableArrayList();
    private final ObservableList<Parking> parkings = FXCollections.observableArrayList();
    private Carte carte;

    private final ArrayList<Coordonnee> spawnVoiture = new ArrayList<>();

    public Model() {
        super();
    }

    /**
     * Méthode qui permet de mettre à jour la vue
     */
    public void updateView() {
        setChanged();
        notifyObservers();
    }

    public void StartTheModel() {

        System.out.println("Model started");
        carte = new Carte("/map/mapCity.txt");

        spawnVoiture.add(new Coordonnee(4, 1));
        spawnVoiture.add(new Coordonnee(1, 38));
        spawnVoiture.add(new Coordonnee(17, 51));

        /////////////////////// CREATION DES 3 PARKINGS ///////////////////////
        ArrayList<Coordonnee> coordonneeP = new ArrayList<>();

        coordonneeP.add(new Coordonnee(27,7));
        coordonneeP.add(new Coordonnee(27,6));
        Parking Park = new Parking(1, 10,coordonneeP);
        Park.start();
        System.out.println("Parking 1 started");
        parkings.add(Park);

        ArrayList<Coordonnee> coordonneeP2 = new ArrayList<>();
        coordonneeP2.add(new Coordonnee(25,43));
        coordonneeP2.add(new Coordonnee(26,43));
        Parking Park2 = new Parking(2, 50, coordonneeP2);
        Park2.start();
        System.out.println("Parking 2 started");
        parkings.add(Park2);

        ArrayList<Coordonnee> coordonneeP3 = new ArrayList<>();
        coordonneeP3.add(new Coordonnee(8,38));
        coordonneeP3.add(new Coordonnee(8,39));
        Parking Park3 = new Parking(3, 100, coordonneeP3);
        Park3.start();
        System.out.println("Parking 3 started");
        parkings.add(Park3);

        // Définition d'un runnable qui permet de gérer le stationnement des voitures
        Runnable repetition = this::gestionTempsStationnement;
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(repetition, 0,1000 , TimeUnit.MILLISECONDS); // On répète la méthode toutes les secondes

        // Définition d'un runnable qui permet de rafraichir la vue automatiquement
        Runnable rafraichirVue = this::updateView;
        ScheduledExecutorService executor2 = Executors.newScheduledThreadPool(1);
        executor2.scheduleAtFixedRate(rafraichirVue,0,200,TimeUnit.MILLISECONDS); // On répète la méthode toutes les 0.2 seconde

        // Définition d'un runnable qui permet la génération automatique une voiture
        Runnable creationVoiture = this::creationVoiture;
        ScheduledExecutorService executor3 = Executors.newScheduledThreadPool(1);
        executor3.scheduleAtFixedRate(creationVoiture, 2000, 7000, TimeUnit.MILLISECONDS); // On répète la méthode toutes les 7secondes
    }

    /**
     * @return la liste de voitures
     */
    public ObservableList<Voiture> getVoitures() {
        return voitures;
    }

    /**
     * @return la liste de parkings
     */
    public ObservableList<Parking> getParkings() {
        return parkings;
    }

    /**
     * Crée une voiture dans le modèle
     */
    public Voiture creationVoiture() {
        Voiture v = new Voiture(this.carte,this.spawnVoiture.get(new Random().nextInt(this.spawnVoiture.size())));
        v.start();
        voitures.add(v);

        return v;
    }

    /**
     * Méthode permettant de gérer le stationnement des voitures en les supprimant lorsqu'elles ont terminé
     */
    public void gestionTempsStationnement(){
        Platform.runLater(() -> {
            // Si le temps de stationnement est dépassé, on supprime la voiture du model
            boolean tempsDepasse;
            //On fait un iterateur de voiture
            for(Iterator<Voiture> i = voitures.iterator(); i.hasNext();) {
                Voiture v = i.next();
                tempsDepasse = v.tempsEcoule(); // On vérifie si le temps de stationnement est dépassé
                if (tempsDepasse) {
                    System.out.println("La voiture " + v.getImmatriculation() + " a dépassé son temps de stationnement");
                    // On la supprime du parking sur lequel elle était stationnée
                    if (Objects.equals(v.getGareParking(), "P1")) {
                        parkings.get(0).setNbPlaces(parkings.get(0).getNbPlaces() + 1);
                    } else if (Objects.equals(v.getGareParking(), "P2")) {
                        parkings.get(1).setNbPlaces(parkings.get(1).getNbPlaces() + 1);
                    } else if (Objects.equals(v.getGareParking(), "P3")) {
                        parkings.get(2).setNbPlaces(parkings.get(2).getNbPlaces() + 1);
                    }
                    i.remove(); // On la supprime du model
                    break; // On sort de la boucle
                    //sinon si une voiture s'est désistée, on l'enlève aussi
                } else if (v.getEtatVoiture() == EtatVoiture.Desistement)
                    i.remove();
            }
        });
    }

    public Carte getCarte() {
        return carte;
    }
}