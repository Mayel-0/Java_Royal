package com.example.java_royal.controllers;

import com.example.java_royal.session.SessionManager;
import com.example.java_royal.session.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class HomeController {
    @FXML
    private Label welcomeLabel;

    @FXML
    public void initialize() {
        refreshWelcomeLabel();
    }

    @FXML
    private void handleOpenProfile() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/java_royal/profile-view.fxml"));
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier Profil");
            stage.show();
        } catch (IOException e) {
            welcomeLabel.setText("Impossible d'ouvrir la page profil.");
        }
    }

    @FXML
    private void handleOpenMemory() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/java_royal/memory-view.fxml"));
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root, 980, 760));
            stage.setTitle("Memory - Clash Royale");
            stage.show();
        } catch (IOException e) {
            welcomeLabel.setText("Impossible d'ouvrir le mode Memory.");
        }
    }

    private void refreshWelcomeLabel() {
        String username = UserSession.getInstance().getUsername();

        if (username == null || username.isBlank()) {
            username = SessionManager.getInstance().getCurrentUser() == null
                    ? "Utilisateur"
                    : SessionManager.getInstance().getCurrentUser().getUsername();

            if (SessionManager.getInstance().getCurrentUser() != null) {
                UserSession.getInstance().setId(SessionManager.getInstance().getCurrentUser().getId());
                UserSession.getInstance().setUsername(SessionManager.getInstance().getCurrentUser().getUsername());
            }
        }

        welcomeLabel.setText("Bonjour " + username);
    }
}
