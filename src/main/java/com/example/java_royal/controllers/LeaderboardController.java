package com.example.java_royal.controllers;

import com.example.java_royal.service.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
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
 * Affiche les meilleurs joueurs avec un style Clash Royale.
 * Utilise une TableView pour afficher Rang, Pseudo, Niveau et XP Totale.
 */
public class LeaderboardController {

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
            // Charge les données du classement
            loadLeaderboard();

            // Configure les colonnes de la TableView
            setupTableColumns();

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("[LeaderboardController] ❌ Erreur lors du chargement du classement: " + e.getMessage());
        }
    }

    /**
     * Charge les données du classement depuis la base de données
     * Récupère les 10 meilleurs joueurs
     */
    private void loadLeaderboard() throws SQLException {
        System.out.println("[LeaderboardController] Chargement du classement...");

        // Récupère les 10 meilleurs joueurs
        List<UserService.LeaderboardEntry> topPlayers = UserService.getTopPlayers(10);

        System.out.println("[LeaderboardController] ✅ " + topPlayers.size() + " joueurs chargés");

        // Peuple la TableView avec les données
        for (int i = 0; i < topPlayers.size(); i++) {
            UserService.LeaderboardEntry entry = topPlayers.get(i);
            int rank = i + 1;

            LeaderboardRowData rowData = new LeaderboardRowData(
                rank,
                entry.getUsername(),
                entry.getLevel(),
                entry.getTotalXp()
            );

            leaderboardTable.getItems().add(rowData);
        }
    }

    /**
     * Configure les colonnes de la TableView avec les formatages appropriés
     */
    private void setupTableColumns() {
        // Colonne Rang avec médailles pour le top 3
        rankColumn.setCellValueFactory(cellData -> {
            String rankDisplay = getRankDisplay(cellData.getValue().getRank());
            return new javafx.beans.property.SimpleStringProperty(rankDisplay);
        });

        rankColumn.setCellFactory(column -> new TableCell<LeaderboardRowData, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    int rowIndex = getIndex();
                    if (rowIndex < 3) {
                        // Style pour le top 3
                        setStyle("-fx-text-fill: #FFD700; -fx-font-weight: bold; -fx-font-size: 14; -fx-alignment: CENTER;");
                    } else {
                        setStyle("-fx-text-fill: #AAAAAA; -fx-alignment: CENTER;");
                    }
                }
            }
        });

        // Colonne Pseudo
        usernameColumn.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getUsername())
        );

        usernameColumn.setCellFactory(column -> new TableCell<LeaderboardRowData, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    int rowIndex = getIndex();
                    if (rowIndex == 0) {
                        // 1er place : Or
                        setStyle("-fx-text-fill: #FFD700; -fx-font-weight: bold; -fx-font-size: 12; -fx-background-color: rgba(255, 215, 0, 0.1);");
                    } else if (rowIndex == 1) {
                        // 2ème place : Argent
                        setStyle("-fx-text-fill: #C0C0C0; -fx-font-weight: bold; -fx-font-size: 12; -fx-background-color: rgba(192, 192, 192, 0.1);");
                    } else if (rowIndex == 2) {
                        // 3ème place : Bronze
                        setStyle("-fx-text-fill: #CD7F32; -fx-font-weight: bold; -fx-font-size: 12; -fx-background-color: rgba(205, 127, 50, 0.1);");
                    } else {
                        // Autres joueurs
                        setStyle("-fx-text-fill: #CCCCCC;");
                    }
                }
            }
        });

        // Colonne Niveau
        levelColumn.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getLevel())
        );

        levelColumn.setCellFactory(column -> new TableCell<LeaderboardRowData, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText("⭐ " + item);
                    int rowIndex = getIndex();
                    if (rowIndex < 3) {
                        setStyle("-fx-text-fill: #FFD700; -fx-font-weight: bold; -fx-alignment: CENTER;");
                    } else {
                        setStyle("-fx-text-fill: #AAAAAA; -fx-alignment: CENTER;");
                    }
                }
            }
        });

        // Colonne XP Totale
        totalXpColumn.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getTotalXp())
        );

        totalXpColumn.setCellFactory(column -> new TableCell<LeaderboardRowData, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.format("%,d XP", item));
                    int rowIndex = getIndex();
                    if (rowIndex < 3) {
                        setStyle("-fx-text-fill: #FFD700; -fx-font-weight: bold; -fx-alignment: CENTER;");
                    } else {
                        setStyle("-fx-text-fill: #CCCCCC; -fx-alignment: CENTER;");
                    }
                }
            }
        });

        // Style de la TableView
        leaderboardTable.setStyle(
            "-fx-control-inner-background: #2a2a4e;" +
            "-fx-table-cell-border-color: #333333;" +
            "-fx-text-fill: #FFFFFF;" +
            "-fx-font-family: Arial;" +
            "-fx-border-color: #FFD700;" +
            "-fx-border-width: 2;"
        );

        // Alternance de couleurs des lignes
        leaderboardTable.setRowFactory(tv -> new TableRow<LeaderboardRowData>() {
            @Override
            public void updateItem(LeaderboardRowData item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() < 0) {
                    setStyle("");
                } else {
                    int index = getIndex();
                    if (index == 0) {
                        // 1ère ligne : Or avec fond
                        setStyle("-fx-background-color: rgba(255, 215, 0, 0.15);");
                    } else if (index == 1) {
                        // 2ème ligne : Argent
                        setStyle("-fx-background-color: rgba(192, 192, 192, 0.1);");
                    } else if (index == 2) {
                        // 3ème ligne : Bronze
                        setStyle("-fx-background-color: rgba(205, 127, 50, 0.1);");
                    } else if (index % 2 == 0) {
                        // Alternance : bleu foncé
                        setStyle("-fx-background-color: #2a2a4e;");
                    } else {
                        // Alternance : bleu un peu plus clair
                        setStyle("-fx-background-color: #353555;");
                    }
                }
            }
        });
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
            System.out.println("[LeaderboardController] Navigation vers l'accueil...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/java_royal/home-view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Accueil");
            stage.show();

            System.out.println("[LeaderboardController] ✅ Navigation réussie vers l'accueil");
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

        public int getRank() {
            return rank;
        }

        public String getUsername() {
            return username;
        }

        public int getLevel() {
            return level;
        }

        public int getTotalXp() {
            return totalXp;
        }
    }
}

