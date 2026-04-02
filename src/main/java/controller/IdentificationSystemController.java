package controller;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
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
import javafx.util.Duration;

import main.App;
import model.PixelNode;
import util.ColorUtils;
import util.FXUtils;
import util.ImageUtils;

import java.io.File;
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

    private final Tooltip nodeTooltip;
    private final ContextMenu nodeContextMenu;
    private final MenuItem addToColorPickerMenuItem;

    private Pane imagePane;
    private File imageFile;
    private WritableImage displayedImage;
    private WritableImage displayBlackAndWhiteImage;
    private WritableImage unselectedImage;
    private WritableImage unselectedBlackAndWhiteImage;
    private int fixedWidth;
    private int fixedHeight;

    private NodeController nodeController;
    private PixelNode clickedNode;
    private PixelNode selectedNode;

    private final Random randomGenerator = new Random();

    private final int MIN_NODE_SIZE;
    private final int MAX_CUSTOM_COLORS;

    private final String ALERT_NO_IMAGE_LOADED;
    private final String ALERT_NO_COLORS_SELECTED;
    private final String ALERT_NO_NODE_SELECTED;

    public IdentificationSystemController() {
        // the minimum size for a leaf node to be considered valid
        MIN_NODE_SIZE = 55;
        // the maximum number of custom colors that can be selected by the user
        MAX_CUSTOM_COLORS = 120;

        ALERT_NO_IMAGE_LOADED = "No Image Loaded!";
        ALERT_NO_COLORS_SELECTED = "No Colors Selected!";
        ALERT_NO_NODE_SELECTED = "No Leaf Selected!";

        nodeTooltip = new Tooltip();
        nodeTooltip.setAutoHide(true);

        nodeContextMenu = new ContextMenu();
        addToColorPickerMenuItem = new MenuItem("Add to Color Picker");
        nodeContextMenu.getItems().add(addToColorPickerMenuItem);
    }

    @FXML
    private void loadImageFile() {
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

        // check if the file is valid
        if (file == null || !file.exists() || !file.isFile()) {
            return;
        }

        imageFile = file;
        String imageURL = String.valueOf(imageFile.toURI());

        // set the image dimensions based on the image center pane size
        fixedWidth = (int) imagePane.getWidth();
        fixedHeight = (int) imagePane.getHeight();

        // initialize the node controller with the image dimensions
        nodeController = new NodeController(fixedWidth * fixedHeight);

        // resize the original image to fit the center pane
        Image originalColorImage = new Image(imageURL, fixedWidth, fixedHeight, false, false);

        // initialize the image view with the display image
        displayedImage = new WritableImage(originalColorImage.getPixelReader(), fixedWidth, fixedHeight);
        imageView.setImage(displayedImage);
    }

    @FXML
    private void showBlackAndWhite() {
        if (!isImageFileLoaded()) {
            showAlert(ALERT_NO_IMAGE_LOADED, AlertType.ERROR);
            return;
        }

        if (!hasCustomColorsSelected()) {
            showAlert(ALERT_NO_COLORS_SELECTED, AlertType.ERROR);
            return;
        }

        Pane pane = new Pane(new ImageView(displayBlackAndWhiteImage));
        pane.setPrefSize(fixedWidth, fixedHeight);
        FXUtils.showPopupWindow("Black and White", pane, pane.getPrefWidth(), pane.getPrefHeight(), false);
    }

    @FXML
    private void showRandomColors() {
        if (!isImageFileLoaded()) {
            showAlert(ALERT_NO_IMAGE_LOADED, AlertType.ERROR);
            return;
        }

        if (!hasCustomColorsSelected()) {
            showAlert(ALERT_NO_COLORS_SELECTED, AlertType.ERROR);
            return;
        }

        WritableImage randomColoredImage = new WritableImage(displayBlackAndWhiteImage.getPixelReader(), fixedWidth, fixedHeight);

        PixelNode[] validNodes = nodeController.getNodes();

        for (PixelNode node : validNodes) {
            Color randomColor = ColorUtils.getRandomColor(randomGenerator);
            setNodePixelColor(randomColoredImage, node, randomColor);
        }

        Pane pane = new Pane(new ImageView(randomColoredImage));
        pane.setPrefSize(fixedWidth, fixedHeight);
        FXUtils.showPopupWindow("Random Colors", pane, pane.getPrefWidth(), pane.getPrefHeight(), false);
    }

    @FXML
    private void showNodeBounds() {
        if (!isImageFileLoaded()) {
            showAlert(ALERT_NO_IMAGE_LOADED, AlertType.ERROR);
            return;
        }

        if (!hasCustomColorsSelected()) {
            showAlert(ALERT_NO_COLORS_SELECTED, AlertType.ERROR);
            return;
        }

        Pane drawPane = new Pane(new ImageView(displayedImage));
        drawPane.setPrefSize(fixedWidth, fixedHeight);

        // draw node boundary rectangles
        NodeBoundsController controller = new NodeBoundsController(drawPane, nodeController.getNodes(), fixedWidth, fixedHeight);
        controller.drawNodeBounds(Color.BLACK, Color.BLUE, 8, 2, null);

        // display the bound popup window
        Stage stage = FXUtils.showPopupWindow("Bounds | Press 'N' for numbering", drawPane, drawPane.getPrefWidth(), drawPane.getPrefHeight(), false);
        controller.setTargetScene(stage.getScene());
        stage.getScene().getStylesheets().add(Objects.requireNonNull(App.class.getResource("style.css")).toExternalForm());
    }

    @FXML
    private void showAnimatedConnectingPath() {
        if (!isImageFileLoaded()) {
            showAlert(ALERT_NO_IMAGE_LOADED, AlertType.ERROR);
            return;
        }

        PixelNode[] validNodes = nodeController.getNodes();
        Point2D[] nodeCenterPoints = new Point2D[validNodes.length];

        for (int i = 0; i < validNodes.length; i++) {
            nodeCenterPoints[i] = validNodes[i].getCenter();
        }

        if (selectedNode == null) {
            showAlert(ALERT_NO_NODE_SELECTED, AlertType.ERROR);
            return;
        }

        Pane drawPane = new Pane(new ImageView(unselectedImage));
        drawPane.setPrefSize(fixedWidth, fixedHeight);

        // display the animated path popup window
        Stage stage = FXUtils.showPopupWindow("Path | Press 'N' for numbering", drawPane, drawPane.getPrefWidth(), drawPane.getPrefHeight(), false);
        stage.getScene().getStylesheets().add(Objects.requireNonNull(App.class.getResource("style.css")).toExternalForm());

        // initialize the node bounds controller
        NodeBoundsController boundsController = new NodeBoundsController(drawPane, validNodes, fixedWidth, fixedHeight);
        boundsController.setTargetScene(stage.getScene());

        // draw node boundary rectangles
        boundsController.drawNodeBounds(Color.BLACK, Color.BLUE, 8, 2, null);

        // draw the connecting path
        AnimatedPathController pathController = new AnimatedPathController(nodeCenterPoints);
        pathController.drawAnimatedPath(selectedNode.getCenter(), drawPane, Duration.seconds(5), Color.RED, 1);

        // clear the current selection from the displayed image
        clearNodeSelection();
    }

    @FXML
    private void autoSelectColors() {
        // clear the current selection
        clearColorSelection();
        clearNodeSelection();

        PixelReader reader = displayedImage.getPixelReader();

        for (int x = 0; x < fixedWidth; x++) {
            for (int y = 0; y < fixedHeight; y++) {
                Color pixelColor = reader.getColor(x, y);

                // add the color to the color picker if it is an autumn leaf
                if (ImageUtils.isAutumnLeaf(pixelColor)) {
                    addCustomColor(pixelColor);
                }
            }
        }
    }

    @FXML
    private void clearColorSelection() {
        getCustomColors().clear();
        colorPicker.getCustomColors().clear();
        colorPicker.setValue(Color.WHITE);

        // show the filename on the status bar
        if (!isImageFileLoaded()) return;
        setStatusBar("File: " + imageFile.getName(), true);
    }

    @FXML
    private void clearImage() {
        if (!isImageFileLoaded()) return;

        // Only clear the image if the user selects ok
        if (!showAlert("Are you sure you want to clear the loaded image?", AlertType.CONFIRMATION).equals(ButtonType.OK))
            return;

        // clear image data
        if (nodeController != null) {
            nodeController.clearNodes();
        }

        // clear image
        imageFile = null;
        imageView.setImage(null);

        // clear image data
        displayedImage = null;
        displayBlackAndWhiteImage = null;
        unselectedImage = null;
        unselectedBlackAndWhiteImage = null;
        fixedWidth = 0;
        fixedHeight = 0;

        // clear node controller and node data
        clickedNode = null;
        selectedNode = null;

        setStatusBar(ALERT_NO_IMAGE_LOADED, true);
        // show the border after the image is cleared
        FXUtils.SetBorderStyle(imagePane, Color.BLACK, BorderStrokeStyle.DASHED, 3, 2);

        // clear the color selection
        clearColorSelection();
    }

    @FXML
    private void quitApplication() {
        // Only quit if the user selects ok
        if (!showAlert("Are you sure you want to quit?", AlertType.CONFIRMATION).equals(ButtonType.OK))
            return;

        // Close the application
        System.exit(0);
    }

    // Events //
    protected void onImageChanged(ObservableValue<? extends Image> value, Image oldValue, Image newValue) {
        if (!isImageFileLoaded()) return;

        if (newValue != null && !newValue.isError()) {
            // show the filename on the status bar
            setStatusBar("File: " + imageFile.getName(), true);

            // hide the border after the image is loaded
            imagePane.setBorder(null);

            autoSelectColors();
            createNodesFromImage(displayedImage);

            // clear the color picker after loading a new image
            if (selectedNode == null) {
                clearColorSelection();
            }
        }
    }

    protected void onColorValueChanged(ObservableValue<? extends Color> value, Color oldValue, Color newValue) {
        if (!isImageFileLoaded()) return;

        createNodesFromImage(displayedImage);

        // show the number of nodes on the status bar
        setStatusBar("Leaf Count: " + nodeController.getNodeCount(), true);
    }

    protected void onCustomColorsListChanged(ListChangeListener.Change<? extends Color> newValue) {
        if (!isImageFileLoaded()) return;

        // show the number of nodes on the status bar
        setStatusBar("Leaf Count: " + nodeController.getNodeCount(), true);
    }

    protected void onAutoSelectButtonClicked(ActionEvent event) {
        if (!isImageFileLoaded()) return;

        autoSelectColors();
        createNodesFromImage(displayedImage);
    }

    protected void onImageViewMouseClicked(MouseEvent event) {
        if (!isImageFileLoaded() || displayedImage == null || nodeController == null) {
            return;
        }

        // get the clicked position in the image
        Point2D clickPosition = imageView.screenToLocal(event.getScreenX(), event.getScreenY());

        // set the current clicked node
        clickedNode = nodeController.getNode(imageView.getImage(), clickPosition);

        // when the left mouse button is pressed, select the clicked node
        if (event.getButton() == MouseButton.PRIMARY) {
            if (!clickedNode.isValid()) {
                nodeTooltip.hide();
                nodeContextMenu.hide();

                // clear the selection if the clicked node is not valid
                if (selectedNode != null) {
                    clearNodeSelection();
                }
            }
            else
            {
                // set the clicked node as selected
                if (selectedNode == null) {
                    selectNode(clickedNode, Color.BLUE);
                }

                nodeContextMenu.hide();
                showNodeTooltip((Node) event.getSource(), event.getScreenX(), event.getScreenY());
            }
        }

        if (event.getButton() == MouseButton.SECONDARY) {
            if (!clickedNode.equals(selectedNode)) return;

            // hide the tooltip if it is showing
            // this is necessary so that tooltip will be hidden when the context menu is shown
            nodeTooltip.hide();

            // display the context menu on right click of the mouse
            nodeContextMenu.show((Node) event.getSource(), event.getScreenX(), event.getScreenY());
        }
    }

    protected void onAddToColorPickerClicked(ActionEvent event) {
        if (selectedNode == null) return;

        for (Integer pixel : selectedNode.pixels()) {
            int x = pixel % fixedWidth;
            int y = pixel / fixedWidth;

            Color pixelColor = unselectedImage.getPixelReader().getColor(x, y);
            addCustomColor(pixelColor);
        }
    }

    // Utility //
    public void showNodeTooltip(Node source, double x, double y) {
        int sequenceNumber = nodeController.getNodeSequenceNumber(clickedNode);
        int pixelCount = clickedNode.getPixelCount();

        Label content = new Label("""
                Leaf Number: %d
                Estimated Size (pixel units): %d
                """.formatted(sequenceNumber, pixelCount));
        content.setMouseTransparent(true);

        nodeTooltip.setGraphic(content);
        nodeTooltip.show(source, x, y);
    }

    public void setNodePixelColor(WritableImage source, PixelNode node, Color color) {
        PixelWriter writer = source.getPixelWriter();

        for (Integer pixel : node.pixels()) {
            int x = pixel % fixedWidth;
            int y = pixel / fixedWidth;

            writer.setColor(x, y, color);
        }
    }

    public void selectNode(PixelNode node, Color color) {
        unselectedImage = new WritableImage(displayedImage.getPixelReader(), fixedWidth, fixedHeight);
        unselectedBlackAndWhiteImage = new WritableImage(displayBlackAndWhiteImage.getPixelReader(), fixedWidth, fixedHeight);

        setNodePixelColor(displayedImage, node, color);
        setNodePixelColor(displayBlackAndWhiteImage, node, color);

        selectedNode = node;
    }

    public void clearNodeSelection() {
        if (unselectedImage == null && unselectedBlackAndWhiteImage == null) return;

        PixelWriter writer = displayedImage.getPixelWriter();
        PixelReader reader = unselectedImage.getPixelReader();

        PixelWriter blackAndWhiteWriter = displayBlackAndWhiteImage.getPixelWriter();
        PixelReader blackAndWhiteReader = unselectedBlackAndWhiteImage.getPixelReader();

        // restore original image pixels
        for (int x = 0; x < fixedWidth; x++) {
            for (int y = 0; y < fixedHeight; y++) {
                writer.setColor(x, y, reader.getColor(x, y));
                blackAndWhiteWriter.setColor(x, y, blackAndWhiteReader.getColor(x, y));
            }
        }

        selectedNode = null;
        unselectedImage = null;
        unselectedBlackAndWhiteImage = null;
    }

    public void createNodesFromImage(WritableImage source) {
        // black and white image based on user-selected colors
        displayBlackAndWhiteImage = ImageUtils.getBlackAndWhite(source, getCustomColors(), 0.3, 0.2);
        PixelReader reader = displayBlackAndWhiteImage.getPixelReader();

        // clear the node controller if it already has nodes
        if (nodeController.getNodeCount() > 0) {
            nodeController.clearNodes();
        }

        // create clusters from the black and white image
        nodeController.createNodes(fixedWidth, fixedHeight, MIN_NODE_SIZE, reader, Color.BLACK);

        // create a new black and white image with the processed pixels
        PixelWriter writer = displayBlackAndWhiteImage.getPixelWriter();

        // fill the image with black pixels
        for (int y = 0; y < fixedHeight; y++) {
            for (int x = 0; x < fixedWidth; x++) {
                writer.setColor(x, y, Color.BLACK);
            }
        }

        for (PixelNode node : nodeController.getNodes()) {
            setNodePixelColor(displayBlackAndWhiteImage, node, Color.WHITE);
        }
    }

    public void setStatusBar(String message, boolean visible) {
        statusLabel.setText(message);
        statusLabel.setVisible(visible);
    }

    public void addCustomColor(Color color) {
        List<Color> customColors = getCustomColors();

        // prevent the ui from going out of bounds
        if (customColors.size() >= MAX_CUSTOM_COLORS) return;

        if (!customColors.contains(color)) {
            customColors.add(color);
        }
    }

    public List<Color> getCustomColors() {
        ColorPicker picker = colorPicker;
        List<Color> singleColorList = FXCollections.observableArrayList(picker.getValue());

        return picker.getValue().equals(Color.WHITE) ? picker.getCustomColors() : singleColorList;
    }

    public boolean hasCustomColorsSelected() {
        List<Color> customColors = getCustomColors();

        return !customColors.isEmpty() && !customColors.getFirst().equals(Color.WHITE);
    }

    public boolean isImageFileLoaded() {
        return imageFile != null;
    }

    // Initialization //
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        imagePane = (Pane) borderPane.getCenter();
        FXUtils.SetBorderStyle(imagePane, Color.BLACK, BorderStrokeStyle.DASHED, 3, 2);

        colorPicker.valueProperty().addListener(this::onColorValueChanged);
        colorPicker.getCustomColors().addListener(this::onCustomColorsListChanged);

        imageView.imageProperty().addListener(this::onImageChanged);

        imageView.setOnMouseClicked(this::onImageViewMouseClicked);
        autoSelectColorsButton.setOnAction(this::onAutoSelectButtonClicked);
        addToColorPickerMenuItem.setOnAction(this::onAddToColorPickerClicked);
    }
}
