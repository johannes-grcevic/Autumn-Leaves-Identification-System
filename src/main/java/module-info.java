module org.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.compiler;

    opens main to javafx.fxml;
    exports main;
    exports controller;
    exports model;
    opens controller to javafx.fxml;
}
