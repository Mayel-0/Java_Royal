module com.example.java_royal {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.java_royal to javafx.fxml;
    opens com.example.java_royal.controller to javafx.fxml;
    exports com.example.java_royal;
    exports com.example.java_royal.controller;
    exports com.example.java_royal.model;
    exports com.example.java_royal.app;
}