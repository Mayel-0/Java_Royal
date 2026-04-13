package com.example.java_royal.controllers;

import com.example.java_royal.model.Card;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MemoryController {
    private static final int COLUMNS = 6;
    private static final int ROWS = 4;
    private static final int TOTAL_PAIRS = 12;
    private static final String CARD_BACK_PATH = "/assets/Memory/Memory-Card-Back.png";

    @FXML
    private BorderPane rootPane;

    @FXML
    private GridPane memoryGrid;

    @FXML
    private Label movesLabel;

    @FXML
    private Label timeLabel;

    @FXML
    private Label statusLabel;

    private final List<CardNode> selectedCards = new ArrayList<>();
    private int moves;
    private int elapsedSeconds;
    private int matchedPairs;
    private boolean boardLocked;
    private Timeline timer;

    @FXML
    public void initialize() {
        applyCss();
        setupNewGame();
        startTimer();
    }

    private void applyCss() {
        String css = Objects.requireNonNull(getClass().getResource("/com/example/java_royal/memory.css")).toExternalForm();
        if (!rootPane.getStylesheets().contains(css)) {
            rootPane.getStylesheets().add(css);
        }
    }

    private void setupNewGame() {
        selectedCards.clear();
        boardLocked = false;
        moves = 0;
        elapsedSeconds = 0;
        matchedPairs = 0;
        memoryGrid.getChildren().clear();
        updateMovesLabel();
        updateTimerLabel();
        statusLabel.setText("Trouve les 12 paires !");

        List<Card> deck = buildDeck();
        int cardIndex = 0;

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                CardNode cardNode = createCardNode(deck.get(cardIndex));
                memoryGrid.add(cardNode.container(), col, row);
                cardIndex++;
            }
        }
    }

    private List<Card> buildDeck() {
        List<String> imagePool = new ArrayList<>(List.of(
                "/assets/Memory/Balloon_card.png",
                "/assets/Memory/Barbarian_card.png",
                "/assets/Memory/Bats_card.png",
                "/assets/Memory/Bowler_card.png",
                "/assets/Memory/Dart_Goblin_card.png",
                "/assets/Memory/Giant_card.png",
                "/assets/Memory/Golem_card.png",
                "/assets/Memory/Hog_Rider_card.png",
                "/assets/Memory/Lava_Hound_card.png",
                "/assets/Memory/Mini_Pekka_card.png",
                "/assets/Memory/Minion_card.png",
                "/assets/Memory/PEKKA_card.png",
                "/assets/Memory/Prince_card.png",
                "/assets/Memory/Valkyrie- card.png",
                "/assets/Memory/Witch_card.png",
                "/assets/Memory/Wizard_card.png"
        ));

        Collections.shuffle(imagePool);

        List<Card> deck = new ArrayList<>();
        for (int i = 0; i < TOTAL_PAIRS; i++) {
            String path = imagePool.get(i);
            deck.add(new Card(i, path));
            deck.add(new Card(i, path));
        }

        Collections.shuffle(deck);
        return deck;
    }

    private CardNode createCardNode(Card card) {
        ImageView backView = createCardImage(CARD_BACK_PATH);
        ImageView frontView = createCardImage(card.getImagePath());
        frontView.setVisible(false);

        StackPane container = new StackPane(backView, frontView);
        CardNode cardNode = new CardNode(card, container, backView, frontView);
        container.getStyleClass().add("memory-card");
        container.setOnMouseClicked(event -> handleCardClick(cardNode));

        return cardNode;
    }

    private ImageView createCardImage(String resourcePath) {
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(resourcePath)));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(110);
        imageView.setFitHeight(140);
        imageView.setPreserveRatio(false);
        imageView.setSmooth(true);
        return imageView;
    }

    private void handleCardClick(CardNode cardNode) {
        if (boardLocked || cardNode.card().isMatched() || cardNode.card().isFlipped()) {
            return;
        }

        flipCard(cardNode, true);
        selectedCards.add(cardNode);

        if (selectedCards.size() < 2) {
            return;
        }

        moves++;
        updateMovesLabel();
        evaluatePair();
    }

    private void evaluatePair() {
        CardNode first = selectedCards.get(0);
        CardNode second = selectedCards.get(1);

        if (first.card().getPairId() == second.card().getPairId()) {
            first.card().setMatched(true);
            second.card().setMatched(true);
            matchedPairs++;
            first.container().getStyleClass().add("memory-card-matched");
            second.container().getStyleClass().add("memory-card-matched");
            selectedCards.clear();
            checkVictory();
            return;
        }

        boardLocked = true;
        first.container().getStyleClass().add("memory-card-mismatch");
        second.container().getStyleClass().add("memory-card-mismatch");

        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(event -> {
            flipCard(first, false);
            flipCard(second, false);
            first.container().getStyleClass().remove("memory-card-mismatch");
            second.container().getStyleClass().remove("memory-card-mismatch");
            selectedCards.clear();
            boardLocked = false;
        });
        pause.play();
    }

    private void flipCard(CardNode cardNode, boolean revealFront) {
        cardNode.card().setFlipped(revealFront);

        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(120), cardNode.container());
        scaleOut.setFromX(1);
        scaleOut.setToX(0);

        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(120), cardNode.container());
        scaleIn.setFromX(0);
        scaleIn.setToX(1);

        scaleOut.setOnFinished(event -> {
            cardNode.frontView().setVisible(revealFront);
            cardNode.backView().setVisible(!revealFront);
        });

        new SequentialTransition(scaleOut, scaleIn).play();
    }

    private void checkVictory() {
        if (matchedPairs == TOTAL_PAIRS) {
            if (timer != null) {
                timer.stop();
            }
            statusLabel.setText("Victoire ! " + moves + " coups en " + timeLabel.getText() + ".");
        }
    }

    private void startTimer() {
        if (timer != null) {
            timer.stop();
        }

        timer = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            elapsedSeconds++;
            updateTimerLabel();
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    private void updateMovesLabel() {
        movesLabel.setText("Coups : " + moves);
    }

    private void updateTimerLabel() {
        int minutes = elapsedSeconds / 60;
        int seconds = elapsedSeconds % 60;
        timeLabel.setText(String.format("%02d:%02d", minutes, seconds));
    }

    @FXML
    private void handleRestart() {
        setupNewGame();
        startTimer();
    }

    @FXML
    private void handleBackToHome() {
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

    private record CardNode(Card card, StackPane container, ImageView backView, ImageView frontView) {
    }
}


