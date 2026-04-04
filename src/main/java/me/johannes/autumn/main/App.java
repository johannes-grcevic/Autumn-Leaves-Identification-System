package me.johannes.autumn.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

/**
 * JavaFX Autumn Leaves Identification System
 */
public class App extends Application {
    private static Image iconImage;

    @Override
    public void start(Stage stage) throws IOException {
        // set up the main scene
        Scene scene = new Scene(loadFXML("identification-system"), 1280, 720);

        // set up the CSS stylesheet
        scene.getStylesheets().add(Objects.requireNonNull(getResource("style.css")).toExternalForm());

        // set up the main stage
        stage.setTitle("Autumn Leaves Identification System");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

        // set the application icon
        iconImage = new Image(Objects.requireNonNull(getResource("icon.png")).toExternalForm(), 64, 64, true, true);
        stage.getIcons().add(iconImage);
    }

    // load an FXML file
    public static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(getResource(fxml + ".fxml"));
        return loader.load();
    }

    // get a resource from the resources folder
    public static URL getResource(String filename) {
        return App.class.getResource("/me/johannes/autumn/" + filename);
    }

    public static String getStylesheet(String filename) {
        return getResource(filename + ".css").toExternalForm();
    }

    public static Image getIconImage() {
        return iconImage;
    }

    // start the application
    static void main(String[] args) {
        launch(args);
    }
}