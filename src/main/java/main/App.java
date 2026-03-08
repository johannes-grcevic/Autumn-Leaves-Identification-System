package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;

/**
 * JavaFX Autumn Leaves Identification/Vision System
 */
public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double scaledWidth = screenSize.getWidth() * 0.85;
        double scaledHeight = screenSize.getHeight() * 0.85;

        Scene scene = new Scene(loadFXML("identification-system"), 1280, 720);
        stage.setTitle("Autumn Leaves Identification System");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    static void main() {
        launch();
    }
}