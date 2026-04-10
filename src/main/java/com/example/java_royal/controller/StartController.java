package com.example.java_royal.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class StartController {
    @FXML
    private void goToLogin(ActionEvent event) throws IOException {
        changeScene(event, "/com/example/java_royal/hello-view.fxml", "Connexion");
    }

    @FXML
    private void goToRegister(ActionEvent event) throws IOException {
        changeScene(event, "/com/example/java_royal/register-view.fxml", "S'inscrire");
    }

    private void changeScene(ActionEvent event, String resourcePath, String title) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(resourcePath));
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle(title);
        stage.show();
    }
}
