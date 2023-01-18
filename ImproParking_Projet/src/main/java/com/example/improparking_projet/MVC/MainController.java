package com.example.improparking_projet.MVC;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class MainController {

    @FXML
    private Button boutonDemarrage;

    /**
     * Méthode qui s'éxecute à l'ouverture de la fenêtre
     */
    @FXML
    private void ouvertureSimulation() throws IOException {

        //On va chercher le fichier fxml
        FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/fxml/Simulation.fxml")));

        //On crée la scene avec le fichier fxml
        Scene scene = new Scene(fxmlLoader.load(), 1200,560);
        Stage stage = new Stage();

        //Application de css sur la scène
        scene.getStylesheets().add(getClass().getResource("/css/Simulation.css").toExternalForm());

        //On récupère le stage de la fenêtre courante par le bouton
        Stage actualStage = (Stage)(boutonDemarrage.getScene().getWindow());
        actualStage.close();


        stage.setTitle("Parking Simulator 2023");
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/LogoParking.png"))));
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
}