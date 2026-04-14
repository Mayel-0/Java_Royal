package com.example.java_royal.controllers;

import com.example.java_royal.model.Card;
import com.example.java_royal.model.DifficultyLevel;
import com.example.java_royal.service.UserService;
import com.example.java_royal.session.UserSession;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MemoryController {
    private static final String CARD_BACK_PATH = "/assets/Memory/Memory-Card-Back.png";
    private static final double EASY_CARD_WIDTH = 130;
    private static final double EASY_CARD_HEIGHT = 165;
    private static final double MEDIUM_CARD_WIDTH = 120;
    private static final double MEDIUM_CARD_HEIGHT = 152;
    private static final double HARD_CARD_WIDTH = 105;
    private static final double HARD_CARD_HEIGHT = 134;
    private static final double DEFAULT_FRONT_ZOOM = 1.12;
    private static final double REDUCED_FRONT_ZOOM = 1.05;

    // Gains d'XP par difficulté
    private static final int XP_EASY = 50;
    private static final int XP_MEDIUM = 80;
    private static final int XP_HARD = 100;

    private int columns;
    private int rows;
    private int totalPairs;
    private double cardWidth;
    private double cardHeight;
    private double gridGap;
    private DifficultyLevel currentDifficulty;

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

    @FXML
    private Label victoryLabel;

    @FXML
    private ComboBox<DifficultyLevel> difficultyCombo;

    private final List<CardNode> selectedCards = new ArrayList<>();
    private int moves;
    private int elapsedSeconds;
    private int matchedPairs;
    private boolean boardLocked;
    private Timeline timer;

    @FXML
    public void initialize() {
        applyCss();
        initializeDifficultyCombo();
        setupNewGame();
        startTimer();
    }

    private void initializeDifficultyCombo() {
        difficultyCombo.getItems().addAll(DifficultyLevel.values());
        difficultyCombo.setValue(DifficultyLevel.EASY);
        currentDifficulty = DifficultyLevel.EASY;
        updateGridDimensions(DifficultyLevel.EASY);
        difficultyCombo.setOnAction(event -> handleDifficultyChange());
    }

    private void handleDifficultyChange() {
        DifficultyLevel selected = difficultyCombo.getValue();
        if (selected == null) {
            return;
        }

        currentDifficulty = selected;
        updateGridDimensions(selected);
        memoryGrid.getChildren().clear();
        elapsedSeconds = 0;
        moves = 0;
        matchedPairs = 0;
        selectedCards.clear();
        boardLocked = false;
        setupNewGame();
        updateTimerLabel();
        updateMovesLabel();
        startTimer();
    }

    private void updateGridDimensions(DifficultyLevel level) {
        this.columns = level.getColumns();
        this.rows = level.getRows();
        this.totalPairs = (columns * rows) / 2;

        switch (level) {
            case EASY -> {
                cardWidth = EASY_CARD_WIDTH;
                cardHeight = EASY_CARD_HEIGHT;
                gridGap = 14;
            }
            case MEDIUM -> {
                cardWidth = MEDIUM_CARD_WIDTH;
                cardHeight = MEDIUM_CARD_HEIGHT;
                gridGap = 12;
            }
            case HARD -> {
                cardWidth = HARD_CARD_WIDTH;
                cardHeight = HARD_CARD_HEIGHT;
                gridGap = 10;
            }
        }
    }

    private void applyCss() {
        String css = Objects.requireNonNull(getClass().getResource("/com/example/java_royal/memory.css")).toExternalForm();
        if (!rootPane.getStylesheets().contains(css)) {
            rootPane.getStylesheets().add(css);
        }
    }

    private void setupNewGame() {
        DifficultyLevel selected = difficultyCombo != null && difficultyCombo.getValue() != null
                ? difficultyCombo.getValue()
                : DifficultyLevel.EASY;
        currentDifficulty = selected;
        updateGridDimensions(selected);

        selectedCards.clear();
        boardLocked = false;
        memoryGrid.getChildren().clear();
        updateMovesLabel();
        updateTimerLabel();
        statusLabel.setText("Trouve les " + totalPairs + " paires !");
        victoryLabel.setVisible(false);
        victoryLabel.setManaged(false);
        memoryGrid.setHgap(gridGap);
        memoryGrid.setVgap(gridGap);

        List<Card> deck = buildDeck();
        int cardIndex = 0;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                if (cardIndex >= deck.size()) {
                    return;
                }
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
                "/assets/Memory/Valkyrie_card.png",
                "/assets/Memory/Witch_card.png",
                "/assets/Memory/Wizard_card.png"
        ));

        Collections.shuffle(imagePool);

        List<Card> deck = new ArrayList<>();
        for (int i = 0; i < totalPairs; i++) {
            String path = imagePool.get(i);
            deck.add(new Card(i, path));
            deck.add(new Card(i, path));
        }

        Collections.shuffle(deck);
        return deck;
    }

    private CardNode createCardNode(Card card) {
        try {
            ImageView backView = createCardImage(CARD_BACK_PATH, 1.0);
            ImageView frontView = createCardImage(card.getImagePath(), resolveFrontZoom(card.getImagePath()));
            frontView.setVisible(false);

            StackPane container = new StackPane(backView, frontView);
            container.setPrefSize(cardWidth, cardHeight);
            container.setMinSize(cardWidth, cardHeight);
            container.setMaxSize(cardWidth, cardHeight);
            // Le clip garde les proportions tout en autorisant un leger zoom centre.
            Rectangle clip = new Rectangle(cardWidth, cardHeight);
            clip.setArcWidth(16);
            clip.setArcHeight(16);
            container.setClip(clip);

            CardNode cardNode = new CardNode(card, container, backView, frontView);
            container.getStyleClass().add("memory-card");
            container.setOnMouseClicked(event -> handleCardClick(cardNode));

            return cardNode;
        } catch (Exception e) {
            System.err.println("ERROR creating card with image: " + card.getImagePath());
            e.printStackTrace();
            throw new RuntimeException("Impossible de créer la carte", e);
        }
    }

    private ImageView createCardImage(String resourcePath, double zoomFactor) {
        Image image = new Image(openImageStream(resourcePath));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(cardWidth);
        imageView.setFitHeight(cardHeight);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setScaleX(zoomFactor);
        imageView.setScaleY(zoomFactor);
        return imageView;
    }

    private InputStream openImageStream(String resourcePath) {
        InputStream stream = getClass().getResourceAsStream(resourcePath);
        if (stream != null) {
            return stream;
        }
        InputStream fallback = getClass().getResourceAsStream(CARD_BACK_PATH);
        if (fallback != null) {
            return fallback;
        }
        throw new IllegalStateException("Aucune image chargeable pour la carte: " + resourcePath);
    }

    private double resolveFrontZoom(String imagePath) {
        String normalized = imagePath.toLowerCase();
        if (normalized.contains("mini_pekka")
                || normalized.contains("giant_card")
                || normalized.contains("pekka_card")
                || normalized.contains("witch_card")
                || normalized.contains("bats_card")
                || normalized.contains("golem_card")) {
            return REDUCED_FRONT_ZOOM;
        }
        return DEFAULT_FRONT_ZOOM;
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
        if (matchedPairs == totalPairs) {
            if (timer != null) {
                timer.stop();
            }
            boardLocked = true;
            String difficulty = currentDifficulty.getLabel();
            String message = "Victoire ! (" + difficulty + ") " + moves + " coups en " + timeLabel.getText() + ".";
            statusLabel.setText(message);
            victoryLabel.setText("Bravo !\n" + message);
            victoryLabel.setManaged(true);
            victoryLabel.setVisible(true);

            // Attribuer l'XP en fonction de la difficulté
            awardXpOnWin();
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
        elapsedSeconds = 0;
        moves = 0;
        matchedPairs = 0;
        selectedCards.clear();
        boardLocked = false;
        setupNewGame();
        updateTimerLabel();
        updateMovesLabel();
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

    private void awardXpOnWin() {
        try {
            UserSession session = UserSession.getInstance();

            // Vérifier que l'utilisateur est connecté
            if (session.getId() == 0) {
                System.out.println("[MemoryController] Session utilisateur non trouvée, impossible d'ajouter l'XP.");
                return;
            }

            // Déterminer l'XP à ajouter selon la difficulté
            int xpGained = switch (currentDifficulty) {
                case EASY -> XP_EASY;
                case MEDIUM -> XP_MEDIUM;
                case HARD -> XP_HARD;
            };

            int currentXp = session.getCurrentXp();
            int currentLevel = session.getCurrentLevel();
            int threshold = currentLevel * 100;

            int updatedXp = currentXp + xpGained;
            int updatedLevel = currentLevel;

            // Calculer les niveaux et l'XP restant après level up
            while (updatedXp >= threshold) {
                updatedXp -= threshold;
                updatedLevel++;
                threshold = updatedLevel * 100;
            }

            // Mettre à jour la base de données
            UserService.updateUserXp(session.getId(), updatedXp, updatedLevel);

            // Mettre à jour la session
            session.setCurrentLevel(updatedLevel);
            session.setCurrentXp(updatedXp);

            System.out.println("[MemoryController] ✅ XP attribué: " + xpGained + " XP (Difficulté: " + currentDifficulty.getLabel() + ")");
            if (updatedLevel > currentLevel) {
                System.out.println("[MemoryController] 🎉 Level up! Nouveau niveau: " + updatedLevel);
            }
        } catch (SQLException e) {
            System.err.println("[MemoryController] ❌ Erreur lors de la mise à jour de l'XP: " + e.getMessage());
            e.printStackTrace();
            statusLabel.setText("Impossible de mettre à jour l'XP.");
        }
    }

    private record CardNode(Card card, StackPane container, ImageView backView, ImageView frontView) {
    }
}

