package com.example.java_royal.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class StartController {
    private static final String LOGIN_VIEW = "/com/example/java_royal/hello-view.fxml";
    private static final String REGISTER_VIEW = "/com/example/java_royal/register-view.fxml";

    @FXML
    private void goToLogin(ActionEvent event) throws IOException {
        changeScene(event, LOGIN_VIEW, "Connexion");
    }

    @FXML
    private void goToRegister(ActionEvent event) throws IOException {
        changeScene(event, REGISTER_VIEW, "S'inscrire");
    }

    private void changeScene(ActionEvent event, String resourcePath, String title) throws IOException {
        Parent root = loadFxml(resourcePath);
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle(title);
        stage.show();
    }

    private Parent loadFxml(String resourcePath) throws IOException {
        return new FXMLLoader(Objects.requireNonNull(getClass().getResource(resourcePath), "FXML introuvable: " + resourcePath)).load();
    }
}
