package com.example.java_royal.controllers;

import com.example.java_royal.config.DatabaseConnection;
import com.example.java_royal.session.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class ProfileController {
    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField newPasswordField;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    @FXML
    public void initialize() {
        UserSession session = UserSession.getInstance();
        usernameField.setText(session.getUsername());
        emailField.setText(session.getEmail());
        newPasswordField.clear();
    }

    @FXML
    private void handleSaveProfile() {
        UserSession session = UserSession.getInstance();

        String newUsername = usernameField.getText() == null ? "" : usernameField.getText().trim();
        String newEmail = emailField.getText() == null ? "" : emailField.getText().trim();
        String newPassword = newPasswordField.getText() == null ? "" : newPasswordField.getText();

        if (newUsername.isEmpty() || newEmail.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le nom d'utilisateur et l'email sont obligatoires.");
            return;
        }

        if (!EMAIL_PATTERN.matcher(newEmail).matches()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Format d'email invalide.");
            return;
        }

        try {
            updateUser(session.getId(), newUsername, newEmail, newPassword);
            session.update(session.getId(), newUsername, newEmail);
            showAlert(Alert.AlertType.INFORMATION, "Succes", "Profil mis a jour avec succes.");
            goBackHome();
        } catch (SQLException e) {
            String error = e.getMessage() == null ? "" : e.getMessage().toLowerCase();
            if (error.contains("users.username") || error.contains("username")) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Ce nom d'utilisateur existe deja.");
            } else if (error.contains("users.email") || error.contains("email")) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Cet email existe deja.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de mettre a jour le profil.");
            }
        }
    }

    public void updateUser(long id, String newName, String newEmail, String newPassword) throws SQLException {
        boolean hasNewPassword = newPassword != null && !newPassword.isBlank();

        String sqlWithoutPassword = "UPDATE users SET username = ?, email = ? WHERE id = ?";
        String sqlWithPassword = "UPDATE users SET username = ?, email = ?, password = ? WHERE id = ?";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(hasNewPassword ? sqlWithPassword : sqlWithoutPassword)) {

            statement.setString(1, newName);
            statement.setString(2, newEmail);

            if (hasNewPassword) {
                String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt(12));
                statement.setString(3, hashedPassword);
                statement.setLong(4, id);
            } else {
                statement.setLong(3, id);
            }

            statement.executeUpdate();
        }
    }

    @FXML
    private void handleBackHome() {
        goBackHome();
    }

    private void goBackHome() {
        try {
            Scene scene = usernameField.getScene();
            Stage stage = (Stage) scene.getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/java_royal/home-view.fxml"));
            stage.setScene(new Scene(root));
            stage.setTitle("Accueil");
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de revenir a l'accueil.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
