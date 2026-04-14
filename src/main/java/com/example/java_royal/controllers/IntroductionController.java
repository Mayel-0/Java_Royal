package com.example.java_royal.controllers;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Objects;

/**
 * Contrôleur pour la vue d'introduction.
 */
public class IntroductionController {

    private static final String HOME_VIEW = "/com/example/java_royal/home-view.fxml";

    @FXML
    private Button startButton;

    /**
     * Navigue vers l'accueil (Home) avec une transition FadeTransition
     */
    @FXML
    private void goToHome() {
        try {
            Parent root = new FXMLLoader(Objects.requireNonNull(getClass().getResource(HOME_VIEW), "FXML introuvable: " + HOME_VIEW)).load();
            Stage stage = (Stage) startButton.getScene().getWindow();
            Scene nextScene = new Scene(root);

            // Crée une transition de fondu (fade out vers fade in)
            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), stage.getScene().getRoot());
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);

            fadeOut.setOnFinished(event -> {
                stage.setScene(nextScene);
                stage.setTitle("Accueil");
                System.out.println("[IntroductionController] ✅ Scène changée vers HomeView");

                FadeTransition fadeIn = new FadeTransition(Duration.millis(250), root);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            });

            fadeOut.play();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
