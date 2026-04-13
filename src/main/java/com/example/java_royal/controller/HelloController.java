package com.example.java_royal.controller;

import com.example.java_royal.model.User;
import com.example.java_royal.service.UserService;
import com.example.java_royal.session.SessionManager;
import com.example.java_royal.session.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Contrôleur pour la page de connexion.
 * Utilise UserService pour authentifier l'utilisateur et charger ses données.
 * Redirige vers Introduction si level=1, sinon vers Home.
 */
public class HelloController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label messageLabel;

    @FXML
    private void handleLogin() {
        String identifier = usernameField.getText() == null ? "" : usernameField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText();

        if (identifier.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Veuillez renseigner le nom d'utilisateur et le mot de passe.");
            return;
        }

        try {
            // Authentifie l'utilisateur via UserService (qui récupère aussi level et xp)
            User user = UserService.authenticate(identifier, password);

            if (user != null) {
                // Stocke les données en session
                UserSession session = UserSession.getInstance();
                session.update(user.getId(), user.getUsername(), user.getEmail(),
                              user.getCurrentLevel(), user.getCurrentXp());

                // Met à jour l'ancien SessionManager pour compatibilité
                SessionManager.getInstance().setCurrentUser(user);

                // Redirige vers Introduction si c'est le premier lancement (level=1)
                // Sinon, redirige vers Home
                if (user.getCurrentLevel() == 1) {
                    goToIntroduction();
                } else {
                    goToHome();
                }
            } else {
                messageLabel.setText("Identifiants invalides.");
            }

        } catch (SQLException e) {
            messageLabel.setText("Erreur de connexion à la base de données: " + e.getMessage());
        }
    }

    @FXML
    private void goToRegister() {
        try {
            Scene scene = usernameField.getScene();
            Stage stage = (Stage) scene.getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/java_royal/register-view.fxml"));
            stage.setScene(new Scene(root));
            stage.setTitle("S'inscrire");
            stage.show();
        } catch (IOException e) {
            messageLabel.setText("Impossible d'ouvrir la page d'inscription.");
        }
    }

    /**
     * Navigue vers la page d'introduction (premier lancement)
     */
    private void goToIntroduction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/java_royal/introduction-view.fxml"));
            if (loader.getLocation() == null) {
                messageLabel.setText("Erreur: Fichier introduction-view.fxml introuvable.");
                return;
            }
            Parent root = loader.load();
            Stage stage = (Stage) usernameField.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Introduction");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText("Impossible d'ouvrir la page d'introduction: " + e.getMessage());
        } catch (NullPointerException e) {
            messageLabel.setText("Erreur: Impossible de récupérer la fenêtre.");
        }
    }

    /**
     * Navigue vers l'accueil (lobby)
     */
    private void goToHome() {
        try {
            System.out.println("[HelloController] Tentative de chargement de home-view.fxml...");

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com/example/java_royal/home-view.fxml"));

            if (loader.getLocation() == null) {
                messageLabel.setText("Erreur: home-view.fxml introuvable.");
                System.err.println("[ERROR] home-view.fxml not found!");
                return;
            }

            Parent root = loader.load();
            System.out.println("[HelloController] home-view.fxml chargé avec succès!");

            Scene scene = usernameField.getScene();
            Stage stage = (Stage) scene.getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Accueil");
            stage.show();
        } catch (IOException e) {
            messageLabel.setText("Impossible d'ouvrir la page d'accueil.");
            e.printStackTrace();
            System.err.println("[ERROR] IOException: " + e.getMessage());
        }
    }
}
