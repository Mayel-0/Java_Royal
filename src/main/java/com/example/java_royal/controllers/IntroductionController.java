package com.example.java_royal.controllers;

import com.example.java_royal.util.SceneNavigator;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

/**
 * Contrôleur pour la vue d'introduction.
 */
public class IntroductionController {

    @FXML
    private Button startButton;

    /**
     * Navigue vers l'accueil (Home) avec une transition FadeTransition
     */
    @FXML
    private void goToHome() {
        try {
            Stage stage = (Stage) startButton.getScene().getWindow();

            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), stage.getScene().getRoot());
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);

            fadeOut.setOnFinished(event -> {
                try {
                    SceneNavigator.replaceScene(stage, "/com/example/java_royal/home-view.fxml", "Accueil");
                    FadeTransition fadeIn = new FadeTransition(Duration.millis(250), stage.getScene().getRoot());
                    fadeIn.setFromValue(0.0);
                    fadeIn.setToValue(1.0);
                    fadeIn.play();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            fadeOut.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
