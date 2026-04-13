package com.example.java_royal.controllers;

import com.example.java_royal.model.User;
import com.example.java_royal.service.UserService;
import com.example.java_royal.session.UserSession;
import com.example.java_royal.util.ArenaImageCache;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Contrôleur pour la vue d'accueil (Lobby).
 * Gère l'affichage des données du joueur avec un background d'arène dynamique.
 * Implémente l'architecture MVC en utilisant UserService pour récupérer les données.
 *
 * Support de deux modes d'affichage :
 * 1. Background fullscreen (via BorderPane + ArenaImageCache) - Mode principal
 * 2. ImageView alternative (fallback) - Mode secondaire
 */
public class HomeController {

    @FXML
    private BorderPane rootPane;

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
            // Nettoie d'abord le background du rootPane
            rootPane.setStyle("");

            // Initialise le cache des images si pas déjà fait
            ArenaImageCache.initialize();

            // Charge les données du joueur
            loadPlayerData();

            // Met à jour le background selon le niveau
            updateBackground();

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
        System.out.println("[HomeController] loadPlayerData() - Session ID: " + session.getId());

        // Récupère les données à jour depuis la base de données
        User user = UserService.getUserById(session.getId());
        System.out.println("[HomeController] getUserById() - User: " + user);

        if (user != null) {
            System.out.println("[HomeController] User trouvé: " + user.getUsername());

            // Met à jour la session
            session.update(user.getId(), user.getUsername(), user.getEmail(),
                          user.getCurrentLevel(), user.getCurrentXp());

            // Met à jour l'interface avec le vrai username
            String welcomeText = "Bienvenue " + user.getUsername() + " !";
            welcomeLabel.setText(welcomeText);
            System.out.println("[HomeController] welcomeLabel set to: " + welcomeText);

            levelBadge.setText(String.valueOf(user.getCurrentLevel()));

            // Configure la barre de progression XP
            double progressPercentage = user.getXpProgressPercentage();
            xpProgressBar.setProgress(progressPercentage);

            int nextLevelThreshold = user.getNextLevelThreshold();
            xpLabel.setText(user.getCurrentXp() + " / " + nextLevelThreshold + " XP");
        } else {
            System.err.println("[HomeController] ❌ User NOT found in DB!");
            welcomeLabel.setText("Utilisateur non trouvé");
        }
    }

    /**
     * Mise à jour du fond d'écran selon le niveau du joueur.
     *
     * Logique:
     * - Récupère le niveau depuis la session
     * - Limite au niveau 5 si dépassé
     * - Charge l'image correspondante du cache
     * - Applique l'image en fond d'écran avec la bonne configuration
     * - Fallback sur ImageView si rootPane n'est pas disponible
     *
     * Formule:
     * - Niveau 1 = arene1.jpg (Arène d'entraînement)
     * - Niveau 2 = arene2.jpg
     * - Niveau 3 = arene3.jpg
     * - Niveau 4 = arene4.jpg
     * - Niveau 5+ = arene5.jpg (par défaut)
     */
    private void updateBackground() {
        UserSession session = UserSession.getInstance();
        int level = session.getCurrentLevel();
        System.out.println("[HomeController] updateBackground() - Level: " + level);

        // Récupère l'image du cache
        Image arenaImage = ArenaImageCache.getArenaImage(level);
        System.out.println("[HomeController] arenaImage retrieved: " + (arenaImage != null ? "NOT NULL" : "NULL"));

        if (arenaImage != null && !arenaImage.isError()) {
            System.out.println("[HomeController] Image width: " + arenaImage.getWidth() + ", height: " + arenaImage.getHeight());

            // Mode 1: Affichage fullscreen via Background (principal)
            if (rootPane != null) {
                try {
                    // Crée une BackgroundImage avec l'image chargée
                    // Configuration: COVER mode pour que l'image couvre TOUT l'écran
                    BackgroundImage bgImage = new BackgroundImage(
                        arenaImage,
                        BackgroundRepeat.NO_REPEAT,      // Pas de répétition horizontale
                        BackgroundRepeat.NO_REPEAT,      // Pas de répétition verticale
                        BackgroundPosition.CENTER,       // Centré
                        new BackgroundSize(
                            100,                         // 100% de la largeur
                            100,                         // 100% de la hauteur
                            true,                        // Utiliser les pourcentages
                            true,                        // Utiliser les pourcentages
                            true,                        // ✅ COVER MODE = l'image couvre TOUTE la zone
                            false                        // Ne pas maintenir aspect ratio (étire si nécessaire)
                        )
                    );

                    // Applique le background au rootPane
                    javafx.scene.layout.Background background = new javafx.scene.layout.Background(bgImage);
                    rootPane.setBackground(background);
                    rootPane.setStyle(""); // S'assurer qu'il n'y a pas de couleur CSS

                    System.out.println("[HomeController] 📏 Mode COVER: L'image couvre 100% de l'écran!");
                    System.out.println("[HomeController] ✅ Background arene" + Math.min(level, 5) + " appliqué avec succès!");
                    arenaImageView.setVisible(false);
                    arenaPlaceholder.setVisible(false);
                } catch (Exception e) {
                    System.err.println("[HomeController] ⚠️ Erreur lors de l'application du background: " + e.getMessage());
                    // Fallback sur ImageView
                    applyImageViewFallback(arenaImage, level);
                }
            } else {
                // Si rootPane n'existe pas, utiliser ImageView
                applyImageViewFallback(arenaImage, level);
            }
        } else {
            System.err.println("[HomeController] ⚠️ Image pour niveau " + level + " est NULL ou erreur!");
            if (arenaImage != null) {
                System.err.println("[HomeController] Image error: " + arenaImage.isError());
            }
            // Afficher un placeholder texte
            if (arenaPlaceholder != null) {
                arenaPlaceholder.setText("Arène " + level + " - Entraînement");
                arenaPlaceholder.setVisible(true);
            }
            if (arenaImageView != null) {
                arenaImageView.setVisible(false);
            }
        }
    }

    /**
     * Fallback: affiche l'image via ImageView si le Background n'est pas disponible
     */
    private void applyImageViewFallback(Image image, int level) {
        if (arenaImageView != null) {
            System.out.println("[HomeController] 🔄 Fallback ImageView pour arène " + level);
            arenaImageView.setImage(image);
            arenaImageView.setVisible(true);
            if (arenaPlaceholder != null) {
                arenaPlaceholder.setVisible(false);
            }
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

