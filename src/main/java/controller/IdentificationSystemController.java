package controller;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import javafx.stage.Stage;
import model.PixelNode;
import util.ColorUtils;
import util.FXUtils;
import util.ImageUtils;

import java.io.File;
import java.net.URI;
import java.net.URL;

import java.util.*;

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
    @FXML
    private Button autoSelectColorsButton;

    private Pane centerPane;
    private final Tooltip nodeTooltip = new Tooltip();

    private File imageFile;
    private Image resizedImage;
    private WritableImage blackAndWhiteImage;

    private BorderStroke imageBorderStroke;

    private NodeController nodeController;

    private final int MIN_NODE_SIZE;
    private final int MAX_CUSTOM_COLORS;

    private final String GITHUB_URL;
    private final String ALERT_NO_IMAGE_LOADED = "No Image Loaded!";
    private final String ALERT_NO_COLORS_SELECTED = "No Colors Selected!";

    public IdentificationSystemController() {
        MIN_NODE_SIZE = 55;
        MAX_CUSTOM_COLORS = 120;
        GITHUB_URL = "https://github.com/JohannesGrcevic/Autumn-Leaves-Identification-System";
    }

    @FXML
    private void openImageFile() {
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
        fileChooser.setTitle("Open Image");

        File file = fileChooser.showOpenDialog(borderPane.getScene().getWindow());

        if (file != null && file.exists() && file.isFile()) {
            imageFile = file;

            // resize the image to fit the center pane
            resizedImage = new Image(String.valueOf(imageFile.toURI()), centerPane.getWidth(), centerPane.getHeight(), false, false);
            imageView.setImage(resizedImage);
        }
    }

    @FXML
    private void showBlackAndWhite() {
        if (!isImageFileLoaded()) {
            showAlert(ALERT_NO_IMAGE_LOADED, AlertType.ERROR);
            return;
        }

        if (!hasCustomColorsSelected(colorPicker)) {
            showAlert(ALERT_NO_COLORS_SELECTED, AlertType.ERROR);
            return;
        }

        int width = (int) blackAndWhiteImage.getWidth();
        int height = (int) blackAndWhiteImage.getHeight();

        Pane pane = new Pane(new ImageView(blackAndWhiteImage));
        pane.setPrefSize(width, height);
        FXUtils.showPopupWindow("Black and White", pane, pane.getPrefWidth(), pane.getPrefHeight(), false);
    }

    @FXML
    private void showRandomColors() {
        if (!isImageFileLoaded()) {
            showAlert(ALERT_NO_IMAGE_LOADED, AlertType.ERROR);
            return;
        }

        if (!hasCustomColorsSelected(colorPicker)) {
            showAlert(ALERT_NO_COLORS_SELECTED, AlertType.ERROR);
            return;
        }

        Random rand = new Random();
        WritableImage coloredImage = new WritableImage(
                blackAndWhiteImage.getPixelReader(),
                (int) blackAndWhiteImage.getWidth(),
                (int) blackAndWhiteImage.getHeight()
        );

        int width = (int) coloredImage.getWidth();
        int height = (int) coloredImage.getHeight();
        PixelWriter writer = coloredImage.getPixelWriter();
        boolean[] coloredNodes = new boolean[width * height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int index = y * width + x;
                int rootNode = nodeController.getNodeRoot(index);

                if (!nodeController.isValidNode(rootNode) || coloredNodes[rootNode]) {
                    continue;
                }

                PixelNode node = nodeController.getNode(width, height, x, y);
                Color randomColor = ColorUtils.getRandomColor(rand);

                for (Integer pixel : node.pixels()) {
                    int pixelX = pixel % width;
                    int pixelY = pixel / width;
                    writer.setColor(pixelX, pixelY, randomColor);
                }

                coloredNodes[rootNode] = true;
            }
        }

        Pane pane = new Pane(new ImageView(coloredImage));
        pane.setPrefSize(width, height);
        FXUtils.showPopupWindow("Random Colors", pane, pane.getPrefWidth(), pane.getPrefHeight(), false);
    }

    @FXML
    private void showNodes() {
        if (!isImageFileLoaded()) {
            showAlert(ALERT_NO_IMAGE_LOADED, AlertType.ERROR);
            return;
        }

        if (!hasCustomColorsSelected(colorPicker)) {
            showAlert(ALERT_NO_COLORS_SELECTED, AlertType.ERROR);
            return;
        }

        // draw node boundary rectangles
        NodeBoundsController controller = new NodeBoundsController(blackAndWhiteImage.getWidth(), blackAndWhiteImage.getHeight(), nodeController);
        Pane nodeBoundsPane = controller.drawNodeBounds(resizedImage, Color.BLACK, Color.BLUE, 2, null);

        // display the node popup window
        Stage stage = FXUtils.showPopupWindow("Bounds | Press 'N' for numbering", nodeBoundsPane, nodeBoundsPane.getPrefWidth(), nodeBoundsPane.getPrefHeight(), false);
        controller.setTargetScene(stage.getScene());

        // show the number of nodes on the status bar
        setStatusBar("Leaf/Node Count: " + nodeController.getNodeCount(), true);
    }

    @FXML
    private void autoSelectColors() {
        // clear the current color selection
        clearColorPicker();

        // convert to a square image for faster processing
        Image squareImage = new Image(String.valueOf(imageFile.toURI()), 512, 512, true, false);

        int width = (int) squareImage.getWidth();
        int height = (int) squareImage.getHeight();
        PixelReader reader = squareImage.getPixelReader();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Color pixelColor = reader.getColor(x, y);

                // add the color to the color picker if it is an autumn leaf
                if (ImageUtils.isAutumnLeaf(pixelColor)) {
                    addCustomColor(colorPicker, pixelColor);
                }
            }
        }
    }

    @FXML
    private void clearColorPicker() {
        getCustomColors(colorPicker).clear();
        colorPicker.getCustomColors().clear();
        colorPicker.setValue(Color.WHITE);
    }

    @FXML
    private void clearImage() {
        if (!isImageFileLoaded()) return;
        if (!showAlert("Are you sure you want to clear the current image?", AlertType.CONFIRMATION).equals(ButtonType.OK))
            return;

        imageFile = null;
        imageView.setImage(null);
        nodeController = null;

        setStatusBar(ALERT_NO_IMAGE_LOADED, true);
        FXUtils.SetBorderStyle(centerPane, imageBorderStroke);
        clearColorPicker();
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
    private void browseAboutPage() {
        try {
            java.awt.Desktop.getDesktop().browse(URI.create(GITHUB_URL));
        } catch (Exception e) {
            System.err.print("Error opening about page: " + e);
        }
    }

    // Events //

    protected void onImageChanged(ObservableValue<? extends Image> value, Image oldValue, Image newValue) {
        if (newValue != null && !newValue.isError()) {
            setStatusBar("Image: " + imageFile.getName(), true);

            // hide the border after the image is loaded
            centerPane.setBorder(null);

            // auto select colors if none are selected
            autoSelectColors();

            createNodesFromImage(resizedImage);
        }
    }

    protected void onColorSelectionChanged(ObservableValue<? extends Color> value, Color oldValue, Color newValue) {
        if (!isImageFileLoaded()) return;

        createNodesFromImage(resizedImage);
    }

    protected void onAutoSelectColorsButtonClicked(ActionEvent event) {
        if (!isImageFileLoaded()) return;

        autoSelectColors();
        createNodesFromImage(resizedImage);
    }

    protected void onImageViewMouseClicked(MouseEvent event) {
        if (!isImageFileLoaded()) return;

        showNodeTooltip((Node) event.getSource(), event.getScreenX(), event.getScreenY(), true, true);
    }

    // Initialization //
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        centerPane = (Pane) borderPane.getCenter();

        imageBorderStroke = new BorderStroke(Color.BLACK, BorderStrokeStyle.DASHED, new CornerRadii(3), new BorderWidths(2));
        FXUtils.SetBorderStyle(centerPane, imageBorderStroke);

        colorPicker.valueProperty().addListener(this::onColorSelectionChanged);
        imageView.imageProperty().addListener(this::onImageChanged);

        imageView.setOnMouseClicked(this::onImageViewMouseClicked);
        autoSelectColorsButton.setOnAction(this::onAutoSelectColorsButtonClicked);
    }

    // Utility //

    public void showNodeTooltip(Node owner, double screenX, double screenY, boolean autoHide, boolean autoHideOnEscape) {
        // get local mouse coordinates
        Point2D screenLocalCoords = imageView.screenToLocal(screenX, screenY);

        int pixelCount = nodeController.getNodePixelCount(blackAndWhiteImage, screenLocalCoords);
        int sequenceNumber = nodeController.getNodeSequenceNumber(blackAndWhiteImage, screenLocalCoords);

        // hide the tooltip if no node is found (e.g., clicking on an area of grass)
        if (pixelCount <= 0 || sequenceNumber < 0) {
            nodeTooltip.hide();
            return;
        }

        nodeTooltip.setText("""
                Leaf/Node Number: %d
                Estimated Size (pixel units): %d
                """.formatted(sequenceNumber, pixelCount));

        nodeTooltip.setHideOnEscape(autoHideOnEscape);
        nodeTooltip.setAutoHide(autoHide);
        nodeTooltip.show(owner, screenX, screenY);
    }

    private void createNodesFromImage(Image source) {
        WritableImage blackAndWhite = getBlackAndWhite(source, getCustomColors(colorPicker));
        if (blackAndWhite == null) return;

        int width = (int) blackAndWhite.getWidth();
        int height = (int) blackAndWhite.getHeight();

        PixelReader reader = blackAndWhite.getPixelReader();

        // clear the previous node controller
        if (nodeController != null) {
            nodeController = null;
        }

        // create nodes
        nodeController = new NodeController(width, height, MIN_NODE_SIZE);
        nodeController.unionNeighboringPixels(width, height, reader, Color.BLACK);
    }

    private WritableImage getBlackAndWhite(Image source, List<Color> identificationColors) {
        return blackAndWhiteImage = ImageUtils.getColorSeparated(source, identificationColors, Color.WHITE);
    }

    private void setStatusBar(String message, boolean visible) {
        statusLabel.setText(message);
        statusLabel.setVisible(visible);
    }

    private void addCustomColor(ColorPicker picker, Color color) {
        List<Color> customColors = getCustomColors(picker);

        // prevent the ui from going out of bounds
        if (customColors.size() >= MAX_CUSTOM_COLORS) return;

        if (!customColors.contains(color)) {
            customColors.add(color);
        }
    }

    private List<Color> getCustomColors(ColorPicker picker) {
        List<Color> singleColorList = FXCollections.observableArrayList(picker.getValue());

        return picker.getValue().equals(Color.WHITE) ? picker.getCustomColors() : singleColorList;
    }

    private boolean hasCustomColorsSelected(ColorPicker picker) {
        List<Color> customColors = getCustomColors(picker);

        return !customColors.isEmpty() && !customColors.getFirst().equals(Color.WHITE);
    }

    private boolean isImageFileLoaded() {
        return imageFile != null;
    }
}
