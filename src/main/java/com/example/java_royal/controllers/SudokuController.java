package com.example.java_royal.controllers;

import com.example.java_royal.model.SudokuGame;
import com.example.java_royal.model.SudokuGrid;
import com.example.java_royal.model.SudokuGrid.Position;
import com.example.java_royal.service.SudokuScoreService;
import com.example.java_royal.service.UserService;
import com.example.java_royal.session.UserSession;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class SudokuController {
    private static final int DEFAULT_CLUES = 36;
    private static final int SCORE_BASE = 10000;
    private static final int SCORE_TIME_PENALTY = 2;
    private static final int SCORE_MOVE_PENALTY = 8;
    private static final int XP_PER_WIN = 25;
    private static final String HOME_VIEW = "/com/example/java_royal/home-view.fxml";

    @FXML
    private GridPane sudokuGrid;

    @FXML
    private Label timerLabel;

    @FXML
    private Label scoreLabel;

    @FXML
    private Label movesLabel;

    @FXML
    private Button newGameButton;

    @FXML
    private Button backButton;

    private final Random random = new Random();
    private SudokuGame game;
    private int[][] currentGrid;
    private TextField[][] cells;
    private int elapsedSeconds;
    private int moves;
    private Timeline timer;

    @FXML
    public void initialize() {
        setupNewGame();
    }

    @FXML
    private void handleNewGame() {
        setupNewGame();
    }

    @FXML
    private void handleBack() {
        if (timer != null) {
            timer.stop();
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(HOME_VIEW));
            Parent root = loader.load();

            Stage stage = (Stage) sudokuGrid.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Accueil");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupNewGame() {
        if (timer != null) {
            timer.stop();
        }
        elapsedSeconds = 0;
        moves = 0;
        game = new SudokuGame(random, DEFAULT_CLUES);
        currentGrid = game.getPuzzle();
        buildGrid();
        updateScore();
        startTimer();
    }

    private void buildGrid() {
        sudokuGrid.getChildren().clear();
        cells = new TextField[SudokuGrid.SIZE][SudokuGrid.SIZE];

        for (int row = 0; row < SudokuGrid.SIZE; row++) {
            for (int col = 0; col < SudokuGrid.SIZE; col++) {
                TextField cell = createCell(row, col);
                cells[row][col] = cell;
                StackPane wrapper = new StackPane(cell);
                wrapper.getStyleClass().add("sudoku-cell-wrapper");
                if ((row / 3 + col / 3) % 2 == 0) {
                    wrapper.getStyleClass().add("sudoku-cell-wrapper-alt");
                }
                sudokuGrid.add(wrapper, col, row);
            }
        }
    }

    private TextField createCell(int row, int col) {
        TextField field = new TextField();
        field.setPrefSize(48, 48);
        field.getStyleClass().add("sudoku-cell");
        field.setPromptText("");

        int value = currentGrid[row][col];
        if (value != 0) {
            field.setText(String.valueOf(value));
            field.setDisable(true);
            field.getStyleClass().add("sudoku-cell-fixed");
        }

        field.textProperty().addListener((obs, oldValue, newValue) -> {
            String sanitized = sanitizeInput(newValue);
            if (!sanitized.equals(newValue)) {
                field.setText(sanitized);
                return;
            }
            handleCellChange(row, col, sanitized);
        });

        return field;
    }

    private void handleCellChange(int row, int col, String value) {
        int numericValue = value.isBlank() ? 0 : Integer.parseInt(value);
        currentGrid[row][col] = numericValue;
        moves++;
        movesLabel.setText("Coups: " + moves);

        clearErrorStyles();
        Set<Position> conflicts = new HashSet<>(SudokuGrid.findConflicts(currentGrid, row, col, numericValue));
        if (!conflicts.isEmpty()) {
            fieldAt(row, col).getStyleClass().add("sudoku-cell-error");
            for (Position position : conflicts) {
                fieldAt(position.row(), position.col()).getStyleClass().add("sudoku-cell-error");
            }
        }

        updateScore();
        if (game.isSolved(currentGrid)) {
            onSolved();
        }
    }

    private void clearErrorStyles() {
        for (int r = 0; r < SudokuGrid.SIZE; r++) {
            for (int c = 0; c < SudokuGrid.SIZE; c++) {
                TextField field = cells[r][c];
                field.getStyleClass().remove("sudoku-cell-error");
            }
        }
    }

    private TextField fieldAt(int row, int col) {
        return cells[row][col];
    }

    private String sanitizeInput(String input) {
        if (input == null || input.isBlank()) {
            return "";
        }
        char ch = input.charAt(input.length() - 1);
        if (ch < '1' || ch > '9') {
            return "";
        }
        return String.valueOf(ch);
    }

    private void startTimer() {
        timerLabel.setText(formatTime(elapsedSeconds));
        timer = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            elapsedSeconds++;
            timerLabel.setText(formatTime(elapsedSeconds));
            updateScore();
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    private void updateScore() {
        int score = Math.max(0, SCORE_BASE - elapsedSeconds * SCORE_TIME_PENALTY - moves * SCORE_MOVE_PENALTY);
        scoreLabel.setText("Score: " + score);
    }

    private int computeScore() {
        return Math.max(0, SCORE_BASE - elapsedSeconds * SCORE_TIME_PENALTY - moves * SCORE_MOVE_PENALTY);
    }

    private void onSolved() {
        if (timer != null) {
            timer.stop();
        }
        int score = computeScore();
        try {
            new SudokuScoreService().saveScore(score, elapsedSeconds, moves);
            awardXpOnWin();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sudoku termine");
        alert.setHeaderText("Bravo !");
        alert.setContentText("Temps: " + formatTime(elapsedSeconds)
            + "\nCoups: " + moves
            + "\nScore: " + score);
        alert.showAndWait();
    }

    private void awardXpOnWin() throws SQLException {
        UserSession session = UserSession.getInstance();
        int currentXp = session.getCurrentXp();
        int currentLevel = session.getCurrentLevel();
        int threshold = currentLevel * 100;

        int updatedXp = currentXp + XP_PER_WIN;
        int updatedLevel = currentLevel;

        while (updatedXp >= threshold) {
            updatedXp -= threshold;
            updatedLevel++;
            threshold = updatedLevel * 100;
        }

        UserService.updateUserXp(session.getId(), updatedXp, updatedLevel);
        session.setCurrentLevel(updatedLevel);
        session.setCurrentXp(updatedXp);
    }

    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int remaining = seconds % 60;
        return String.format("%02d:%02d", minutes, remaining);
    }
}
