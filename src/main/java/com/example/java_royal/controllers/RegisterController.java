package com.example.java_royal.controllers;

import com.example.java_royal.model.User;
import com.example.java_royal.service.UserService;
import com.example.java_royal.session.UserSession;
import com.example.java_royal.config.DatabaseConnection;
import com.example.java_royal.util.SceneNavigator;
import org.mindrot.jbcrypt.BCrypt;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Contrôleur pour la page d'inscription.
 * Après inscription réussie :
 * - Crée un nouvel utilisateur avec level=1 et xp=0 par défaut
 * - Récupère les données du nouvel utilisateur
 * - Stocke les données en session utilisateur
 * - Redirige vers la page d'introduction
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
            Stage stage = (Stage) usernameField.getScene().getWindow();
            SceneNavigator.replaceScene(stage, "/com/example/java_royal/hello-view.fxml", "Connexion");
        } catch (IOException e) {
            messageLabel.setText("Impossible de revenir à la connexion.");
        }
    }

    /**
     * Redirige vers la page d'introduction après une inscription réussie
     */
    private void goToIntroduction() {
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            SceneNavigator.replaceScene(stage, "/com/example/java_royal/introduction-view.fxml", "Introduction");
        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText("Impossible d'accéder à l'introduction: " + e.getMessage());
        } catch (NullPointerException e) {
            messageLabel.setText("Erreur: Impossible de récupérer la fenêtre.");
        }
    }
}
