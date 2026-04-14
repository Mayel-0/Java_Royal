package com.javaroyal.games.flappy;

import com.example.java_royal.service.FlappyScoreService;
import com.example.java_royal.util.SceneNavigator;
import javafx.animation.AnimationTimer;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Pos;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class FlappyBirdGame extends StackPane {

    private final DoubleProperty gameWidth = new SimpleDoubleProperty();
    private final DoubleProperty gameHeight = new SimpleDoubleProperty();

    private double getGameWidth() { return gameWidth.get(); }
    private double getGameHeight() { return gameHeight.get(); }

    private static final double FLOOR_HEIGHT = 110;
    private double getGroundY() { return getGameHeight() - FLOOR_HEIGHT; }
    private static final double BIRD_SIZE = 108;
    private static final double BIRD_START_X = 300;
    private double getBirdStartY() { return getGameHeight() * 0.45; }
    private static final double GRAVITY = 980;
    private static final double FLAP_VELOCITY = -360;
    private static final double BASE_SCROLL_SPEED = 212;
    private static final double SPAWN_INTERVAL = 1.45;
    private static final double GAP_SIZE = 182;
    private static final double OBSTACLE_WIDTH = 132;
    private static final double TOP_MARGIN = 84;
    private static final double BOTTOM_MARGIN = 140;
    private static final int ALPHA_THRESHOLD = 18;

    private enum GameMode {
        DETENTE("Detente", 900, -365, 360, 1.70, 218, 4.2),
        CLASSIQUE("Classique", 980, -380, 420, 1.45, 184, 5.0),
        CHAMPION("Champion", 1120, -405, 485, 1.12, 160, 6.0);

        private final String title;
        private final double gravity;
        private final double flapVelocity;
        private final double baseSpeed;
        private final double spawnInterval;
        private final double gapSize;
        private final double speedPerScore;

        GameMode(
                String title,
                double gravity,
                double flapVelocity,
                double baseSpeed,
                double spawnInterval,
                double gapSize,
                double speedPerScore
        ) {
            this.title = title;
            this.gravity = gravity;
            this.flapVelocity = flapVelocity;
            this.baseSpeed = baseSpeed;
            this.spawnInterval = spawnInterval;
            this.gapSize = gapSize;
            this.speedPerScore = speedPerScore;
        }
    }

    private final Pane playfield = new Pane();
    private final List<ObstaclePair> obstacles = new ArrayList<>();
    private final Random random = new Random();

    private final Image backgroundImage = loadImage("assets", "flappy-bird", "flappy-bird-background.png");
    private final Image birdImage = loadImage("assets", "flappy-bird", "flappy-bird-Balloon.png");
    private final Image[] obstacleImages = new Image[] {
            loadImage("assets", "flappy-bird", "obstacles", "flappy-bird-prince.png"),
            loadImage("assets", "flappy-bird", "obstacles", "flappy-bird-TDE.png"),
            loadImage("assets", "flappy-bird", "obstacles", "flappy-bird-tesla.png")
    };

    private final ImageView backgroundView;
    private final ImageView birdView = createImageView(birdImage, BIRD_SIZE, BIRD_SIZE, false);

    private final Label scoreLabel = createLabel("0", 44, Color.web("#ffd54a"));
    private final Label helpLabel = createLabel("Espace / clic : voler   |   R : recommencer   |   M : menu", 16, Color.web("#f3f0d7"));
    private final Label modeLabel = createLabel("Mode: Classique", 20, Color.web("#d3e8ff"));

    private final Pane overlay = new Pane();
    private final Rectangle overlayBackdrop;
    private final VBox overlayCard = new VBox(14);
    private final Label overlayTitle = createLabel("GAME OVER", 34, Color.web("#ffd54a"));
    private final Label overlayScore = createLabel("Score : 0", 20, Color.web("#f4f4f4"));
    private final Button restartButton = createButton("Rejouer");
    private final Button changeModeButton = createButton("Changer de mode");
    private final Button quitButton = createButton("Quitter");

    private final Pane modeMenu = new Pane();
    private final Rectangle modeMenuBackdrop;
    private final VBox modeMenuCard = new VBox(12);
    private final Label modeMenuTitle = createLabel("Flappy Royale", 42, Color.web("#ffd54a"));
    private final Label modeMenuSubtitle = createLabel("Choisis ton mode", 20, Color.web("#f3f0d7"));
    private final Button detenteButton = createButton("Mode Detente");
    private final Button classiqueButton = createButton("Mode Classique");
    private final Button championButton = createButton("Mode Champion");
    private final Button backToHomeButton = createButton("Retour à l'accueil");

    private final AnimationTimer timer;

    private boolean running;
    private boolean gameOver;
    private double birdVelocityY;
    private double spawnAccumulator;
    private double score;
    private long lastFrameNanos = -1L;
    private GameMode currentMode = GameMode.CLASSIQUE;
    private boolean nextGapHigh;
    private double previousGapCenter;

    public FlappyBirdGame() {
        gameWidth.bind(this.widthProperty());
        gameHeight.bind(this.heightProperty());

        backgroundView = createImageView(backgroundImage, 0, 0, false);
        backgroundView.fitWidthProperty().bind(gameWidth);
        backgroundView.fitHeightProperty().bind(gameHeight);

        overlayBackdrop = new Rectangle(0, 0, Color.rgb(9, 14, 28, 0));
        overlayBackdrop.widthProperty().bind(gameWidth);
        overlayBackdrop.heightProperty().bind(gameHeight);

        modeMenuBackdrop = new Rectangle(0, 0, Color.rgb(9, 14, 28, 0));
        modeMenuBackdrop.widthProperty().bind(gameWidth);
        modeMenuBackdrop.heightProperty().bind(gameHeight);

        configurePlayfield();
        configureOverlay();
        configureModeMenu();
        configureInput();

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateFrame(now);
            }
        };

        showModeMenu();
        timer.start();
    }

    public FlappyBirdGame(double ignoredWidth, double ignoredHeight) {
        this();
    }

    private void resetGame() {
        restartGame();
    }

    private void startWithMode(GameMode mode) {
        currentMode = mode;
        modeLabel.setText("Mode: " + currentMode.title);
        modeMenuBackdrop.setVisible(false);
        modeMenuCard.setVisible(false);
        restartGame();
    }

    private void showModeMenu() {
        clearObstacles();
        running = false;
        gameOver = false;
        score = 0;
        birdVelocityY = 0;
        spawnAccumulator = 0;
        lastFrameNanos = -1L;

        birdView.setLayoutX(BIRD_START_X);
        birdView.setLayoutY(getBirdStartY());
        birdView.setRotate(0);
        previousGapCenter = getBirdStartY();
        nextGapHigh = random.nextBoolean();

        scoreLabel.setText("0");
        overlayBackdrop.setVisible(false);
        overlayCard.setVisible(false);
        modeMenuBackdrop.setVisible(true);
        modeMenuCard.setVisible(true);
        requestFocus();
    }

    private void configurePlayfield() {
        styleProperty().bind(Bindings.concat("-fx-background-color: linear-gradient(to bottom, #16213f, #0c1022);"));

        playfield.prefWidthProperty().bind(gameWidth);
        playfield.prefHeightProperty().bind(gameHeight);

        backgroundView.setViewOrder(20);
        birdView.setLayoutX(BIRD_START_X);
        // On n'utilise plus de binding sur layoutY pour éviter le conflit avec setLayoutY dans showModeMenu
        birdView.setLayoutY(getBirdStartY());
        birdView.setViewOrder(-2);

        scoreLabel.layoutXProperty().bind(gameWidth.subtract(130));
        scoreLabel.setLayoutY(18);
        scoreLabel.setViewOrder(-3);

        modeLabel.setLayoutX(16);
        modeLabel.setLayoutY(18);
        modeLabel.setViewOrder(-3);

        helpLabel.setLayoutX(16);
        helpLabel.layoutYProperty().bind(gameHeight.subtract(46));
        helpLabel.setViewOrder(-3);

        playfield.getChildren().addAll(backgroundView, birdView, scoreLabel, modeLabel, helpLabel);

        getChildren().add(playfield);
    }

    private void configureOverlay() {
        overlayBackdrop.setMouseTransparent(true);
        overlayBackdrop.setVisible(false);
        overlayBackdrop.setViewOrder(-10);

        overlayCard.setAlignment(Pos.CENTER);
        overlayCard.setPrefSize(380, 310);
        overlayCard.layoutXProperty().bind(gameWidth.subtract(380).divide(2));
        overlayCard.layoutYProperty().bind(gameHeight.subtract(310).divide(2));
        overlayCard.setStyle(
                "-fx-background-color: rgba(22, 28, 51, 0.95);" +
                "-fx-background-radius: 24;" +
                "-fx-border-color: rgba(255, 215, 74, 0.50);" +
                "-fx-border-radius: 24;" +
                "-fx-border-width: 2;" +
                "-fx-padding: 28;"
        );
        overlayCard.setVisible(false);

        restartButton.setMaxWidth(Double.MAX_VALUE);
        changeModeButton.setMaxWidth(Double.MAX_VALUE);
        quitButton.setMaxWidth(Double.MAX_VALUE);
        restartButton.setOnAction(event -> restartGame());
        changeModeButton.setOnAction(event -> showModeMenu());
        quitButton.setOnAction(event -> backToHome());

        overlayCard.getChildren().addAll(overlayTitle, overlayScore);
        overlay.getChildren().addAll(overlayBackdrop, overlayCard);
        overlay.setViewOrder(-20);

        getChildren().add(overlay);
    }

    private void configureModeMenu() {
        modeMenuCard.setAlignment(Pos.CENTER);
        modeMenuCard.setPadding(new Insets(28));
        modeMenuCard.setPrefSize(460, 450);
        modeMenuCard.layoutXProperty().bind(gameWidth.subtract(460).divide(2));
        modeMenuCard.layoutYProperty().bind(gameHeight.subtract(450).divide(2));
        modeMenuCard.setStyle(
                "-fx-background-color: rgba(12, 19, 36, 0.95);" +
                "-fx-background-radius: 26;" +
                "-fx-border-color: rgba(255, 215, 74, 0.52);" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 26;"
        );
        modeMenuCard.setVisible(false);

        detenteButton.setMaxWidth(Double.MAX_VALUE);
        classiqueButton.setMaxWidth(Double.MAX_VALUE);
        championButton.setMaxWidth(Double.MAX_VALUE);
        backToHomeButton.setMaxWidth(Double.MAX_VALUE);
        backToHomeButton.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #6c757d, #5a6268);" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 16;" +
            "-fx-padding: 10 18 10 18;" +
            "-fx-cursor: hand;"
        );


        detenteButton.setOnAction(event -> startWithMode(GameMode.DETENTE));
        classiqueButton.setOnAction(event -> startWithMode(GameMode.CLASSIQUE));
        championButton.setOnAction(event -> startWithMode(GameMode.CHAMPION));
        backToHomeButton.setOnAction(event -> backToHome());

        modeMenuCard.getChildren().addAll(
                modeMenuTitle,
                modeMenuSubtitle,
                detenteButton,
                classiqueButton,
                championButton,
                backToHomeButton
        );

        modeMenu.getChildren().addAll(modeMenuBackdrop, modeMenuCard);
        modeMenu.setViewOrder(-40);

        getChildren().add(modeMenu);
    }

    private void configureInput() {
        setFocusTraversable(true);

        setOnMouseClicked(event -> {
            // Ignore les clics si un menu est visible pour ne pas interférer avec les boutons.
            if (overlay.isVisible() || modeMenu.isVisible()) {
                return;
            }
            requestFocus();
            if (event.getButton() == MouseButton.PRIMARY) {
                handleAction();
            }
        });

        setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.SPACE || event.getCode() == KeyCode.UP || event.getCode() == KeyCode.W) {
                handleAction();
            } else if (event.getCode() == KeyCode.R) {
                restartGame();
            } else if (event.getCode() == KeyCode.M) {
                showModeMenu();
            }
        });
    }

    private void handleAction() {
        if (gameOver) {
            restartGame();
            return;
        }

        if (!running) {
            return;
        }

        flap();
    }

    private void flap() {
        if (!running) {
            return;
        }

        birdVelocityY = currentMode.flapVelocity;
        birdView.setRotate(-22);
    }

    private void restartGame() {
        clearObstacles();

        running = true;
        gameOver = false;
        score = 0;
        spawnAccumulator = 0;
        birdVelocityY = 0;
        lastFrameNanos = -1L;

        birdView.setLayoutX(BIRD_START_X);
        birdView.setLayoutY(getBirdStartY());
        birdView.setRotate(0);
        previousGapCenter = getBirdStartY();
        nextGapHigh = random.nextBoolean();

        scoreLabel.setText("0");
        overlayBackdrop.setVisible(false);
        overlayCard.setVisible(false);
        modeMenuBackdrop.setVisible(false);
        modeMenuCard.setVisible(false);
        requestFocus();
    }

    private void updateFrame(long now) {
        if (lastFrameNanos < 0L) {
            lastFrameNanos = now;
            return;
        }

        double deltaSeconds = (now - lastFrameNanos) / 1_000_000_000.0;
        lastFrameNanos = now;

        if (!running) {
            return;
        }

        birdVelocityY += currentMode.gravity * deltaSeconds;
        birdView.setLayoutY(birdView.getLayoutY() + birdVelocityY * deltaSeconds);
        birdView.setRotate(Math.max(-28, Math.min(90, birdVelocityY / 5.0)));

        if (birdView.getLayoutY() < 0 || birdView.getLayoutY() + BIRD_SIZE > getGroundY()) {
            triggerGameOver();
            return;
        }

        spawnAccumulator += deltaSeconds;
        if (spawnAccumulator >= currentMode.spawnInterval) {
            spawnAccumulator -= currentMode.spawnInterval;
            spawnObstaclePair();
        }

        double currentSpeed = currentMode.baseSpeed + (score * currentMode.speedPerScore);

        for (Iterator<ObstaclePair> iterator = obstacles.iterator(); iterator.hasNext();) {
            ObstaclePair pair = iterator.next();
            pair.update(deltaSeconds, currentSpeed);

            if (!pair.scored && pair.getRightEdge() < birdView.getLayoutX()) {
                pair.scored = true;
                score += 1;
                scoreLabel.setText(Integer.toString((int) score));
            }

            if (pair.isOffscreen()) {
                pair.removeFrom(playfield);
                iterator.remove();
                continue;
            }

            if (pair.collidesWith(birdView)) {
                triggerGameOver();
                return;
            }
        }
    }

    private void triggerGameOver() {
        running = false;
        gameOver = true;
        overlayBackdrop.setVisible(true);
        overlayCard.setVisible(true);
        overlayScore.setText("Score : " + (int) score);
        birdView.setRotate(85);

        try {
            new FlappyScoreService().saveScore((int) score, currentMode.title);
            awardXpOnGameOver((int) score);
        } catch (Exception ignored) {
            // Ignore save failures to avoid blocking game over.
        }
    }

    private void awardXpOnGameOver(int finalScore) {
        if (finalScore <= 0) return;

        int xpGained = finalScore * 5; // 5 XP par point de score

        try {
            com.example.java_royal.session.UserSession session = com.example.java_royal.session.UserSession.getInstance();
            if (session.getId() == 0) {
                System.out.println("[FlappyBirdGame] Session utilisateur non trouvée, impossible d'ajouter l'XP.");
                return;
            }

            int currentXp = session.getCurrentXp();
            int currentLevel = session.getCurrentLevel();
            int threshold = currentLevel * 100;

            int updatedXp = currentXp + xpGained;
            int updatedLevel = currentLevel;

            while (updatedXp >= threshold) {
                updatedXp -= threshold;
                updatedLevel++;
                threshold = updatedLevel * 100;
            }

            com.example.java_royal.service.UserService.updateUserXp(session.getId(), updatedXp, updatedLevel);
            session.setCurrentLevel(updatedLevel);
            session.setCurrentXp(updatedXp);

            System.out.println("[FlappyBirdGame] ✅ " + xpGained + " XP ajoutés. Nouveau statut : Niveau " + updatedLevel + ", " + updatedXp + " XP.");

        } catch (Exception e) {
            System.err.println("[FlappyBirdGame] ❌ Erreur lors de la mise à jour de l'XP : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void spawnObstaclePair() {
        Image obstacleImage = obstacleImages[random.nextInt(obstacleImages.length)];
        double startX = getGameWidth() + 35;

        ObstaclePair pair;
        if (shouldSpawnBottomGiant()) {
            pair = new ObstaclePair(obstacleImage, startX, currentMode.gapSize, true);
        } else {
            double gapCenter = computeNextGapCenter(currentMode.gapSize);
            pair = new ObstaclePair(obstacleImage, startX, gapCenter, currentMode.gapSize);
        }

        obstacles.add(pair);
        pair.addTo(playfield);
    }

    private void backToHome() {
        try {
            timer.stop();
            Stage stage = (Stage) getScene().getWindow();
            SceneNavigator.replaceScene(stage, "/com/example/java_royal/home-view.fxml", "Accueil");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean shouldSpawnBottomGiant() {
        if (score < 4) {
            return false;
        }

        double chance;
        if (currentMode == GameMode.DETENTE) {
            chance = 0.12;
        } else if (currentMode == GameMode.CLASSIQUE) {
            chance = 0.20;
        } else {
            chance = 0.28;
        }

        return random.nextDouble() < chance;
    }

    private double computeNextGapCenter(double gapSize) {
        double minCenter = TOP_MARGIN + gapSize / 2.0;
        double maxCenter = getGroundY() - BOTTOM_MARGIN - gapSize / 2.0;
        double span = maxCenter - minCenter;

        double highStart = minCenter + span * 0.08;
        double highEnd = minCenter + span * 0.30;
        double lowStart = minCenter + span * 0.70;
        double lowEnd = minCenter + span * 0.92;

        double candidate = randomBetween(nextGapHigh ? highStart : lowStart, nextGapHigh ? highEnd : lowEnd);
        double minJump = span * 0.42;

        if (Math.abs(candidate - previousGapCenter) < minJump) {
            candidate = randomBetween(nextGapHigh ? lowStart : highStart, nextGapHigh ? lowEnd : highEnd);
        }

        if (score >= 8) {
            double jitter = randomBetween(-14, 14);
            candidate = clamp(candidate + jitter, minCenter, maxCenter);
        }

        previousGapCenter = candidate;
        nextGapHigh = !nextGapHigh;
        return candidate;
    }

    private double randomBetween(double min, double max) {
        return min + random.nextDouble() * (max - min);
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private void clearObstacles() {
        for (ObstaclePair pair : obstacles) {
            pair.removeFrom(playfield);
        }

        obstacles.clear();
    }

    private ImageView createImageView(Image image, double fitWidth, double fitHeight, boolean preserveRatio) {
        ImageView view = new ImageView(image);
        view.setFitWidth(fitWidth);
        view.setFitHeight(fitHeight);
        view.setPreserveRatio(preserveRatio);
        return view;
    }

    private Label createLabel(String text, double fontSize, Color color) {
        Label label = new Label(text);
        label.setTextFill(color);
        label.setFont(Font.font("Trebuchet MS", FontWeight.EXTRA_BOLD, fontSize));
        label.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.75), 10, 0.4, 0, 2);");
        return label;
    }

    private Button createButton(String text) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setFont(Font.font("Trebuchet MS", FontWeight.BOLD, 16));
        button.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #ffd54a, #f5a623);" +
                "-fx-text-fill: #1d233d;" +
                "-fx-background-radius: 16;" +
                "-fx-padding: 10 18 10 18;" +
                "-fx-cursor: hand;"
        );
        return button;
    }

    private Image loadImage(String firstPart, String... moreParts) {
        Path path = Paths.get(firstPart, moreParts);

        if (!Files.exists(path)) {
            throw new IllegalStateException("Image asset missing: " + path.toAbsolutePath());
        }

        try (InputStream inputStream = Files.newInputStream(path)) {
            return new Image(inputStream);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load image asset: " + path.toAbsolutePath(), exception);
        }
    }

    private boolean intersectsOpaquePixels(ImageView first, ImageView second) {
        Bounds firstSceneBounds = first.localToScene(first.getBoundsInLocal());
        Bounds secondSceneBounds = second.localToScene(second.getBoundsInLocal());

        double minX = Math.max(firstSceneBounds.getMinX(), secondSceneBounds.getMinX());
        double minY = Math.max(firstSceneBounds.getMinY(), secondSceneBounds.getMinY());
        double maxX = Math.min(firstSceneBounds.getMaxX(), secondSceneBounds.getMaxX());
        double maxY = Math.min(firstSceneBounds.getMaxY(), secondSceneBounds.getMaxY());

        if (minX >= maxX || minY >= maxY) {
            return false;
        }

        for (int y = (int) Math.floor(minY); y <= (int) Math.ceil(maxY); y++) {
            for (int x = (int) Math.floor(minX); x <= (int) Math.ceil(maxX); x++) {
                if (isOpaqueAtScenePixel(first, x + 0.5, y + 0.5) && isOpaqueAtScenePixel(second, x + 0.5, y + 0.5)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isOpaqueAtScenePixel(ImageView view, double sceneX, double sceneY) {
        Image image = view.getImage();
        if (image == null) {
            return false;
        }

        Bounds localBounds = view.getBoundsInLocal();
        double localX = view.sceneToLocal(sceneX, sceneY).getX();
        double localY = view.sceneToLocal(sceneX, sceneY).getY();

        if (localX < localBounds.getMinX() || localX >= localBounds.getMaxX()
                || localY < localBounds.getMinY() || localY >= localBounds.getMaxY()) {
            return false;
        }

        double widthScale = image.getWidth() / localBounds.getWidth();
        double heightScale = image.getHeight() / localBounds.getHeight();

        int imageX = (int) ((localX - localBounds.getMinX()) * widthScale);
        int imageY = (int) ((localY - localBounds.getMinY()) * heightScale);

        if (imageX < 0 || imageX >= (int) image.getWidth() || imageY < 0 || imageY >= (int) image.getHeight()) {
            return false;
        }

        PixelReader reader = image.getPixelReader();
        if (reader == null) {
            return false;
        }

        int argb = reader.getArgb(imageX, imageY);
        int alpha = (argb >>> 24) & 0xFF;
        return alpha >= ALPHA_THRESHOLD;
    }

    private final class ObstaclePair {
        private final ImageView topView;
        private final ImageView bottomView;
        private final double width = OBSTACLE_WIDTH;
        private boolean scored;

        private ObstaclePair(Image obstacleImage, double startX, double gapCenter, double gapSize) {
            double topHeight = Math.max(48, gapCenter - gapSize / 2.0);
            double bottomY = gapCenter + gapSize / 2.0;
            double bottomHeight = Math.max(48, getGroundY() - bottomY);

            topView = createImageView(obstacleImage, OBSTACLE_WIDTH, topHeight, false);
            topView.setLayoutX(startX);
            topView.setLayoutY(0);
            topView.setScaleY(-1);
            topView.setViewOrder(1);

            bottomView = createImageView(obstacleImage, OBSTACLE_WIDTH, bottomHeight, false);
            bottomView.setLayoutX(startX);
            bottomView.setLayoutY(bottomY);
            bottomView.setViewOrder(1);
        }

        private ObstaclePair(Image obstacleImage, double startX, double gapSize, boolean giantBottom) {
            double topFreeHeight = TOP_MARGIN + gapSize * 0.92;
            double bottomY = Math.min(getGroundY() - 64, topFreeHeight);
            double bottomHeight = Math.max(140, getGroundY() - bottomY);

            topView = null;
            bottomView = createImageView(obstacleImage, OBSTACLE_WIDTH, bottomHeight, false);
            bottomView.setLayoutX(startX);
            bottomView.setLayoutY(bottomY);
            bottomView.setViewOrder(1);
        }

        private void addTo(Pane parent) {
            if (topView != null) {
                parent.getChildren().add(topView);
            }
            parent.getChildren().add(bottomView);
        }

        private void removeFrom(Pane parent) {
            if (topView != null) {
                parent.getChildren().remove(topView);
            }
            parent.getChildren().remove(bottomView);
        }

        private void update(double deltaSeconds, double speed) {
            double shift = speed * deltaSeconds;
            if (topView != null) {
                topView.setLayoutX(topView.getLayoutX() - shift);
            }
            bottomView.setLayoutX(bottomView.getLayoutX() - shift);
        }

        private boolean collidesWith(ImageView bird) {
            boolean topCollision = topView != null && intersectsOpaquePixels(topView, bird);
            return topCollision || intersectsOpaquePixels(bottomView, bird);
        }

        private boolean isOffscreen() {
            return getRightEdge() < -40;
        }

        private double getRightEdge() {
            return bottomView.getLayoutX() + width;
        }
    }
}

