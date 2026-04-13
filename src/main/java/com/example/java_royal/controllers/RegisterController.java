package com.example.java_royal.controllers;

import com.example.java_royal.config.DatabaseConnection;
import org.mindrot.jbcrypt.BCrypt;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
        String sql = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, email);
            statement.setString(3, hashedPassword);
            statement.executeUpdate();
            messageLabel.setText("Inscription reussie. Vous pouvez vous connecter.");
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
}
