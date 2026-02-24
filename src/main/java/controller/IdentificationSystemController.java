package controller;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import javafx.stage.Stage;
import util.FXUtils;

import java.io.File;
import java.net.URI;
import java.net.URL;

import java.util.Collections;
import java.util.ResourceBundle;

import static util.FXUtils.showAlert;

public class IdentificationSystemController implements Initializable {
    @FXML
    private BorderPane borderPane;
    @FXML
    private ImageView imageView;
    @FXML
    private ColorPicker colorPicker;
    @FXML
    private Label statusLabel;

    private final Tooltip clusterTooltip = new Tooltip();

    private File imageFile;
    private Image resizedImage;

    private ClusterController clusterController;

    @FXML
    private void openFile() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter imageFilter =
                new FileChooser.ExtensionFilter(
                        "Image Files (*.jpg, *.jpeg, *.png, *.bmp, *.wbmp)",
                        "*.jpg",
                        "*.jpeg",
                        "*.png",
                        "*.bmp",
                        "*.wbmp"
                );

        fileChooser.getExtensionFilters().add(imageFilter);
        File file = fileChooser.showOpenDialog(borderPane.getScene().getWindow());

        if (file != null && file.exists() && file.isFile()) {
            imageFile = file;
        }

        if (!isImageFileLoaded()) return;

        AnchorPane centerAnchorPane = (AnchorPane)borderPane.getCenter();
        resizedImage = new Image(String.valueOf(imageFile.toURI()), centerAnchorPane.getWidth(), centerAnchorPane.getHeight(), false, false);
        imageView.setImage(resizedImage);

