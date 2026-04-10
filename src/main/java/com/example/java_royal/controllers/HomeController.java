package com.example.java_royal.controllers;

import com.example.java_royal.session.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HomeController {
    @FXML
    private Label welcomeLabel;

    @FXML
    public void initialize() {
        String username = SessionManager.getInstance().getCurrentUser() == null
                ? "Utilisateur"
                : SessionManager.getInstance().getCurrentUser().getUsername();
        welcomeLabel.setText("Bonjour " + username);
    }
}

