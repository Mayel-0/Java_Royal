package com.example.java_royal.model;

/**
 * Modèle simple qui contient le message de salutation.
 */
public class GreetingModel {
    private String message = "Welcome to JavaFX Application!";

    public GreetingModel() {
    }

    public GreetingModel(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

