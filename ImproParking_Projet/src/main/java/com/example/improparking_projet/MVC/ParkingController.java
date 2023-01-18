package com.example.improparking_projet.MVC;

import com.example.improparking_projet.parking.Parking;
import com.example.improparking_projet.voiture.Voiture;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

public class ParkingController implements Initializable {

    @FXML
    public Label nbPlacesTotal;
    public Label nbPlacesLibres;
    public Label revenu;
    public TableView<Voiture> tableViewVoitures;
    public TableColumn<Voiture,String> tableColumnImmatriculation;
    public TableColumn<Voiture,String> tableColumnEtat;
    public TableColumn<Voiture,String> tableColumnParking;
    private Model m;
    private int numero;

    /**
     * Méthode appelée à l'initialisation de la fenêtre
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        // Exécution du code un peu après l'initialisation pour laisser le model s'initialiser
        Platform.runLater(() -> {
            // Affichage au moment de l'ouverture de la fenêtre des informations liées au parking
            nbPlacesTotal.setText(String.valueOf(m.getParkings().get(numero-1).getNbPlaceMax())); // Nombre de places total
            nbPlacesLibres.setText(String.valueOf(m.getParkings().get(numero-1).getNbPlaces())); // Nombre de places libres
            revenu.setText(String.format("%.2f",(m.getParkings().get(numero-1).getRevenu()))); // Revenu généré par le parking

            //initialisation de la table
            tableColumnImmatriculation.setCellValueFactory(new PropertyValueFactory<Voiture,String>("immatriculation"));
            tableColumnEtat.setCellValueFactory(new PropertyValueFactory<Voiture,String>("etatVoiture"));
            tableColumnParking.setCellValueFactory(new PropertyValueFactory<Voiture,String>("gareParking"));
            // Ajout des voitures dans le tableau
            ObservableList<Voiture> voituresParking = FXCollections.observableArrayList();
            for(Voiture v : m.getVoitures())
            {
                if(v.getGareParking().equals("P"+(numero)))
                {
                    voituresParking.add(v);
                }
            }
            tableViewVoitures.setItems(voituresParking);
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

    public void setNumeroParking(int numeroParking)
    {
        this.numero = numeroParking;
    }

    public void infosVoiture(MouseEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("/fxml/Voiture.fxml"));
        Parent root = fxmlLoader.load();
        VoitureController voitureController = fxmlLoader.getController();
        voitureController.setModel(m);
        // Récupération de la voiture sélectionnée dans la table et envoi de l'objet voiture au controller Voiture
        Voiture v = tableViewVoitures.getSelectionModel().getSelectedItem();
        for ( Voiture voiture : m.getVoitures()) {
            if (Objects.equals(voiture.getImmatriculation(), v.getImmatriculation()))
            {
                voitureController.setVoiture(voiture);
            }
        }

        Scene scene = new Scene(root, 640, 360);
        Stage stageVoiture = new Stage();
        stageVoiture.setScene(scene);
        //Application de css sur la scène
        scene.getStylesheets().add(getClass().getResource("/css/Voiture.css").toExternalForm());

        // Écriture du titre de la fenêtre en fonction de l'immatriculation de la voiture
        String immatriculation = tableViewVoitures.getSelectionModel().getSelectedItem().getImmatriculation();
        stageVoiture.setTitle("Informations sur la voiture " + immatriculation);
        // L'application est concentrée sur cette fenêtre
        stageVoiture.initModality(Modality.APPLICATION_MODAL);
        stageVoiture.initOwner(((Node) event.getSource()).getScene().getWindow());
        stageVoiture.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/LogoParking.png"))));
        stageVoiture.setResizable(false);
        stageVoiture.show();
    }
}
