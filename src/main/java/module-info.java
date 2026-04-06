module me.johannes.autumn {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.compiler;
    requires java.naming;
    requires xstream;

    opens me.johannes.autumn.main to javafx.fxml;
    opens me.johannes.autumn.controller to javafx.fxml;

    exports me.johannes.autumn.main;
    exports me.johannes.autumn.controller;
    exports me.johannes.autumn.model;
    exports me.johannes.autumn.util;
}
