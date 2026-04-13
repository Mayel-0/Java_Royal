package com.example.java_royal.controllers;

import com.example.java_royal.model.User;
import com.example.java_royal.service.UserService;
import com.example.java_royal.session.UserSession;
import org.mindrot.jbcrypt.BCrypt;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import com.example.java_royal.config.DatabaseConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Contrôleur pour la page d'inscription.
 * Après inscription, redirige vers la page d'introduction (level=1 par défaut).
 */
public class RegisterController {
    @FXML
    private TextField usernameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label messageLabel;

    @FXML
    private void handleRegister() {
        String username = usernameField.getText() == null ? "" : usernameField.getText().trim();
        String email = emailField.getText() == null ? "" : emailField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Veuillez remplir tous les champs.");
            return;
        }

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));
        String sql = "INSERT INTO users (username, email, password, current_level, current_xp) VALUES (?, ?, ?, 1, 0)";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, email);
            statement.setString(3, hashedPassword);
            statement.executeUpdate();

            // Récupère les données du nouvel utilisateur
            User newUser = UserService.getUserByUsername(username);
            if (newUser != null) {
                // Stocke en session
                UserSession session = UserSession.getInstance();
                session.update(newUser.getId(), newUser.getUsername(), newUser.getEmail(),
                              newUser.getCurrentLevel(), newUser.getCurrentXp());

                // Redirige vers l'introduction
                goToIntroduction();
            }

        } catch (SQLException e) {
            String error = e.getMessage() == null ? "" : e.getMessage().toLowerCase();
            if (error.contains("users.username") || error.contains("username")) {
                messageLabel.setText("Ce nom d'utilisateur existe deja.");
            } else if (error.contains("users.email") || error.contains("email")) {
                messageLabel.setText("Cet email existe deja.");
            } else {
                messageLabel.setText("Impossible de creer le compte: " + e.getMessage());
            }
        }
    }

    @FXML
    private void backToLogin() {
        try {
            Scene scene = usernameField.getScene();
            Stage stage = (Stage) scene.getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/java_royal/hello-view.fxml"));
            stage.setScene(new Scene(root));
            stage.setTitle("Connexion");
            stage.show();
        } catch (IOException e) {
            messageLabel.setText("Impossible de revenir à la connexion.");
        }
    }

    /**
     * Redirige vers la page d'introduction après une inscription réussie
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
            messageLabel.setText("Impossible d'accéder à l'introduction: " + e.getMessage());
        } catch (NullPointerException e) {
            messageLabel.setText("Erreur: Impossible de récupérer la fenêtre.");
        }
    }
}
