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

/**
 * Contrôleur pour la vue d'introduction.
 * Affiche un message de bienvenue avec une transition fluide vers l'accueil.
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
            System.out.println("[IntroductionController] Tentative de chargement de home-view.fxml...");

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com/example/java_royal/home-view.fxml"));

            if (loader.getLocation() == null) {
                System.err.println("[IntroductionController] ❌ Fichier home-view.fxml introuvable!");
                return;
            }

            Parent root = loader.load();
            System.out.println("[IntroductionController] ✅ Fichier chargé avec succès!");

            Stage stage = (Stage) startButton.getScene().getWindow();
            Scene newScene = new Scene(root);

            // Crée une transition de fondu (fade out vers fade in)
            FadeTransition fadeOut = new FadeTransition(Duration.millis(500), stage.getScene().getRoot());
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);

            fadeOut.setOnFinished(event -> {
                stage.setScene(newScene);
                stage.setTitle("Accueil");
                System.out.println("[IntroductionController] ✅ Scène changée vers HomeView");

                FadeTransition fadeIn = new FadeTransition(Duration.millis(500), root);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            });

            fadeOut.play();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("[IntroductionController] ❌ IOException: " + e.getMessage());
        } catch (NullPointerException e) {
            System.err.println("[IntroductionController] ❌ NullPointerException: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("[IntroductionController] ❌ Exception générale: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

