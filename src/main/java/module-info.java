module com.example.java_royal {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;
    requires org.xerial.sqlitejdbc;
    requires org.slf4j;
    requires io.github.cdimascio.dotenv.java;
    requires jbcrypt;

    opens com.example.java_royal to javafx.fxml;
    opens com.example.java_royal.controller to javafx.fxml;
    opens com.example.java_royal.controllers to javafx.fxml;
    opens com.example.java_royal.app to javafx.fxml;
    opens com.javaroyal.games.flappy to javafx.fxml;
    opens com.javaroyal.games.hangman to javafx.fxml;

    exports com.example.java_royal;
    exports com.example.java_royal.controller;
    exports com.example.java_royal.controllers;
    exports com.example.java_royal.model;
    exports com.example.java_royal.config;
    exports com.example.java_royal.app;
    exports com.javaroyal.games.flappy;
    exports com.javaroyal.games.hangman;
}