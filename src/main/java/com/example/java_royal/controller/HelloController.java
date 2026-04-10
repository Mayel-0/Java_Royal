package com.example.java_royal.controller;

import com.example.java_royal.config.DatabaseConnection;
import com.example.java_royal.model.User;
import com.example.java_royal.session.SessionManager;
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
import java.sql.ResultSet;
import java.sql.SQLException;

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

        String sql = "SELECT id, username, password FROM users WHERE username = ? OR email = ?";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, identifier);
            statement.setString(2, identifier);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String hashedPassword = resultSet.getString("password");
                    if (BCrypt.checkpw(password, hashedPassword)) {
                        SessionManager.getInstance().setCurrentUser(
                                new User(resultSet.getLong("id"), resultSet.getString("username"))
                        );
                        goToHome();
                    } else {
                        messageLabel.setText("Identifiants invalides.");
                    }
                } else {
                    messageLabel.setText("Identifiants invalides.");
                }
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

    private void goToHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/java_royal/home-view.fxml"));
            Parent root = loader.load();
            Scene scene = usernameField.getScene();
            Stage stage = (Stage) scene.getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Accueil");
            stage.show();
        } catch (IOException e) {
            messageLabel.setText("Impossible d'ouvrir la page d'accueil.");
        }
    }
}
