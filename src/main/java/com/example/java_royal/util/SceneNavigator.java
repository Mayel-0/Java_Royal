package com.example.java_royal.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public final class SceneNavigator {
    private SceneNavigator() {
    }

    public static void replaceScene(Stage stage, String fxmlPath, String title) throws IOException {
        Parent root = loadRoot(fxmlPath);
        double width = stage.getWidth();
        double height = stage.getHeight();
        stage.setScene(width > 0 && height > 0 ? new Scene(root, width, height) : new Scene(root));
        stage.setTitle(title);
        stage.show();
    }

    public static void replaceScene(Stage stage, String fxmlPath, String title, Scene fallbackScene) throws IOException {
        Parent root = loadRoot(fxmlPath);
        double width = fallbackScene == null ? -1 : fallbackScene.getWidth();
        double height = fallbackScene == null ? -1 : fallbackScene.getHeight();
        if (width > 0 && height > 0) {
            stage.setScene(new Scene(root, width, height));
        } else {
            stage.setScene(new Scene(root));
        }
        stage.setTitle(title);
        stage.show();
    }

    private static Parent loadRoot(String fxmlPath) throws IOException {
        URL resource = Objects.requireNonNull(SceneNavigator.class.getResource(fxmlPath), "FXML introuvable: " + fxmlPath);
        return new FXMLLoader(resource).load();
    }
}
