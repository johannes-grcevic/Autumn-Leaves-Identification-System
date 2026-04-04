package me.johannes.autumn.util;

import javafx.animation.ScaleTransition;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.util.Duration;

public class FXUtils {
    public static void showAlert(String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(StringUtils.capitalize(alertType.toString()));
        alert.setHeaderText(message);
        alert.show();
    }

    public static ButtonType showConfirmationAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(message);

        // wait for the user to click on a button
        alert.showAndWait();

        return alert.getResult();
    }

    public static Stage showPopupWindow(String title, Parent parent, double width, double height, boolean isResizable) {
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setResizable(isResizable);

        Scene scene = new Scene(parent, width, height);
        stage.setScene(scene);
        stage.show();

        return stage;
    }

    public static void setHoverScaleAnimation(Node node, double scaleFactor, Duration duration) {
        ScaleTransition scaleIn = new ScaleTransition(duration, node);
        scaleIn.setToX(scaleFactor);
        scaleIn.setToY(scaleFactor);

        ScaleTransition scaleOut = new ScaleTransition(duration, node);
        scaleOut.setToX(node.getScaleX());
        scaleOut.setToY(node.getScaleY());

        node.setOnMouseEntered(_ -> {
            scaleOut.stop();
            scaleIn.playFromStart();
        });

        node.setOnMouseExited(_ -> {
            scaleIn.stop();
            scaleOut.playFromStart();
        });
    }
}
