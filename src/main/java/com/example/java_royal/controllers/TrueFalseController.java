package com.example.java_royal.controllers;

import com.example.java_royal.config.DatabaseConnection;
import com.example.java_royal.model.TrueFalseQuestion;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class TrueFalseController {
    private static final int TARGET_QUESTIONS = 10;

    @FXML
    private BorderPane rootPane;

    @FXML
    private Label questionLabel;

    @FXML
    private Button trueButton;

    @FXML
    private Button falseButton;

    @FXML
    private Label scoreLabel;

    @FXML
    private Label progressLabel;

    @FXML
    private Label statusLabel;

    private final List<TrueFalseQuestion> questions = new ArrayList<>();
    private int currentIndex;
    private int score;
    private int elapsedSeconds;
    private boolean gameFinished;
    private Timeline timer;

    @FXML
    public void initialize() {
        applyCss();
        resetGameState();
        loadQuestions();
        startTimer();
        showCurrentQuestion();
    }

    private void resetGameState() {
        currentIndex = 0;
        score = 0;
        elapsedSeconds = 0;
        gameFinished = false;
        trueButton.getStyleClass().removeAll("correct-feedback", "wrong-feedback");
        falseButton.getStyleClass().removeAll("correct-feedback", "wrong-feedback");
    }

    private void applyCss() {
        String css = Objects.requireNonNull(getClass().getResource("/com/example/java_royal/true_false.css")).toExternalForm();
        if (!rootPane.getStylesheets().contains(css)) {
            rootPane.getStylesheets().add(css);
        }
    }

    private void loadQuestions() {
        questions.clear();

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT id, question_text, correct_answer FROM true_or_false ORDER BY RANDOM() LIMIT ?")) {
            statement.setInt(1, TARGET_QUESTIONS);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    questions.add(new TrueFalseQuestion(
                            rs.getInt("id"),
                            rs.getString("question_text"),
                            rs.getInt("correct_answer") == 1
                    ));
                }
            }

            if (questions.size() < TARGET_QUESTIONS && !questions.isEmpty()) {
                List<TrueFalseQuestion> pool = new ArrayList<>(questions);
                while (questions.size() < TARGET_QUESTIONS) {
                    Collections.shuffle(pool);
                    for (TrueFalseQuestion question : pool) {
                        if (questions.size() >= TARGET_QUESTIONS) {
                            break;
                        }
                        questions.add(question);
                    }
                }
            }

            if (questions.isEmpty()) {
                statusLabel.setText("Aucune question disponible.");
                questionLabel.setText("Ajoute une question pour commencer.");
                disableAnswerButtons(true);
            } else {
                statusLabel.setText("Reponds Vrai ou Faux !");
                disableAnswerButtons(false);
            }
        } catch (SQLException e) {
            statusLabel.setText("Erreur de chargement des questions.");
            questionLabel.setText("Impossible de charger le quiz.");
            disableAnswerButtons(true);
        }
    }

    @FXML
    private void handleTrueAnswer() {
        handleAnswer(true);
    }

    @FXML
    private void handleFalseAnswer() {
        handleAnswer(false);
    }

    private void handleAnswer(boolean userAnswer) {
        if (gameFinished || questions.isEmpty() || currentIndex >= questions.size()) {
            return;
        }

        TrueFalseQuestion current = questions.get(currentIndex);
        boolean correct = current.isCorrectAnswer() == userAnswer;

        disableAnswerButtons(true);
        trueButton.getStyleClass().removeAll("correct-feedback", "wrong-feedback");
        falseButton.getStyleClass().removeAll("correct-feedback", "wrong-feedback");

        Button clicked = userAnswer ? trueButton : falseButton;
        clicked.getStyleClass().add(correct ? "correct-feedback" : "wrong-feedback");

        if (correct) {
            score++;
            statusLabel.setText("Bonne reponse !");
        } else {
            statusLabel.setText("Mauvaise reponse.");
        }

        updateHud();

        PauseTransition pause = new PauseTransition(Duration.seconds(0.8));
        pause.setOnFinished(event -> {
            clicked.getStyleClass().removeAll("correct-feedback", "wrong-feedback");
            currentIndex++;
            showCurrentQuestion();
        });
        pause.play();
    }

    private void showCurrentQuestion() {
        if (questions.isEmpty()) {
            return;
        }

        if (currentIndex >= questions.size()) {
            gameFinished = true;
            if (timer != null) {
                timer.stop();
            }
            questionLabel.setText("Quiz termine !");
            statusLabel.setText("Score final : " + score + "/" + questions.size());
            disableAnswerButtons(true);
            updateHud();
            return;
        }

        questionLabel.setText(questions.get(currentIndex).getQuestionText());
        disableAnswerButtons(false);
        updateHud();
    }

    private void startTimer() {
        if (timer != null) {
            timer.stop();
        }
        elapsedSeconds = 0;
        timer = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            elapsedSeconds++;
            updateHud();
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    private void updateHud() {
        int total = questions.isEmpty() ? TARGET_QUESTIONS : questions.size();
        int questionNumber = Math.min(currentIndex + 1, total);
        int minutes = elapsedSeconds / 60;
        int seconds = elapsedSeconds % 60;

        scoreLabel.setText("Score : " + score + "/" + total);
        progressLabel.setText("Question " + questionNumber + "/" + total + "   Temps : " + String.format("%02d:%02d", minutes, seconds));
    }

    private void disableAnswerButtons(boolean disabled) {
        trueButton.setDisable(disabled);
        falseButton.setDisable(disabled);
    }

    @FXML
    private void handleAddQuestion() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Ajouter une question");
        dialog.setHeaderText("Nouvelle question Vrai/Faux");

        TextField questionField = new TextField();
        questionField.setPromptText("Texte de la question");
        CheckBox trueCheckBox = new CheckBox("La bonne reponse est Vrai");

        VBox content = new VBox(10, questionField, trueCheckBox);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(result -> {
            if (result != ButtonType.OK) {
                return;
            }

            String questionText = questionField.getText() == null ? "" : questionField.getText().trim();
            if (questionText.isEmpty()) {
                statusLabel.setText("Question vide: enregistrement annule.");
                return;
            }

            try (Connection connection = DatabaseConnection.getInstance().getConnection();
                 PreparedStatement statement = connection.prepareStatement(
                         "INSERT INTO true_or_false(question_text, correct_answer) VALUES (?, ?)")) {
                statement.setString(1, questionText);
                statement.setInt(2, trueCheckBox.isSelected() ? 1 : 0);
                statement.executeUpdate();
                statusLabel.setText("Question ajoutee avec succes.");
            } catch (SQLException e) {
                statusLabel.setText("Insertion impossible (question deja existante ?).");
            }
        });
    }

    @FXML
    private void handleReplay() {
        resetGameState();
        loadQuestions();
        startTimer();
        showCurrentQuestion();
        statusLabel.setText("Nouvelle partie lancee !");
    }

    @FXML
    private void backToHome() {
        try {
            if (timer != null) {
                timer.stop();
            }
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/java_royal/home-view.fxml"));
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Accueil");
            stage.show();
        } catch (IOException e) {
            statusLabel.setText("Impossible de revenir a l'accueil.");
        }
    }
}

