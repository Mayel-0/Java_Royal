package com.example.java_royal;

import com.example.java_royal.config.DatabaseInitializer;
import com.example.java_royal.util.ArenaImageCache;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        try {
            // Initialise la base de données
            DatabaseInitializer.initialize();

            // Initialise le cache des images d'arènes
            System.out.println("[HelloApplication] Initialisation du cache des images...");
            ArenaImageCache.initialize();
            ArenaImageCache.printCachedImages();

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur base de donnees");
            alert.setHeaderText("Initialisation de la base impossible");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return;
        }

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/com/example/java_royal/start-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 420, 260);
        stage.setTitle("Java Royal");
        stage.setScene(scene);
        stage.show();
    }
}
