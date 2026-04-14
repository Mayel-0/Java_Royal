package com.example.javaroyal.games.t_or_f;

import com.example.java_royal.config.DatabaseConnection;
import com.example.java_royal.model.User;
import com.example.java_royal.service.UserService;
import com.example.java_royal.session.UserSession;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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

public class TrueFalseController {

    private static final int QUESTIONS_PER_GAME = 10;
    private static final int XP_PER_CORRECT_ANSWER = 5;

    @FXML
    private Label questionLabel;
    @FXML
    private Button trueButton;
    @FXML
    private Button falseButton;
    @FXML
    private Label scoreLabel;
    @FXML
    private Button replayButton;
    @FXML
    private TextField newQuestionField;
    @FXML
    private CheckBox newQuestionAnswer;

    private List<TrueFalseQuestion> questions;
    private int currentQuestionIndex = 0;
    private int score = 0;

    @FXML
    public void initialize() {
        loadQuestions();
        displayQuestion();
    }

    private void loadQuestions() {
        questions = new ArrayList<>();
        String sql = "SELECT question_text, correct_answer FROM true_or_false ORDER BY RANDOM()";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String text = rs.getString("question_text");
                boolean answer = rs.getInt("correct_answer") == 1;
                questions.add(new TrueFalseQuestion(text, answer));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void displayQuestion() {
        if (currentQuestionIndex < QUESTIONS_PER_GAME && currentQuestionIndex < questions.size()) {
            questionLabel.setText(questions.get(currentQuestionIndex).getText());
            trueButton.setDisable(false);
            falseButton.setDisable(false);
            trueButton.getStyleClass().removeAll("correct", "incorrect");
            falseButton.getStyleClass().removeAll("correct", "incorrect");
        } else {
            endGame();
        }
    }

    @FXML
    private void handleAnswer(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        boolean userAnswer = clickedButton == trueButton;
        boolean correctAnswer = questions.get(currentQuestionIndex).isAnswer();

        if (userAnswer == correctAnswer) {
            score++;
            clickedButton.getStyleClass().add("correct");
        } else {
            clickedButton.getStyleClass().add("incorrect");
        }
        scoreLabel.setText("Score: " + score);
        trueButton.setDisable(true);
        falseButton.setDisable(true);

        PauseTransition pause = new PauseTransition(Duration.seconds(0.8));
        pause.setOnFinished(e -> {
            currentQuestionIndex++;
            displayQuestion();
        });
        pause.play();
    }

    private void endGame() {
        int xpGained = score * XP_PER_CORRECT_ANSWER;
        questionLabel.setText("Partie terminée ! Score : " + score + "/" + QUESTIONS_PER_GAME + ". Vous gagnez " + xpGained + " XP !");
        trueButton.setVisible(false);
        falseButton.setVisible(false);
        replayButton.setVisible(true);

        updateUserXp(xpGained);
    }

    private void updateUserXp(int xpGained) {
        try {
            UserSession session = UserSession.getInstance();
            User user = UserService.getUserById(session.getId());
            if (user != null) {
                int newTotalXp = user.getTotalXp() + xpGained;
                int newLevel = user.getCurrentLevel();
                int xpForNextLevel = newLevel * 100;
                int currentLevelXp = user.getCurrentXp() + xpGained;

                while (currentLevelXp >= xpForNextLevel) {
                    currentLevelXp -= xpForNextLevel;
                    newLevel++;
                    xpForNextLevel = newLevel * 100;
                }

                UserService.updateUserXp(session.getId(), currentLevelXp, newLevel);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void replayGame() {
        currentQuestionIndex = 0;
        score = 0;
        scoreLabel.setText("Score: 0");
        Collections.shuffle(questions);
        trueButton.setVisible(true);
        falseButton.setVisible(true);
        replayButton.setVisible(false);
        displayQuestion();
    }

    @FXML
    private void handleBack() {
        try {
            Parent gameChoiceRoot = FXMLLoader.load(getClass().getResource("/com/example/java_royal/home-view.fxml"));
            Scene scene = questionLabel.getScene();
            scene.setRoot(gameChoiceRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void addQuestion() {
        String text = newQuestionField.getText();
        boolean answer = newQuestionAnswer.isSelected();
        if (text.isEmpty()) {
            return;
        }

        String sql = "INSERT INTO true_or_false(question_text, correct_answer) VALUES(?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, text);
            pstmt.setInt(2, answer ? 1 : 0);
            pstmt.executeUpdate();
            newQuestionField.clear();
            newQuestionAnswer.setSelected(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

