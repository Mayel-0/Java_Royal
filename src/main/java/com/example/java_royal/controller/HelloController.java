package com.example.java_royal.controller;

import com.example.java_royal.model.User;
import com.example.java_royal.service.UserService;
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
 * Contrôleur pour la page de connexion (login).
 * Utilise UserService pour authentifier et charge les données complètes de l'utilisateur.
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
            System.out.println("[HelloController] Tentative login avec: " + identifier);

            // Authentifie via UserService (retourne User complet avec id, username, email, level, xp)
            User user = UserService.authenticate(identifier, password);

            if (user != null) {
                System.out.println("[HelloController] ✅ Authentification réussie: " + user);

                // Met à jour UserSession avec TOUS les champs (id, username, email, level, xp)
                UserSession session = UserSession.getInstance();
                session.update(user.getId(), user.getUsername(), user.getEmail(),
                              user.getCurrentLevel(), user.getCurrentXp());

                System.out.println("[HelloController] UserSession mise à jour - ID: " + session.getId());

                // Redirige selon le level
                if (user.getCurrentLevel() == 1) {
                    System.out.println("[HelloController] Level = 1, redirection vers Introduction");
                    goToIntroduction();
                } else {
                    System.out.println("[HelloController] Level > 1, redirection vers Home");
                    goToHome();
                }
            } else {
                messageLabel.setText("Identifiants invalides.");
                System.err.println("[HelloController] ❌ Authentification échouée pour: " + identifier);
            }

        } catch (SQLException e) {
            messageLabel.setText("Erreur de connexion à la base de données: " + e.getMessage());
            e.printStackTrace();
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
     * Navigue vers la page d'introduction (premier lancement, level=1)
     */
    private void goToIntroduction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/java_royal/introduction-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Introduction");
            stage.show();
        } catch (IOException e) {
            messageLabel.setText("Impossible d'ouvrir la page d'introduction.");
            e.printStackTrace();
        }
    }

    /**
     * Navigue vers l'accueil (lobby)
     */
    private void goToHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/java_royal/home-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Accueil");
            stage.show();
        } catch (IOException e) {
            messageLabel.setText("Impossible d'ouvrir la page d'accueil.");
            e.printStackTrace();
        }
    }
}

