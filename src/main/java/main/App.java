package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.util.Objects;

/**
 * JavaFX Autumn Leaves Identification System
 */
public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // set the scene
        Scene scene = new Scene(loadFXML("identification-system"), 1280, 720);

        // set the CSS file
        scene.getStylesheets().add(Objects.requireNonNull(App.class.getResource("style.css")).toExternalForm());

        // set the stage
        stage.setTitle("Autumn Leaves Identification System");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return loader.load();
    }

    static void main(String[] args) {
        launch(args);
    }
}