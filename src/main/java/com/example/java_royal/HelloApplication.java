package com.example.java_royal;

import com.example.java_royal.config.DatabaseInitializer;
import com.example.java_royal.util.ArenaImageCache;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        try {
            DatabaseInitializer.initialize();
            System.out.println("[HelloApplication] Initialisation du cache des images...");
            ArenaImageCache.initialize();
            ArenaImageCache.printCachedImages();

        } catch (SQLException e) {
            System.err.println("[HelloApplication] Base de donnees non initialisee: " + e.getMessage());
        }

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/com/example/java_royal/pendu-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 720);
        stage.setTitle("Pendu Royale");
        stage.setScene(scene);
        stage.show();
    }
}
