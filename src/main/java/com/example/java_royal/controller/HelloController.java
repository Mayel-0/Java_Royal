package com.example.java_royal.controller;

import com.example.java_royal.model.GreetingModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Contrôleur propre suivant l'architecture MVC.
 */
public class HelloController {
    @FXML
    private Label welcomeText;

    private final GreetingModel model = new GreetingModel();

    @FXML
    public void initialize() {
        // Initialisation si nécessaire
        welcomeText.setText("Click the button to see the greeting");
    }

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText(model.getMessage());
    }
}

