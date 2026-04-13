package com.example.java_royal.controllers;

import com.example.java_royal.model.User;
import com.example.java_royal.service.UserService;
import com.example.java_royal.session.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Contrôleur pour la vue d'accueil (Lobby).
 * Gère l'affichage des données du joueur et la navigation vers le menu principal.
 * Implémente l'architecture MVC en utilisant UserService pour récupérer les données.
 */
public class HomeController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label levelBadge;

    @FXML
    private ProgressBar xpProgressBar;

    @FXML
    private Label xpLabel;

    @FXML
    private ImageView arenaImageView;

    @FXML
    private Label arenaPlaceholder;

    /**
     * Initialisation du contrôleur appelée automatiquement après le chargement du FXML
     */
    @FXML
    public void initialize() {
        try {
            loadPlayerData();
            updateArenaBackground();
        } catch (SQLException e) {
            e.printStackTrace();
            welcomeLabel.setText("Erreur: impossible de charger les données du joueur");
        }
    }

    /**
     * Charge les données du joueur depuis la session et met à jour l'interface
     */
    private void loadPlayerData() throws SQLException {
        UserSession session = UserSession.getInstance();

        // Récupère les données à jour depuis la base de données
        User user = UserService.getUserById(session.getId());

        if (user != null) {
            // Met à jour la session
            session.update(user.getId(), user.getUsername(), user.getEmail(),
                          user.getCurrentLevel(), user.getCurrentXp());

            // Met à jour l'interface
            welcomeLabel.setText("Bienvenue " + user.getUsername() + " !");
            levelBadge.setText(String.valueOf(user.getCurrentLevel()));

            // Configure la barre de progression XP
            double progressPercentage = user.getXpProgressPercentage();
            xpProgressBar.setProgress(progressPercentage);

            int nextLevelThreshold = user.getNextLevelThreshold();
            xpLabel.setText(user.getCurrentXp() + " / " + nextLevelThreshold + " XP");
        }
    }

    /**
     * Mise à jour du fond d'arène selon le niveau du joueur.
     * Formule:
     * - Niveau 1 = Arène d'entraînement
     * - Niveau 2 = Arène 2, etc.
     */
    private void updateArenaBackground() {
        UserSession session = UserSession.getInstance();
        int level = session.getCurrentLevel();

        String arenaName = "Arène " + level;
        String imagePath = "/com/example/java_royal/arenas/arena_" + level + ".png";

        try {
            Image image = new Image(getClass().getResourceAsStream(imagePath));
            if (image.isError()) {
                // Si l'image n'existe pas, affiche un placeholder
                arenaPlaceholder.setText(arenaName + " - Entraînement");
                arenaImageView.setVisible(false);
                arenaPlaceholder.setVisible(true);
            } else {
                arenaImageView.setImage(image);
                arenaImageView.setVisible(true);
                arenaPlaceholder.setVisible(false);
            }
        } catch (Exception e) {
            // Fallback sur placeholder si ressource introuvable
            arenaPlaceholder.setText(arenaName + " - Entraînement");
            arenaImageView.setVisible(false);
            arenaPlaceholder.setVisible(true);
        }
    }

    /**
     * Gère la déconnexion et retour à l'écran de connexion
     */
    @FXML
    private void handleLogout() {
        UserSession.getInstance().clear();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/java_royal/start-view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Java Royal");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

