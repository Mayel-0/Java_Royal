package com.example.java_royal.controllers;

import com.javaroyal.games.flappy.FlappyBirdGame;
import com.example.java_royal.model.User;
import com.example.java_royal.service.UserService;
import com.example.java_royal.session.SessionManager;
import com.example.java_royal.session.UserSession;
import com.example.java_royal.util.ArenaImageCache;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

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
    private Label arenaPlaceholder;

    private static final String SUDOKU_VIEW = "/com/example/java_royal/sudoku-view.fxml";

    /**
     * Initialisation du contrôleur appelée automatiquement après le chargement du FXML
     */
    @FXML
    public void initialize() {
        try {
            rootPane.setStyle("");
            ArenaImageCache.initialize();
            loadPlayerData();
            updateBackground();
        } catch (SQLException e) {
            e.printStackTrace();
            refreshWelcomeLabel();
        }
    }

    private void loadPlayerData() throws SQLException {
        UserSession session = UserSession.getInstance();
        User user = UserService.getUserById(session.getId());

        if (user == null) {
            welcomeLabel.setText("Utilisateur non trouve");
            refreshWelcomeLabel();
            return;
        }

        session.update(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getCurrentLevel(),
                user.getCurrentXp()
        );

        welcomeLabel.setText("Bienvenue " + user.getUsername() + " !");
        if (levelBadge != null) {
            levelBadge.setText(String.valueOf(user.getCurrentLevel()));
        }
        if (xpProgressBar != null) {
            xpProgressBar.setProgress(user.getXpProgressPercentage());
        }
        if (xpLabel != null) {
            xpLabel.setText(user.getCurrentXp() + " / " + user.getNextLevelThreshold() + " XP");
        }
    }

    private void updateBackground() {
        int level = UserSession.getInstance().getCurrentLevel();
        Image arenaImage = ArenaImageCache.getArenaImage(level);

        if (arenaImage != null && !arenaImage.isError()) {
            if (rootPane != null) {
                try {
                    BackgroundImage bgImage = new BackgroundImage(
                            arenaImage,
                            BackgroundRepeat.NO_REPEAT,
                            BackgroundRepeat.NO_REPEAT,
                            BackgroundPosition.CENTER,
                            new BackgroundSize(100, 100, true, true, true, false)
                    );

                    rootPane.setBackground(new javafx.scene.layout.Background(bgImage));
                    rootPane.setStyle("");
                    if (arenaPlaceholder != null) {
                        arenaPlaceholder.setVisible(false);
                    }
                } catch (Exception e) {
                    if (arenaPlaceholder != null) {
                        arenaPlaceholder.setText("Arene " + level + " - Entrainement");
                        arenaPlaceholder.setVisible(true);
                    }
                }
            }
            return;
        }

        if (arenaPlaceholder != null) {
            arenaPlaceholder.setText("Arene " + level + " - Entrainement");
            arenaPlaceholder.setVisible(true);
        }
    }

    @FXML
    private void handleOpenMemory() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/java_royal/memory-view.fxml"));
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();

            double width = stage.getWidth();
            double height = stage.getHeight();

            Scene scene = new Scene(root, width, height);
            stage.setScene(scene);
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
    private void handleOpenTrueOrFalse() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/javaroyal/games/t_or_f/true-false-view.fxml"));
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();

            double width = stage.getWidth();
            double height = stage.getHeight();

            Scene scene = new Scene(root, width, height);
            stage.setScene(scene);
            stage.setTitle("True or False - Clash Royale");
            stage.show();
        } catch (IOException e) {
            welcomeLabel.setText("Impossible d'ouvrir le mode True or False.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleOpenPendu() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/java_royal/pendu-view.fxml"));
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();

            double width = stage.getWidth();
            double height = stage.getHeight();

            Scene scene = new Scene(root, width, height);
            stage.setScene(scene);
            stage.setTitle("Pendu - Clash Royale");
            stage.show();
        } catch (IOException e) {
            welcomeLabel.setText("Impossible d'ouvrir le mode Pendu.");
        }
    }

    @FXML
    private void handleOpenProfile() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/java_royal/profile-view.fxml"));
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();

            double width = stage.getWidth();
            double height = stage.getHeight();

            Scene scene = new Scene(root, width, height);
            stage.setScene(scene);
            stage.setTitle("Modifier Profil");
            stage.show();
        } catch (IOException e) {
            welcomeLabel.setText("Impossible d'ouvrir la page profil.");
        }
    }

    @FXML
    private void handleLeaderboard() {
        try {
            System.out.println("[HomeController] Tentative de chargement du classement...");
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            com.example.java_royal.util.SceneNavigator.replaceScene(stage, "/com/example/java_royal/leaderboard-view.fxml", "Classement");
            System.out.println("[HomeController] ✅ Navigation vers le classement réussie");
        } catch (IOException e) {
            System.err.println("[HomeController] ❌ Erreur lors du chargement du classement: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSudoku() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(SUDOKU_VIEW));
            Parent root = loader.load();

            Stage stage = (Stage) rootPane.getScene().getWindow();

            double width = stage.getWidth();
            double height = stage.getHeight();

            Scene scene = new Scene(root, width, height);
            stage.setScene(scene);
            stage.setTitle("Sudoku");
            stage.show();
        } catch (IOException e) {
            System.err.println("[HomeController] ❌ Erreur lors du chargement du Sudoku: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        try {
            UserSession.getInstance().clear();
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/java_royal/start-view.fxml"));
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();

            double width = stage.getWidth();
            double height = stage.getHeight();

            Scene scene = new Scene(root, width, height);
            stage.setScene(scene);
            stage.setTitle("Java Royal");
            stage.show();
        } catch (IOException e) {
            welcomeLabel.setText("Erreur lors de la déconnexion.");
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
