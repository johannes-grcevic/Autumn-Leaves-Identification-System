package util;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class FXUtils {
    public static ButtonType showAlert(String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(StringUtils.capitalize(alertType.toString()));
        alert.setHeaderText(message);
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

    public static void SetBorderStyle(Region border, Color color, BorderStrokeStyle style, double cornerRadius, double width) {
        BorderStroke stroke = new BorderStroke(
                color,
                style,
                new CornerRadii(cornerRadius),
                new BorderWidths(width)
        );

        border.setBorder(new Border(stroke));
    }

    public static void SetBorderStyle(Region border, BorderStroke stroke) {
        border.setBorder(new Border(stroke));
    }
}