        createClusters();
    }

    @FXML
    private void convertBlackAndWhite() {
        if (!isImageFileLoaded()) {
            showAlert("No image loaded!", AlertType.ERROR);
            return;
        }

        if (!hasSelectedColors()) {
            if (!showAlert("No colours selected! Select them automatically?", AlertType.CONFIRMATION).equals(ButtonType.OK)) {
                return;
            }
            else {
                autoSelectColors();
            }
        }

        try {
            WritableImage blackAndWhiteImage = getBlackAndWhiteImage(resizedImage);

            Pane pane = new Pane(new ImageView(blackAndWhiteImage));
            pane.setPrefSize(resizedImage.getWidth(), resizedImage.getHeight());
            FXUtils.showPopupWindow("Black and White Image", pane, pane.getPrefWidth(), pane.getPrefHeight(), false);

        } catch (Exception e) {
            System.err.print("Error converting to black and white: " + e);
        }
    }

    @FXML
    private void displayClusters() {
        if (!isImageFileLoaded()) {
            showAlert("No image loaded!", AlertType.ERROR);
            return;
        }

        // ask to auto-select colours if none are selected
        if (!hasSelectedColors()) {
            if (!showAlert("No colours selected! Select them automatically?", AlertType.CONFIRMATION).equals(ButtonType.OK)) {
                return;
            }
            else {
                autoSelectColors();
            }
        }

        // draw cluster boundary rectangles
        ClusterBoundsController controller = new ClusterBoundsController();
        Pane drawPane = controller.drawBounds(resizedImage, Color.BLACK, Color.BLUE, clusterController);

        Stage stage = FXUtils.showPopupWindow("Clusters", drawPane, drawPane.getPrefWidth(), drawPane.getPrefHeight(), false);
        controller.setKeyListener(stage.getScene());

        // update status text
        setStatusBar("Leaf/Cluster Count: " + clusterController.getClusterCount(), true);
    }

    @FXML
    private void displayClusterTooltip(Node source, double screenX, double screenY) {
        if (!isImageFileLoaded()) return;

        Image blackAndWhiteImage = getBlackAndWhiteImage(resizedImage);

        // get local mouse coordinates
        Point2D screenLocalCoords = imageView.screenToLocal(screenX, screenY);

        int[] clusterPixels = clusterController.getClusterPixels(blackAndWhiteImage, screenLocalCoords);
        int clusterNumber = clusterController.getClusterNumber(blackAndWhiteImage, screenLocalCoords);

        if (clusterPixels.length == 0 || clusterNumber <= 0) return;

        clusterTooltip.setText("""
                Leaf/Cluster Number: %d
                Estimated Size (pixel units): %d
                """.formatted(clusterNumber, clusterPixels.length));

        clusterTooltip.setHideOnEscape(true);
        clusterTooltip.setAutoHide(true);
        clusterTooltip.show(source, screenX, screenY);
    }

    @FXML
    private void autoSelectColors() {
        if (!isImageFileLoaded()) {
            showAlert("No image loaded!", AlertType.ERROR);
            return;
        }

        Image sourceImage = resizedImage;
        int width = (int) sourceImage.getWidth();
        int height = (int) sourceImage.getHeight();
        PixelReader reader = sourceImage.getPixelReader();

        int maxCustomColors = 108;
        ObservableList<Color> customColors = colorPicker.getCustomColors();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Color pixelColor = reader.getColor(i, j);

                // ignore non-autumn leaves
                if (!isAutumnLeaf(pixelColor) || customColors.contains(pixelColor)) {
                    continue;
                }

                // limit max custom colours to fit inside UI
                if (customColors.size() >= maxCustomColors) break;

                customColors.add(pixelColor);
            }
        }

        setStatusBar("Found " + customColors.size() + " Relevant Colors" , true);
    }

    @FXML
    private void clearColorPicker() {
        if (getSelectedColors().isEmpty()) return;

        colorPicker.getCustomColors().clear();
        colorPicker.setValue(Color.WHITE);
    }

    @FXML
    private void deleteFile() {
        if (!isImageFileLoaded()) return;
        if (!showAlert("Are you sure you want to delete the image?", AlertType.CONFIRMATION).equals(ButtonType.OK))
            return;

        imageFile = null;
        imageView.setImage(null);

        setStatusBar("No Image Loaded", true);
        setImageBorder(Color.BLACK, BorderStrokeStyle.DASHED, 3, 2);
        clearColorPicker();
        clusterController = null;
    }

    @FXML
    private void quitApplication() {
        // Don't quit if the user selects cancel
        if (!showAlert("Are you sure you want to quit?", AlertType.CONFIRMATION).equals(ButtonType.OK))
            return;

        // Close the application
        System.exit(0);
    }

    @FXML
    private void openAboutPage() {
        try {
            java.awt.Desktop.getDesktop().browse(URI.create("https://github.com/JohannesGrcevic/Autumn-Leaves-Identification-System"));
        } catch (Exception e) {
            System.err.print("Error opening about page: " + e);
        }
    }

    // event listeners //

    private void OnImageChange(ObservableValue<? extends Image> observableValue, Image oldValue, Image newValue) {
        if (newValue != null && !newValue.isError()) {
            setStatusBar("Image: " + imageFile.getName(), true);
            ((AnchorPane)borderPane.getCenter()).setBorder(null);
            clearColorPicker();
        }
    }

    private void OnColorSelectionChange(ObservableValue<? extends Color> observableValue, Color oldValue, Color newValue) {
        // recompute clusters each time the colour selection changes
        createClusters();

        System.out.println("Cluster count: " + clusterController.getClusterCount());
    }

    // Utility methods //

    private void createClusters() {
        if (!hasSelectedColors()) {
            autoSelectColors();
        }

        WritableImage blackAndWhiteImage = getBlackAndWhiteImage(resizedImage);
        int width = (int) blackAndWhiteImage.getWidth();
        int height = (int) blackAndWhiteImage.getHeight();

        PixelReader reader = blackAndWhiteImage.getPixelReader();

        // create clusters
        if (clusterController != null) {
            clusterController = null;
        }

        clusterController = new ClusterController(width, height);
        clusterController.unionNeighboringPixels(width, height, reader, Color.BLACK);
    }

    private WritableImage getBlackAndWhiteImage(Image source) {
        int width = (int) source.getWidth();
        int height = (int) source.getHeight();

        WritableImage writableImage = new WritableImage(width, height);
        PixelReader reader = source.getPixelReader();
        PixelWriter writer = writableImage.getPixelWriter();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Color pixelColor = reader.getColor(i, j);

                if (isSelectedColor(pixelColor)) {
                    writer.setColor(i, j, Color.WHITE);
                }
                else {
                    writer.setColor(i, j, Color.BLACK);
                }
            }
        }

        return writableImage;
    }

    private boolean isSelectedColor(Color pixelColor) {
        double hue = pixelColor.getHue();
        double saturation = pixelColor.getSaturation();
        double brightness = pixelColor.getBrightness();

        for (Color selectedColor : getSelectedColors()) {
            if (selectedColor.getHue() >= hue &&
                    saturation > 0.3 &&
                    brightness > 0.2) {
                return true;
            }
        }

        return false;
    }

    private boolean isAutumnLeaf(Color pixelColor) {
        double hue = pixelColor.getHue();
        double saturation = pixelColor.getSaturation();
        double brightness = pixelColor.getBrightness();

        // Detect brown/orange leaves
        return hue >= 15 && hue <= 50 && // orange/brown range
                saturation > 0.3 && // avoid gray areas
                brightness > 0.2;
    }

    private ObservableList<Color> getSelectedColors() {
        ColorPicker picker = colorPicker;

        if (!picker.getCustomColors().isEmpty() && picker.getValue().equals(Color.WHITE)) {
            return picker.getCustomColors();
        }
        else {
            return FXCollections.observableList(Collections.singletonList(picker.getValue()));
        }
    }

    private void setStatusBar(String message, boolean visible) {
        statusLabel.setText(message);
        statusLabel.setVisible(visible);
    }

    private void setImageBorder(Color color, BorderStrokeStyle style, double cornerRadius, double width) {
        BorderStroke stroke = new BorderStroke(
                color,
                style,
                new CornerRadii(cornerRadius),
                new BorderWidths(width)
        );
        ((AnchorPane)borderPane.getCenter()).setBorder(new Border(stroke));
    }

    private boolean hasSelectedColors() {
        return !getSelectedColors().isEmpty() && !getSelectedColors().getFirst().equals(Color.WHITE);
    }

    private boolean isImageFileLoaded() {
        return imageFile != null;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colorPicker.setTooltip(new Tooltip("Select colors to be used for clustering"));

        setImageBorder(Color.BLACK, BorderStrokeStyle.DASHED, 3, 2);
        imageView.imageProperty().addListener(this::OnImageChange);
        colorPicker.valueProperty().addListener(this::OnColorSelectionChange);

        imageView.setOnMouseClicked(event -> {
            Node node = (Node)event.getSource();
            displayClusterTooltip(node, event.getScreenX(), event.getScreenY());
        });
    }
}
