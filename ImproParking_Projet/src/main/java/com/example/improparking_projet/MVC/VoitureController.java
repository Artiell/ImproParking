package com.example.improparking_projet.MVC;

import com.example.improparking_projet.voiture.Voiture;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class VoitureController implements Initializable {

    @FXML
    public Label dateArrivee;
    public Label dureeStationnement;
    public Label etatVoiture;
    public Label parkingStationnee;
    private Model m;
    private Voiture voiture;

    /**
     * Méthode appelée à l'initialisation de la fenêtre
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        // Exécution du code un peu après l'initialisation pour laisser le model s'initialiser
        Platform.runLater(() -> {
            // Affichage au moment de l'ouverture de la fenêtre des informations liées au parking
            dateArrivee.setText(voiture.getDateArrivee().getDateString()); // Date d'arrivée de la voiture
            dureeStationnement.setText(String.valueOf(voiture.getDureeStationnement())); // Durée de stationnement de la voiture
            etatVoiture.setText(String.valueOf(voiture.getEtatVoiture())); // Etat de la voiture
            parkingStationnee.setText(voiture.getGareParking()); // Parking où la voiture est stationnée
        });
    }

    /**
     * Donner une valeur au model
     * @param model de l'application
     */
    public void setModel(Model model)
    {
        this.m = model;
    }

    /**
     * Donner une valeur à la voiture
     * @param v voiture
     */
    public void setVoiture(Voiture v)
    {
        this.voiture = v;
    }

}
