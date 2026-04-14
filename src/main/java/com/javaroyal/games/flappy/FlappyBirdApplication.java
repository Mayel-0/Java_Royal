package com.javaroyal.games.flappy;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FlappyBirdApplication extends Application {
    @Override
    public void start(Stage stage) {
        FlappyBirdGame game = new FlappyBirdGame();
        Scene scene = new Scene(game);

        stage.setTitle("Clash Royale Flappy Bird");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();

        Platform.runLater(game::requestFocus);
    }

    public static void main(String[] args) {
        launch(args);
    }
}