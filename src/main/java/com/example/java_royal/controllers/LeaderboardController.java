package com.example.java_royal.controllers;

import com.example.java_royal.service.UserService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Contrôleur pour la vue du classement (Leaderboard).
 */
public class LeaderboardController {

    private static final String HOME_VIEW = "/com/example/java_royal/home-view.fxml";
    private static final String TOP_STYLE = "-fx-text-fill: #FFD700; -fx-font-weight: bold; -fx-font-size: 14; -fx-alignment: CENTER;";
    private static final String STANDARD_STYLE = "-fx-text-fill: #AAAAAA; -fx-alignment: CENTER;";

    @FXML
    private BorderPane rootPane;

    @FXML
    private TableView<LeaderboardRowData> leaderboardTable;

    @FXML
    private TableColumn<LeaderboardRowData, String> rankColumn;

    @FXML
    private TableColumn<LeaderboardRowData, String> usernameColumn;

    @FXML
    private TableColumn<LeaderboardRowData, Integer> levelColumn;

    @FXML
    private TableColumn<LeaderboardRowData, Integer> totalXpColumn;

    /**
     * Initialisation du contrôleur appelée automatiquement après le chargement du FXML
     */
    @FXML
    public void initialize() {
        try {
            leaderboardTable.getItems().setAll(loadLeaderboard());
            setupTableColumns();
            applyTableStyles();
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("[LeaderboardController] ❌ Erreur lors du chargement du classement: " + e.getMessage());
        }
    }

    /**
     * Charge les données du classement depuis la base de données
     * Récupère les 10 meilleurs joueurs
     */
    private List<LeaderboardRowData> loadLeaderboard() throws SQLException {
        System.out.println("[LeaderboardController] Chargement du classement...");

        // Récupère les 10 meilleurs joueurs
        List<UserService.LeaderboardEntry> topPlayers = UserService.getTopPlayers(10);

        System.out.println("[LeaderboardController] ✅ " + topPlayers.size() + " joueurs chargés");

        List<LeaderboardRowData> rows = new java.util.ArrayList<>(topPlayers.size());
        for (int index = 0; index < topPlayers.size(); index++) {
            UserService.LeaderboardEntry entry = topPlayers.get(index);
            rows.add(new LeaderboardRowData(index + 1, entry.getUsername(), entry.getLevel(), entry.getTotalXp()));
        }
        return rows;
    }

    /**
     * Configure les colonnes de la TableView avec les formatages appropriés
     */
    private void setupTableColumns() {
        rankColumn.setCellValueFactory(cellData -> new SimpleStringProperty(getRankDisplay(cellData.getValue().getRank())));
        usernameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUsername()));
        levelColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getLevel()));
        totalXpColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getTotalXp()));

        rankColumn.setCellFactory(column -> createStyledCell(true));
        usernameColumn.setCellFactory(column -> createStyledCell(false));
        levelColumn.setCellFactory(column -> createNumericCell(item -> "⭐ " + item));
        totalXpColumn.setCellFactory(column -> createNumericCell(item -> String.format("%,d XP", item)));
    }

    /**
     * Applique les styles à la TableView et à ses lignes
     */
    private void applyTableStyles() {
        leaderboardTable.setStyle("-fx-control-inner-background: #2a2a4e;" +
            "-fx-table-cell-border-color: #333333;" +
            "-fx-text-fill: #FFFFFF;" +
            "-fx-font-family: Arial;" +
            "-fx-border-color: #FFD700;" +
            "-fx-border-width: 2;");

        leaderboardTable.setRowFactory(tv -> new TableRow<LeaderboardRowData>() {
            @Override
            public void updateItem(LeaderboardRowData item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() < 0) {
                    setStyle("");
                    return;
                }

                int index = getIndex();
                if (index == 0) {
                    setStyle("-fx-background-color: rgba(255, 215, 0, 0.15);");
                } else if (index == 1) {
                    setStyle("-fx-background-color: rgba(192, 192, 192, 0.1);");
                } else if (index == 2) {
                    setStyle("-fx-background-color: rgba(205, 127, 50, 0.1);");
                } else {
                    setStyle(index % 2 == 0 ? "-fx-background-color: #2a2a4e;" : "-fx-background-color: #353555;");
                }
            }
        });
    }

    /**
     * Crée une cellule stylisée pour les colonnes du tableau
     */
    private <T> TableCell<LeaderboardRowData, T> createStyledCell(boolean useTop3Highlight) {
        return new TableCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                    return;
                }

                setText(item.toString());
                setStyle(getIndex() < 3 && useTop3Highlight ? TOP_STYLE : STANDARD_STYLE);
            }
        };
    }

    /**
     * Crée une cellule numérique avec formatage personnalisé
     */
    private TableCell<LeaderboardRowData, Integer> createNumericCell(java.util.function.Function<Integer, String> formatter) {
        return new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                    return;
                }

                setText(formatter.apply(item));
                setStyle(getIndex() < 3 ? TOP_STYLE : STANDARD_STYLE);
            }
        };
    }

    /**
     * Retourne l'affichage du rang avec médailles pour le top 3
     *
     * @param rank Numéro du rang
     * @return String avec médaille ou numéro simple
     */
    private String getRankDisplay(int rank) {
        return switch (rank) {
            case 1 -> "🥇 1er";
            case 2 -> "🥈 2ème";
            case 3 -> "🥉 3ème";
            default -> "#" + rank;
        };
    }

    /**
     * Revient à la page d'accueil (HomeView)
     */
    @FXML
    private void goBackHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(HOME_VIEW));
            Parent root = loader.load();
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Accueil");
            stage.show();
        } catch (IOException e) {
            System.err.println("[LeaderboardController] ❌ Erreur lors du retour à l'accueil: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Classe interne représentant une ligne du tableau de classement
     */
    public static class LeaderboardRowData {
        private final int rank;
        private final String username;
        private final int level;
        private final int totalXp;

        public LeaderboardRowData(int rank, String username, int level, int totalXp) {
            this.rank = rank;
            this.username = username;
            this.level = level;
            this.totalXp = totalXp;
        }

        public int getRank() { return rank; }
        public String getUsername() { return username; }
        public int getLevel() { return level; }
        public int getTotalXp() { return totalXp; }
    }
}

