package com.example.java_royal.legacy;

import com.example.java_royal.model.GreetingModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Ancien contrôleur conservé en tant que legacy pour compatibilité.
 */
public class HelloController {
    @FXML
    private Label welcomeText;

    private GreetingModel model = new GreetingModel();

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText(model.getMessage());
    }
}
