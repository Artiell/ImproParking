module com.example.improparking_projet {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;

    exports com.example.improparking_projet.MVC;
    opens com.example.improparking_projet.MVC to javafx.fxml;
    exports com.example.improparking_projet.parking;
    opens com.example.improparking_projet.parking to javafx.fxml;
    exports com.example.improparking_projet.voiture;
    opens com.example.improparking_projet.voiture to javafx.fxml;
}