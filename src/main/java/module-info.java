module com.example.java_royal {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.java_royal to javafx.fxml;
    exports com.example.java_royal;
}