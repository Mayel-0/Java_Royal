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
import java.util.Objects;

/**
 * Contrôleur pour la page de connexion (login).
 */
public class HelloController {
    private static final String REGISTER_VIEW = "/com/example/java_royal/register-view.fxml";
    private static final String INTRODUCTION_VIEW = "/com/example/java_royal/introduction-view.fxml";
    private static final String HOME_VIEW = "/com/example/java_royal/home-view.fxml";

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label messageLabel;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showMessage("Le nom d'utilisateur et le mot de passe sont requis.");
            return;
        }

        try {
            User user = UserService.authenticate(username, password);

            if (user == null) {
                showMessage("Nom d'utilisateur ou mot de passe incorrect.");
                return;
            }

            SessionManager.getInstance().setCurrentUser(user);
            System.out.println("[HelloController] ✅ Authentification réussie: " + user);

            // Stocke les informations de l'utilisateur dans la session
            UserSession session = UserSession.getInstance();
            session.setId(user.getId());
            session.setUsername(user.getUsername());
            session.setEmail(user.getEmail());
            session.setCurrentLevel(user.getCurrentLevel());
            session.setCurrentXp(user.getCurrentXp());
            System.out.println("[HelloController] UserSession mise à jour - ID: " + UserSession.getInstance().getId());
            try {
                loadView(INTRODUCTION_VIEW, "Introduction");
            } catch (IOException e) {
                showMessage("Impossible d'ouvrir la page d'introduction.");
                e.printStackTrace();
            }
        } catch (SQLException e) {
            showMessage("Erreur de connexion à la base de données: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @FXML
    private void goToRegister() {
        try {
            loadView(REGISTER_VIEW, "S'inscrire");
        } catch (IOException e) {
            showMessage("Impossible d'ouvrir la page d'inscription.");
            e.printStackTrace();
        }
    }

    /**
     * Navigue vers la page d'introduction (premier lancement, level=1)
     */
    private void loadView(String resourcePath, String title) throws IOException {
        Parent root = loadFxml(resourcePath);
        Stage stage = getStage();

        double width = stage.getWidth();
        double height = stage.getHeight();
        stage.setScene(width > 0 && height > 0 ? new Scene(root, width, height) : new Scene(root));
        stage.setTitle(title);
        stage.show();
    }

    private Parent loadFxml(String resourcePath) throws IOException {
        var url = Objects.requireNonNull(getClass().getResource(resourcePath), "FXML introuvable: " + resourcePath);
        return new FXMLLoader(url).load();
    }

    private Stage getStage() {
        return (Stage) usernameField.getScene().getWindow();
    }

    private String safeTrim(TextField field) {
        return field == null || field.getText() == null ? "" : field.getText().trim();
    }

    private String safeText(PasswordField field) {
        return field == null || field.getText() == null ? "" : field.getText();
    }

    private void showMessage(String message) {
        messageLabel.setText(message);
    }
}
