package com.example.java_royal.controllers;

import com.javaroyal.games.hangman.HangmanDifficulty;
import com.javaroyal.games.hangman.HangmanGameEngine;
import com.javaroyal.games.hangman.HangmanWord;
import com.javaroyal.games.hangman.HangmanWordBank;
import com.example.java_royal.service.HangmanScoreService;
import com.example.java_royal.service.UserService;
import com.example.java_royal.session.UserSession;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class HangmanController {

    @FXML
    private BorderPane rootPane;

    @FXML
    private Label difficultyInfoLabel;

    @FXML
    private Label roundLabel;

    @FXML
    private Label scoreLabel;

    @FXML
    private Label bestScoreLabel;

    @FXML
    private Label livesLabel;

    @FXML
    private Label timerLabel;

    @FXML
    private Label comboLabel;

    @FXML
    private Label categoryLabel;

    @FXML
    private Label hintLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private Label wordLabel;

    @FXML
    private Label wrongLettersLabel;

    @FXML
    private ProgressBar hangmanProgressBar;

    @FXML
    private ChoiceBox<String> difficultyChoice;

    @FXML
    private FlowPane keyboardPane;

    @FXML
    private Button hintButton;

    @FXML
    private Canvas hangmanCanvas;

    private final Random random = new Random();
    private final HangmanWordBank wordBank = new HangmanWordBank();
    private final Map<Character, Button> keyboardButtons = new HashMap<>();

    private Timeline timer;
    private int remainingSeconds;
    private int totalScore;
    private int bestScore;
    private int roundCounter;
    private int bestCombo;

    private HangmanDifficulty selectedDifficulty;
    private HangmanGameEngine engine;

    private static final int XP_PER_WIN = 100;

    @FXML
    public void initialize() {
        applyCss();
        configureDifficultyChoice();
        createKeyboard();
        applyBackgroundImage();
        startNewRound();

        Platform.runLater(this::bindPhysicalKeyboard);
    }

    private void applyCss() {
        String css = Objects.requireNonNull(getClass().getResource("/com/example/java_royal/pendu.css")).toExternalForm();
        if (!rootPane.getStylesheets().contains(css)) {
            rootPane.getStylesheets().add(css);
        }
    }

    private void configureDifficultyChoice() {
        for (HangmanDifficulty difficulty : HangmanDifficulty.values()) {
            difficultyChoice.getItems().add(difficulty.getLabel());
        }
        difficultyChoice.setValue(HangmanDifficulty.NORMAL.getLabel());
        selectedDifficulty = HangmanDifficulty.NORMAL;
    }

    private void createKeyboard() {
        keyboardPane.getChildren().clear();
        keyboardButtons.clear();

        for (char c = 'A'; c <= 'Z'; c++) {
            Button letterButton = new Button(String.valueOf(c));
            letterButton.getStyleClass().add("letter-button");
            letterButton.setMinWidth(44);
            letterButton.setPrefWidth(44);
            letterButton.setPrefHeight(42);
            char current = c;
            letterButton.setOnAction(event -> processGuess(current));
            keyboardButtons.put(c, letterButton);
            keyboardPane.getChildren().add(letterButton);
        }
    }

    private void bindPhysicalKeyboard() {
        Scene scene = rootPane.getScene();
        if (scene == null) {
            return;
        }

        scene.setOnKeyPressed(event -> {
            if (engine == null || engine.isFinished()) {
                return;
            }

            if (event.getCode() == KeyCode.ENTER) {
                startNewRound();
                return;
            }

            String text = event.getText();
            if (text == null || text.isBlank()) {
                return;
            }

            char letter = Character.toUpperCase(text.charAt(0));
            if (letter >= 'A' && letter <= 'Z') {
                processGuess(letter);
            }
        });
    }

    private void startNewRound() {
        if (timer != null) {
            timer.stop();
        }

        selectedDifficulty = HangmanDifficulty.fromLabel(difficultyChoice.getValue());
        HangmanWord word = wordBank.pickRandomWord(selectedDifficulty, random);
        engine = new HangmanGameEngine(word, selectedDifficulty);

        roundCounter++;
        remainingSeconds = selectedDifficulty.getRoundSeconds();

        resetKeyboardAvailability();
        updateDifficultyInfo();
        updateHud();
        startTimer();

        statusLabel.setText("Trouve le mot avant la fin du chrono.");
        hintLabel.setText("Indice: ???");
        hintButton.setDisable(false);
    }

    private void startTimer() {
        updateTimerLabel();

        timer = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            remainingSeconds--;
            updateTimerLabel();

            if (remainingSeconds <= 0) {
                timer.stop();
                onTimeExpired();
            }
        }));

        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    private void processGuess(char letter) {
        if (engine == null || engine.isFinished()) {
            return;
        }

        Button keyButton = keyboardButtons.get(letter);
        if (keyButton != null) {
            keyButton.setDisable(true);
        }

        HangmanGameEngine.GuessResult result = engine.guess(letter);

        switch (result) {
            case INVALID -> statusLabel.setText("Lettre invalide.");
            case ALREADY_USED -> statusLabel.setText("Lettre deja proposee.");
            case CORRECT -> statusLabel.setText("Bien joue, la lettre " + letter + " est presente.");
            case INCORRECT -> statusLabel.setText("La lettre " + letter + " n'est pas dans le mot.");
            case ROUND_WON -> {
                statusLabel.setText("Victoire ! Mot trouve: " + engine.getTargetWord() + ".");
                finishRound(true);
            }
            case ROUND_LOST -> {
                statusLabel.setText("Defaite. Le mot etait: " + engine.getTargetWord() + ".");
                finishRound(false);
            }
        }

        updateHud();
    }

    private void onTimeExpired() {
        if (engine == null || engine.isFinished()) {
            return;
        }

        statusLabel.setText("Temps ecoule ! Mot: " + engine.getTargetWord() + ".");
        finishRound(false);
        updateHud();
    }

    private void finishRound(boolean won) {
        disableKeyboard();
        hintButton.setDisable(true);

        if (timer != null) {
            timer.stop();
        }

        if (won) {
            totalScore += engine.getScore();
            bestScore = Math.max(bestScore, totalScore);
            bestCombo = Math.max(bestCombo, engine.getCombo());
            awardXpOnWin();
        }

        scoreLabel.setText("Score total: " + totalScore);
        bestScoreLabel.setText("Meilleur score: " + bestScore);
        comboLabel.setText("Combo: " + engine.getCombo());

        saveHangmanScore();
    }

    private void awardXpOnWin() {
        try {
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
        } catch (Exception e) {
            statusLabel.setText("Impossible de mettre a jour l'XP.");
        }
    }

    private void updateHud() {
        if (engine == null) {
            return;
        }

        wordLabel.setText(engine.getDisplayWord());
        categoryLabel.setText("Categorie: " + engine.getCategory());
        wrongLettersLabel.setText("Erreurs: " + engine.getWrongLettersDisplay());
        roundLabel.setText("Manche: " + roundCounter);

        int maxLives = engine.getMaxLives();
        int remainingLives = engine.getRemainingLives();
        livesLabel.setText("Vies: " + remainingLives + " / " + maxLives);

        double progress = (double) (maxLives - remainingLives) / maxLives;
        hangmanProgressBar.setProgress(progress);
        drawHangman(progress);

        comboLabel.setText("Combo: " + engine.getCombo());
        bestScoreLabel.setText("Meilleur score: " + bestScore);

        for (char c = 'A'; c <= 'Z'; c++) {
            Button button = keyboardButtons.get(c);
            if (button != null) {
                button.setDisable(engine.isLetterUsed(c) || engine.isFinished());
            }
        }
    }

    private void drawHangman(double progress) {
        if (hangmanCanvas == null) {
            return;
        }

        GraphicsContext gc = hangmanCanvas.getGraphicsContext2D();
        double width = hangmanCanvas.getWidth();
        double height = hangmanCanvas.getHeight();

        gc.clearRect(0, 0, width, height);
        gc.setStroke(Color.web("#dce9ff"));
        gc.setLineWidth(4);

        // Base
        gc.strokeLine(width * 0.15, height * 0.90, width * 0.85, height * 0.90);
        // Pole
        gc.strokeLine(width * 0.25, height * 0.90, width * 0.25, height * 0.10);
        // Beam
        gc.strokeLine(width * 0.25, height * 0.10, width * 0.65, height * 0.10);
        // Rope
        gc.strokeLine(width * 0.65, height * 0.10, width * 0.65, height * 0.20);

        int stage = (int) Math.ceil(progress * 6);
        gc.setStroke(Color.web("#ffd54a"));
        gc.setLineWidth(5);

        if (stage >= 1) {
            gc.strokeOval(width * 0.60, height * 0.20, width * 0.10, height * 0.10);
        }
        if (stage >= 2) {
            gc.strokeLine(width * 0.65, height * 0.30, width * 0.65, height * 0.55);
        }
        if (stage >= 3) {
            gc.strokeLine(width * 0.65, height * 0.36, width * 0.55, height * 0.46);
        }
        if (stage >= 4) {
            gc.strokeLine(width * 0.65, height * 0.36, width * 0.75, height * 0.46);
        }
        if (stage >= 5) {
            gc.strokeLine(width * 0.65, height * 0.55, width * 0.58, height * 0.72);
        }
        if (stage >= 6) {
            gc.strokeLine(width * 0.65, height * 0.55, width * 0.72, height * 0.72);
        }
    }

    private void saveHangmanScore() {
        try {
            new HangmanScoreService().saveScore(totalScore, bestScore, roundCounter);
        } catch (Exception e) {
            statusLabel.setText("Impossible de sauvegarder le score du pendu.");
        }
    }

    private void updateDifficultyInfo() {
        difficultyInfoLabel.setText(
                selectedDifficulty.getLabel()
                        + " | "
                        + selectedDifficulty.getMaxLives() + " vies"
                        + " | "
                        + selectedDifficulty.getRoundSeconds() + " sec"
        );
    }

    private void updateTimerLabel() {
        int minutes = remainingSeconds / 60;
        int seconds = remainingSeconds % 60;
        timerLabel.setText(String.format("Temps: %02d:%02d", minutes, seconds));
    }

    private void disableKeyboard() {
        for (Button button : keyboardButtons.values()) {
            button.setDisable(true);
        }
    }

    private void resetKeyboardAvailability() {
        for (Button button : keyboardButtons.values()) {
            button.setDisable(false);
        }
    }


    private void applyBackgroundImage() {
        Path backgroundPath = Paths.get("assets", "pendu", "pendu-background.png");
        if (!Files.exists(backgroundPath)) {
            return;
        }

        String uri = backgroundPath.toAbsolutePath().toUri().toString();
        rootPane.setStyle(
                "-fx-background-image: url('" + uri + "');"
                        + "-fx-background-size: cover;"
                        + "-fx-background-position: center center;"
                        + "-fx-background-repeat: no-repeat;"
        );
    }

    @FXML
    private void handleDifficultyChanged() {
        selectedDifficulty = HangmanDifficulty.fromLabel(difficultyChoice.getValue());
        updateDifficultyInfo();
        statusLabel.setText("Difficulte modifiee. Lance une nouvelle manche.");
    }

    @FXML
    private void handleUseHint() {
        if (engine == null || engine.isFinished()) {
            return;
        }

        if (engine.isHintUsed()) {
            statusLabel.setText("Indice deja utilise pour cette manche.");
            hintButton.setDisable(true);
            return;
        }

        boolean used = engine.useHint(random);
        if (used) {
            hintLabel.setText("Indice: " + engine.getHint());
            statusLabel.setText("Indice active, un caractere a ete revele.");
            hintButton.setDisable(true);
            updateHud();

            if (engine.isFinished() && engine.isWon()) {
                statusLabel.setText("Victoire ! Mot trouve: " + engine.getTargetWord() + ".");
                finishRound(true);
            }
        } else {
            statusLabel.setText("Impossible d'utiliser un indice maintenant.");
        }
    }

    @FXML
    private void handleNewRound() {
        startNewRound();
    }

    @FXML
    private void handleBackHome() {
        if (timer != null) {
            timer.stop();
        }

        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/java_royal/home-view.fxml"));
            Stage stage = (Stage) ((Region) rootPane).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Accueil");
            stage.show();
        } catch (IOException e) {
            statusLabel.setText("Impossible de revenir a l'accueil.");
        }
    }
}
