package com.example.improparking_projet.MVC;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;

public class MainApplication extends Application {

    /**
     * Méthode qui s'éxecute à l'ouverture de l'application
     */
    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("/fxml/Main.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 300);
        //Application de css sur la scène
        scene.getStylesheets().add(getClass().getResource("/css/Main.css").toExternalForm());
        
        stage.setTitle("Parking Simulator 2023");
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/LogoParking.png"))));
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    /**
     * Méthode main qui lance l'application
     */
    public static void main(String[] args) {
        launch();

    }
}