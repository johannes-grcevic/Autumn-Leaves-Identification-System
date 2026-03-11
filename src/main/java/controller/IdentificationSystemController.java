package controller;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;

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
import javafx.scene.input.MouseButton;
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
    private BorderStroke imageBorderStroke;

    private final Tooltip nodeTooltip;
    private final ContextMenu nodeSelectionMenu;
    private final MenuItem clearSelectionMenuItem, addToColorPickerMenuItem;

    private File imageFile;
    private String imageURL;
    private Image colorImage;
    private Image squareImage;
    private WritableImage blackAndWhiteImage;
    private int fixedWidth;
    private int fixedHeight;

    private NodeController nodeController;
    private PixelNode currentSelectedNode;

    private final int MIN_NODE_SIZE;
    private final int MAX_CUSTOM_COLORS;

    private final String GITHUB_URL;
    private final String ALERT_NO_IMAGE_LOADED;
    private final String ALERT_NO_COLORS_SELECTED;

    public IdentificationSystemController() {
        MIN_NODE_SIZE = 55;
        MAX_CUSTOM_COLORS = 120;
        GITHUB_URL = "https://github.com/JohannesGrcevic/Autumn-Leaves-Identification-System";
        ALERT_NO_IMAGE_LOADED = "No Image Loaded!";
        ALERT_NO_COLORS_SELECTED = "No Colors Selected!";

        currentSelectedNode = PixelNode.getEmpty();
        nodeTooltip = new Tooltip();

        nodeSelectionMenu = new ContextMenu();
        clearSelectionMenuItem = new MenuItem("Clear Selection");
        addToColorPickerMenuItem = new MenuItem("Add to Color Picker");
        nodeSelectionMenu.getItems().addAll(clearSelectionMenuItem, addToColorPickerMenuItem);
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

        if (file == null || !file.exists() || !file.isFile()) {
            return;
        }

        imageFile = file;
        imageURL = String.valueOf(imageFile.toURI());

        // store a square image to be used for faster processing
        double width = 120.0;
        double height = 120.0;
        squareImage = new Image(imageURL, width, height, false, false);

        // resize the original image to fit the center pane
        colorImage = new Image(imageURL, centerPane.getWidth(), centerPane.getHeight(), false, false);
        fixedWidth = (int) colorImage.getWidth();
        fixedHeight = (int) colorImage.getHeight();
        imageView.setImage(colorImage);
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

        Pane pane = new Pane(new ImageView(blackAndWhiteImage));
        pane.setPrefSize(fixedWidth, fixedHeight);
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
        WritableImage randomColoredImage = new WritableImage(blackAndWhiteImage.getPixelReader(), fixedWidth, fixedHeight);

        int width = (int) randomColoredImage.getWidth();
        int height = (int) randomColoredImage.getHeight();

        List<PixelNode> validNodes = nodeController.getValidNodes();

        for (PixelNode node : validNodes) {
            Color randomColor = ColorUtils.getRandomColor(rand);
            setNodePixelColor(randomColoredImage, node, randomColor);
        }

        Pane pane = new Pane(new ImageView(randomColoredImage));
        pane.setPrefSize(width, height);
        FXUtils.showPopupWindow("Random Colors", pane, pane.getPrefWidth(), pane.getPrefHeight(), false);
    }

    @FXML
    private void showNodeBounds() {
        if (!isImageFileLoaded()) {
            showAlert(ALERT_NO_IMAGE_LOADED, AlertType.ERROR);
            return;
        }

        if (!hasCustomColorsSelected(colorPicker)) {
            showAlert(ALERT_NO_COLORS_SELECTED, AlertType.ERROR);
            return;
        }

        // draw node boundary rectangles
        NodeBoundsController controller = new NodeBoundsController(fixedWidth, fixedHeight, nodeController);
        Pane nodeBoundsPane = controller.drawNodeBounds(colorImage, Color.BLACK, Color.BLUE, 2, null);

        // display the node popup window
        Stage stage = FXUtils.showPopupWindow("Bounds | Press 'N' for numbering", nodeBoundsPane, nodeBoundsPane.getPrefWidth(), nodeBoundsPane.getPrefHeight(), false);
        controller.setTargetScene(stage.getScene());

        // show the number of nodes on the status bar
        setStatusBar("Leaf Count: " + nodeController.getNodeCount(), true);
    }

    @FXML
    private void autoSelectColors() {
        // clear the current color selection
        clearColorPicker();

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
        if (!isImageFileLoaded()) return;

        if (newValue != null && !newValue.isError()) {
            setStatusBar("Image: " + imageFile.getName(), true);

            // hide the border after the image is loaded
            centerPane.setBorder(null);

            // auto select colors if none are selected
            autoSelectColors();
            createNodesFromImage(colorImage);

            // clear the color picker empty after loading an image
            if (currentSelectedNode.isEmpty()) {
                clearColorPicker();
            }
        }
    }

    protected void onColorSelectionChanged(ObservableValue<? extends Color> value, Color oldValue, Color newValue) {
        if (!isImageFileLoaded()) return;

        createNodesFromImage(colorImage);
    }

    protected void onAutoSelectColorsButtonClicked(ActionEvent event) {
        if (!isImageFileLoaded()) return;

        autoSelectColors();
        createNodesFromImage(colorImage);
    }

    protected void onImageViewMouseClicked(MouseEvent event) {
        if (!isImageFileLoaded()) return;

        if (event.getButton() == MouseButton.PRIMARY) {
            Point2D screenLocalCoords = imageView.screenToLocal(event.getScreenX(), event.getScreenY());
            currentSelectedNode = nodeController.getNode(imageView.getImage(), screenLocalCoords);

            selectNode(currentSelectedNode, Color.BLUE);
            showNodeTooltip((Node) event.getSource(), event.getScreenX(), event.getScreenY(), true, true);

            if (nodeSelectionMenu.isShowing()) {
                nodeSelectionMenu.hide();
            }
        }

        if (event.getButton() == MouseButton.SECONDARY) {
            if (nodeTooltip.isShowing()) {
                nodeTooltip.hide();
            }

            if (currentSelectedNode.isEmpty()) {
                nodeSelectionMenu.hide();
                return;
            }

            nodeSelectionMenu.setHideOnEscape(true);
            nodeSelectionMenu.setAutoHide(true);
            nodeSelectionMenu.show((Node) event.getSource(), event.getScreenX(), event.getScreenY());
        }
    }

    protected void selectNode(PixelNode node, Color color) {
        imageView.setImage(setNodePixelColor(colorImage, node, color));
        blackAndWhiteImage = setNodePixelColor(blackAndWhiteImage, node, color);
    }

    protected void onNodeSelectionClearClicked(ActionEvent event) {
        imageView.setImage(new Image(imageURL, fixedWidth, fixedHeight, false, false));
    }

    protected void onAddToColorPickerClicked(ActionEvent event) {
        if (currentSelectedNode.isEmpty()) return;

        for (Integer pixel : currentSelectedNode.pixels()) {
            int x = pixel % fixedWidth;
            int y = pixel / fixedWidth;

            Color pixelColor = colorImage.getPixelReader().getColor(x, y);
            addCustomColor(colorPicker, pixelColor);
        }
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
        clearSelectionMenuItem.setOnAction(this::onNodeSelectionClearClicked);
        addToColorPickerMenuItem.setOnAction(this::onAddToColorPickerClicked);
    }

    // Utility //

    public void showNodeTooltip(Node owner, double screenX, double screenY, boolean autoHide, boolean autoHideOnEscape) {
        // get local mouse coordinates
        Point2D screenLocalCoords = imageView.screenToLocal(screenX, screenY);

        PixelNode selectedNode = nodeController.getNode(blackAndWhiteImage, screenLocalCoords);
        int sequenceNumber = nodeController.getNodeSequenceNumber(selectedNode);
        int pixelCount = selectedNode.getPixelCount();

        // hide the tooltip if no node is found (e.g., clicking on an area of grass)
        if (pixelCount <= 0 || sequenceNumber <= 0) {
            nodeTooltip.hide();
            return;
        }

        Label content = new Label("""
                Leaf Number: %d
                Size (pixel units): %d
                """.formatted(sequenceNumber, pixelCount));
        content.setMouseTransparent(true);

        nodeTooltip.setGraphic(content);
        nodeTooltip.setHideOnEscape(autoHideOnEscape);
        nodeTooltip.setAutoHide(autoHide);
        nodeTooltip.show(owner, screenX, screenY);
    }

    public WritableImage setNodePixelColor(WritableImage source, PixelNode node, Color color) {
        int width = (int) source.getWidth();
        PixelWriter writer = source.getPixelWriter();

        for (Integer pixel : node.pixels()) {
            int x = pixel % width;
            int y = pixel / width;

            writer.setColor(x, y, color);
        }

        return source;
    }

    public Image setNodePixelColor(Image source, PixelNode node, Color color) {
        int width = (int) source.getWidth();
        int height = (int) source.getHeight();
        PixelReader reader = source.getPixelReader();

        return setNodePixelColor(new WritableImage(reader, width, height), node, color);
    }

    private void createNodesFromImage(Image source) {
        blackAndWhiteImage = ImageUtils.getBlackAndWhite(source, getCustomColors(colorPicker));

        int width = (int) blackAndWhiteImage.getWidth();
        int height = (int) blackAndWhiteImage.getHeight();
        PixelReader reader = blackAndWhiteImage.getPixelReader();

        // clear the previous node controller
        if (nodeController != null) {
            nodeController = null;
        }

        // create nodes
        nodeController = new NodeController(width * height);
        nodeController.createNodes(width, height, MIN_NODE_SIZE, reader, Color.BLACK);
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
