package com.example.improparking_projet.MVC;

import com.example.improparking_projet.Date.Date;
import com.example.improparking_projet.GestionMap.Coordonnee;
import com.example.improparking_projet.GestionMap.VoitureManager;
import com.example.improparking_projet.voiture.EtatVoiture;
import com.example.improparking_projet.voiture.Voiture;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SimulationController implements Initializable, Observer {

    @FXML
    public TableView<Voiture> tableViewVoitures;
    public TableColumn<Voiture,String> tableColumnImmatriculation;
    public TableColumn<Voiture,String> tableColumnEtat;
    public TableColumn<Voiture,String> tableColumnParking;
    public Label parking1, parking2, parking3;
    public Button boutonParking1, boutonParking2, boutonParking3;
    public Label affichageDate;
    @FXML
    private Canvas canvasMap;
    @FXML
    private Canvas canvasVoiture;
    @FXML
    private GraphicsContext gcMap;
    private GraphicsContext gcVoiture;
    @FXML
    public Label menu;
    private VoitureManager voitureManager = new VoitureManager();
    private Model m;

    /**
     * Méthode qui s'éxecute à l'ouverture de la fenêtre
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Voiture.controller = this;
        gcMap = canvasMap.getGraphicsContext2D();
        gcVoiture = canvasVoiture.getGraphicsContext2D();

        //initialisation et lancement du model
        m = new Model();
        m.addObserver(this);
        m.StartTheModel();
        this.dessinerMap(gcMap);

        //initialisation de la table
        tableColumnImmatriculation.setCellValueFactory(new PropertyValueFactory<Voiture,String>("immatriculation"));
        tableColumnEtat.setCellValueFactory(new PropertyValueFactory<Voiture,String>("etatVoiture"));
        tableColumnParking.setCellValueFactory(new PropertyValueFactory<Voiture,String>("gareParking"));
        // Ajout des voitures dans le tableau
        tableViewVoitures.setItems(m.getVoitures());

        for (Voiture v : m.getVoitures()) {
            voitureManager.dessinerSpawnVoiture(gcVoiture,v); // Dessin de la voiture sur la map
            tableViewVoitures.setItems(m.getVoitures()); // On récupère les voitures du modèle pour les afficher dans le tableau
        }


        // Affichage des parkings
        parking1.setText("Parking 1         " + m.getParkings().get(0).getNbPlaces()+ " / " + m.getParkings().get(0).getNbPlaceMax() + " places");
        if(m.getParkings().size() == 1){ // Si il n'y a qu'un seul parking
            parking2.setVisible(false);
            parking3.setVisible(false);
            boutonParking2.setVisible(false);
            boutonParking3.setVisible(false);
        }
        else if(m.getParkings().size() == 2){ // Si il y a deux parkings
            parking2.setText("Parking 2        " + m.getParkings().get(1).getNbPlaces()+ " / " + m.getParkings().get(1).getNbPlaceMax() + " places");            parking3.setVisible(false);
            parking3.setVisible(false);
            boutonParking3.setVisible(false);
        }
        else{ // Si il y a trois parkings
            parking2.setText("Parking 2          " + m.getParkings().get(1).getNbPlaces()+ " / " + m.getParkings().get(1).getNbPlaceMax() + " places");
            parking3.setText("Parking 3          " + m.getParkings().get(2).getNbPlaces()+ " / " + m.getParkings().get(2).getNbPlaceMax() + " places");
        }

        // Création d'un runnable qui permet de gérer le déplacement des voitures
        Runnable deplacementVoiture = this::deplacerVoiture;
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(deplacementVoiture,0,100, TimeUnit.MILLISECONDS); // On répète la méthode toutes les 0.1 seconde

        // Création d'un runnable qui permet de gérer l'affichage de la date
        Runnable affichageDate = this::afficherDate;
        ScheduledExecutorService executor2 = Executors.newScheduledThreadPool(1);
        executor2.scheduleAtFixedRate(affichageDate,0,1, TimeUnit.SECONDS); // On répète la méthode toutes les secondes
    }

    /**
     * Update de la vue par rapport au modèle
     * @param o  Le modèle observateur
     * @param arg an argument passed to the {@code notifyObservers method.
     */
    @Override
    public void update(Observable o, Object arg) {
        rafraichirVue();
    }

    /**
     * Méthode s'occupant de la création d'une voiture
     */
    @FXML
    protected void creationVoiture() {
        Voiture voitureCree = m.creationVoiture(); // Creation de la voiture dans le modèle
        voitureManager.dessinerSpawnVoiture(gcVoiture,voitureCree); // Dessin de la voiture sur la map
        tableViewVoitures.setItems(m.getVoitures()); // On récupère les voitures du modèle pour les afficher dans le tableau
    }

    /**
     * Méthode qui permet de déplacer une voiture grâce au voitureManager
     */
    public void deplacerVoiture(){
        Platform.runLater(() -> {
            voitureManager.cleanMap(gcVoiture,canvasVoiture); // On nettoie la map
            for(Voiture v : m.getVoitures()){ // Pour chaque voiture
                if(v.getEtatVoiture() != EtatVoiture.Garee && v.getEtatVoiture() != EtatVoiture.EnCommunication){ // Si la voiture n'est pas garée ou qu'elle n'est pas en train de communiquer
                    voitureManager.deplacerVoiture(gcVoiture,v); // On déplace la voiture
                }
            }
        });
    }

    /**
     * Méthode qui permet d'afficher la date
     */
    public void afficherDate(){
        Platform.runLater(() -> {
            Date date = new Date();
            affichageDate.setText(date.getDateString()); // On affiche la date
        });
    };

    /**
     * Ouverture d'une fenêtre qui affiche les infos du parking
     * @param event Bouton cliqué
     */
    @FXML
    public void infosParking(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("/fxml/Parking.fxml"));
        Parent root = fxmlLoader.load();
        ParkingController parkingController = fxmlLoader.getController();
        parkingController.setModel(m);

        Scene scene = new Scene(root, 400, 500);
        Stage stageParking = new Stage();
        stageParking.setScene(scene);

        Button boutonClique = (Button) event.getSource();
        if(boutonClique.getId().equals("boutonParking1")) {
            stageParking.setTitle("Informations sur le parking 1");
            parkingController.setNumeroParking(1);
        }
        else if(boutonClique.getId().equals("boutonParking2")){
            stageParking.setTitle("Informations sur le parking 2");
            parkingController.setNumeroParking(2);
        }
        else if(boutonClique.getId().equals("boutonParking3")){
            stageParking.setTitle("Informations sur le parking 3");
            parkingController.setNumeroParking(3);
        }

        //Application de css sur la scène
        scene.getStylesheets().add(getClass().getResource("/css/Parking.css").toExternalForm());

        // L'application est concentrée sur cette fenêtre
        stageParking.initModality(Modality.APPLICATION_MODAL);
        stageParking.initOwner(((Node) event.getSource()).getScene().getWindow());
        stageParking.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/LogoParking.png"))));
        stageParking.setResizable(false);
        stageParking.show();
    }

    /**
     * Ouverture d'une fenêtre affichant les informations d'une voiture
     */
    public void infosVoiture(MouseEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("/fxml/Voiture.fxml"));
        Parent root = fxmlLoader.load();
        VoitureController voitureController = fxmlLoader.getController();
        voitureController.setModel(m);
        // On récupère la voiture sélectionnée dans le tableau et on l'envoie au VoitureController
        Voiture v = tableViewVoitures.getSelectionModel().getSelectedItem();
        voitureController.setVoiture(v);

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

    /**
     * Méthode qui s'occupe du rafrachissement de la vue
     */
    public void rafraichirVue(){
        Platform.runLater(() -> {
            parking1.setText("Parking 1         " + m.getParkings().get(0).getNbPlaces()+ " / " + m.getParkings().get(0).getNbPlaceMax() + " places");
            if(m.getParkings().size() == 2){ // S'il y a deux parkings
                parking2.setText("Parking 2        " + m.getParkings().get(1).getNbPlaces()+ " / " + m.getParkings().get(1).getNbPlaceMax() + " places");
            }
            else if(m.getParkings().size() == 3){ // S'il y a trois parkings
                parking2.setText("Parking 2          " + m.getParkings().get(1).getNbPlaces()+ " / " + m.getParkings().get(1).getNbPlaceMax() + " places");
                parking3.setText("Parking 3          " + m.getParkings().get(2).getNbPlaces()+ " / " + m.getParkings().get(2).getNbPlaceMax() + " places");
            }
        });
        tableViewVoitures.refresh(); // Rafraichit le tableau des voitures
    }

    public void dessinerMap(GraphicsContext gc) {
        int x = 0; // Position x du pixel
        int y = 0; // Position y du pixel

        for (int ligne = 0; ligne < m.getCarte().getHauteur(); ligne++) {
            for(int colonne=0; colonne<m.getCarte().getLongueur(); colonne++){
                int numPixel = m.getCarte().getCase(new Coordonnee(ligne,colonne)).getValeur();

                Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/map/PixelsMap/" + numPixel + ".png"))); // On récupère l'image du pixel
                gc.drawImage(image, x, y, 16, 16); // On dessine le pixel

                x += 16;
            }
            x = 0;
            y += 16;
        }
    }
}
