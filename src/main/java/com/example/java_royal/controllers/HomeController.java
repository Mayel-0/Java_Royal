package com.example.java_royal.controllers;

import com.example.java_royal.flappybird.FlappyBirdGame;
import com.example.java_royal.session.SessionManager;
import com.example.java_royal.session.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Screen;
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

    /**
     * Navigue vers la page du classement (Leaderboard)
     */
    @FXML
    private void handleLeaderboard() {
        try {
            System.out.println("[HomeController] Tentative de chargement du classement...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/java_royal/leaderboard-view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Classement");
            stage.show();

            System.out.println("[HomeController] ✅ Navigation vers le classement réussie");
        } catch (IOException e) {
            System.err.println("[HomeController] ❌ Erreur lors du chargement du classement: " + e.getMessage());
            e.printStackTrace();
        }
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

    @FXML
    private void handleOpenFlappyBird() {
        Stage stage = (Stage) welcomeLabel.getScene().getWindow();
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        FlappyBirdGame game = new FlappyBirdGame(bounds.getWidth(), bounds.getHeight());

        stage.setScene(new Scene(game, bounds.getWidth(), bounds.getHeight()));
        stage.setTitle("Clash Royale Flappy Bird");
        stage.setFullScreenExitHint("");
        stage.setFullScreen(true);
        stage.show();
        game.requestFocus();
    }

    @FXML
    private void handleOpenPendu() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/java_royal/pendu-view.fxml"));
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root, 1080, 760));
            stage.setTitle("Pendu - Clash Royale");
            stage.show();
        } catch (IOException e) {
            welcomeLabel.setText("Impossible d'ouvrir le mode Pendu.");
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
