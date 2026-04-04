package me.johannes.autumn.controller;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;

import me.johannes.autumn.main.App;
import me.johannes.autumn.model.MyArrayList;
import me.johannes.autumn.model.PixelNode;
import me.johannes.autumn.util.ColorUtils;
import me.johannes.autumn.util.FXUtils;
import me.johannes.autumn.util.ImageUtils;
import me.johannes.autumn.util.StringUtils;

import java.io.File;
import java.net.URL;
import java.util.*;

import static me.johannes.autumn.util.FXUtils.showAlert;
import static me.johannes.autumn.util.FXUtils.showConfirmationAlert;

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
    @FXML
    private Button clearColorSelectionButton;

    private final Tooltip nodeTooltip;
    private final ContextMenu nodeContextMenu;
    private final MenuItem addToColorPickerMenuItem;

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
    private Color clickedPixelColor;

    private final Random RANDOM_GENERATOR = new Random();

    private final int MIN_NODE_SIZE;
    private final int MAX_CUSTOM_COLORS;
    private final double HOVER_SCALE_FACTOR;
    private final Duration HOVER_DURATION_MILLISECONDS;

    private final String ALERT_NO_IMAGE_LOADED;
    private final String ALERT_NO_COLORS_SELECTED;
    private final String ALERT_NO_NODE_SELECTED;

    public IdentificationSystemController() {
        // the minimum size for a leaf node to be considered valid
        MIN_NODE_SIZE = 55;
        // the maximum number of custom colors that can be selected by the user
        MAX_CUSTOM_COLORS = 120;
        // the scale factor for buttons when hovered over
        HOVER_SCALE_FACTOR = 1.03;
        // the duration of the button hover effect in milliseconds
        HOVER_DURATION_MILLISECONDS = Duration.millis(200);

        ALERT_NO_IMAGE_LOADED = "Open an Image to get started!";
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
        Pane imageContainer = (Pane) borderPane.getCenter();
        fixedWidth = (int) imageContainer.getWidth();
        fixedHeight = (int) imageContainer.getHeight();

        // resize the original image to fit the center pane
        Image originalColorImage = new Image(imageURL, fixedWidth, fixedHeight, false, false);

        // initialize the image view with the display image
        displayedImage = new WritableImage(originalColorImage.getPixelReader(), fixedWidth, fixedHeight);
        imageView.setImage(displayedImage);

        // initialize the node controller with the image dimensions
        initializeNodeController(fixedWidth * fixedHeight);

        // disable the interactive features of the app until the image is loaded
        setClickableControlsActive(true);
    }

    @FXML
    private void showBlackAndWhite() {
        if (!isImageLoaded()) {
            showAlert(ALERT_NO_IMAGE_LOADED, AlertType.ERROR);
            return;
        }

        if (!hasColorsSelected()) {
            showAlert(ALERT_NO_COLORS_SELECTED, AlertType.ERROR);
            return;
        }

        Pane pane = new Pane(new ImageView(displayBlackAndWhiteImage));
        pane.setPrefSize(fixedWidth, fixedHeight);

        Stage stage = FXUtils.showPopupWindow("Black and White", pane, pane.getPrefWidth(), pane.getPrefHeight(), false);
        stage.getScene().getStylesheets().add(Objects.requireNonNull(App.getStylesheet("style")));
        stage.getIcons().add(App.getIconImage());
    }

    @FXML
    private void showRandomColors() {
        if (!isImageLoaded()) {
            showAlert(ALERT_NO_IMAGE_LOADED, AlertType.ERROR);
            return;
        }

        if (!hasColorsSelected()) {
            showAlert(ALERT_NO_COLORS_SELECTED, AlertType.ERROR);
            return;
        }

        if (selectedNode != null) {
            clearNodeSelection();
        }

        if (displayBlackAndWhiteImage == null) return;

        WritableImage randomColoredImage = new WritableImage(displayBlackAndWhiteImage.getPixelReader(), fixedWidth, fixedHeight);
        List<PixelNode> nodes = nodeController.getNodes();

        for (PixelNode node : nodes) {
            Color randomColor = ColorUtils.getRandomColor(RANDOM_GENERATOR);
            setNodePixelColor(randomColoredImage, node, randomColor);
        }

        Pane pane = new Pane(new ImageView(randomColoredImage));
        pane.setPrefSize(fixedWidth, fixedHeight);

        Stage stage = FXUtils.showPopupWindow("Random Colors", pane, pane.getPrefWidth(), pane.getPrefHeight(), false);
        stage.getScene().getStylesheets().add(Objects.requireNonNull(App.getStylesheet("style")));
        stage.getIcons().add(App.getIconImage());
    }

    @FXML
    private void showNodeBounds() {
        if (!isImageLoaded()) {
            showAlert(ALERT_NO_IMAGE_LOADED, AlertType.ERROR);
            return;
        }

        if (!hasColorsSelected()) {
            showAlert(ALERT_NO_COLORS_SELECTED, AlertType.ERROR);
            return;
        }

        if (selectedNode != null) {
            clearNodeSelection();
        }

        Pane pane = new Pane(new ImageView(displayedImage));
        pane.setPrefSize(fixedWidth, fixedHeight);

        // draw node boundary rectangles
        NodeBoundsController controller = new NodeBoundsController(pane, nodeController.getNodes(), fixedWidth, fixedHeight);
        controller.drawNodeBounds(Color.BLACK, Color.BLUE, 8, 0.5, null);

        // display the bound popup window
        Stage stage = FXUtils.showPopupWindow("Bounds | Press 'N' for numbering", pane, pane.getPrefWidth(), pane.getPrefHeight(), false);
        stage.getScene().getStylesheets().add(Objects.requireNonNull(App.getStylesheet("style")));
        stage.getIcons().add(App.getIconImage());

        controller.setTargetScene(stage.getScene());
    }

    @FXML
    private void showAnimatedConnectingPath() {
        if (!isImageLoaded()) {
            showAlert(ALERT_NO_IMAGE_LOADED, AlertType.ERROR);
            return;
        }

        if (!hasColorsSelected()) {
            showAlert(ALERT_NO_COLORS_SELECTED, AlertType.ERROR);
            return;
        }

        List<PixelNode> nodes = nodeController.getNodes();
        List<Point2D> centerPoints = new MyArrayList<>(nodes.size());

        for (PixelNode node : nodes) {
            centerPoints.add(node.getCenter());
        }

        if (selectedNode == null) {
            showAlert(ALERT_NO_NODE_SELECTED, AlertType.ERROR);
            return;
        }

        Pane pane = new Pane(new ImageView(unselectedImage));
        pane.setPrefSize(fixedWidth, fixedHeight);

        // display the animated path popup window
        Stage stage = FXUtils.showPopupWindow("TSP Path | Press 'N' for numbering", pane, pane.getPrefWidth(), pane.getPrefHeight(), false);
        stage.getScene().getStylesheets().add(Objects.requireNonNull(App.getStylesheet("style")));
        stage.getIcons().add(App.getIconImage());

        // initialize the node bounds controller
        NodeBoundsController controller = new NodeBoundsController(pane, nodes, fixedWidth, fixedHeight);
        controller.setTargetScene(stage.getScene());

        // draw node boundary rectangles
        controller.drawNodeBounds(Color.BLACK, Color.BLUE, 8, 2, null);

        // draw the connecting path
        AnimatedPathController pathController = new AnimatedPathController(centerPoints);
        pathController.drawAnimatedPath(selectedNode.getCenter(), pane, Duration.seconds(5), Color.RED, 1);

        // clear the current selection from the displayed image
        if (selectedNode != null) {
            clearNodeSelection();
        }
    }

    @FXML
    public void clearColorSelection() {
        colorPicker.getCustomColors().clear();
        colorPicker.setValue(Color.WHITE);

        if (nodeController != null && nodeController.hasNodes()) {
            nodeController.clearNodes();
        }

        if (!isImageLoaded()) return;
        // show the filename on the status bar
        setStatusBar("File: " + imageFile.getName(), true);
    }

    @FXML
    private void clearImage() {
        if (!isImageLoaded()) return;

        // Only clear the image if the user selects ok
        if (!showConfirmationAlert("Are you sure you want to clear the Image?").equals(ButtonType.OK))
            return;

        // clear image cluster data
        if (nodeController.hasNodes()) {
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
        setClickableControlsActive(false);

        // clear the color selection
        clearColorSelection();
    }

    @FXML
    private void quitApplication() {
        // Only quit if the user selects ok
        if (!showConfirmationAlert("Are you sure you want to Quit?").equals(ButtonType.OK))
            return;

        // Close the application
        System.exit(0);
    }

    // Events //
    protected void onImageChanged(ObservableValue<?> value, Image oldValue, Image newValue) {
        if (!isImageLoaded()) return;

        if (newValue != null && !newValue.isError()) {
            // show the filename on the status bar
            setStatusBar("File: " + imageFile.getName(), true);

            // clear the color picker after loading a new image
            clearColorSelection();
        }
    }

    protected void onColorPickerClosed(Event event) {
        if (!isImageLoaded()) return;

        if (nodeController.hasNodes()) {
            nodeController.clearNodes();
        }

        createNodesFromImage(displayedImage);
        setStatusBar("Leaf Count: " + nodeController.getNodeCount(), true);

        // refocus the main application window after the color picker is closed
        Platform.runLater(() -> {
            Window mainWindow = borderPane.getScene().getWindow();

            if (mainWindow != null) {
                mainWindow.requestFocus();
            }

            String colorName = ColorUtils.getColorName(colorPicker.getValue());
            if (colorName == null) return;

            // set the color picker label name for colors in the picker
            Label label = (Label) colorPicker.lookup(".color-picker-label");
            label.setText(StringUtils.capitalize(colorName));
        });
    }

    protected void onAddToColorPickerClicked(ActionEvent event) {
        if (clickedPixelColor == null) return;

        addCustomColor(clickedPixelColor);
        String colorName = ColorUtils.getColorName(clickedPixelColor);

        setStatusBar("Added Color: " + StringUtils.capitalize(colorName), true);
    }

    protected void onCustomColorsListChanged(ListChangeListener.Change<?> newValue) {
        if (!isImageLoaded() || newValue == null) return;

        // check if the user has selected more than the maximum number of custom colors
        if (newValue.getList().size() > MAX_CUSTOM_COLORS) {
            showAlert("You can only select up to " + MAX_CUSTOM_COLORS + " custom colors!", AlertType.ERROR);

            // remove the last custom color from the list to prevent the user from selecting more colors
            // this prevents the user from adding more colors than is allowed for the color picker
            newValue.getList().removeLast();

            // close the color picker menu to allow the alert to show on top
            colorPicker.toBack();
            colorPicker.hide();
        }
    }

    protected void onAutoSelectButtonClicked(ActionEvent event) {
        if (!isImageLoaded()) return;

        autoSelectColors();
        createNodesFromImage(displayedImage);
        setStatusBar("Leaf Count: " + nodeController.getNodeCount(), true);
    }

    protected void onImageViewMouseClicked(MouseEvent event) {
        if (!isImageLoaded() || displayedImage == null) {
            return;
        }

        // get the clicked position in the image
        Point2D clickPosition = imageView.screenToLocal(event.getScreenX(), event.getScreenY());
        int x = (int) clickPosition.getX();
        int y = (int) clickPosition.getY();

        // set the current clicked pixel color
        clickedPixelColor = displayedImage.getPixelReader().getColor(x, y);

        // set the current clicked node
        clickedNode = nodeController.getNode(x, y, fixedWidth, fixedHeight);

        // when the left mouse button is pressed, select the clicked node
        if (event.getButton() == MouseButton.PRIMARY) {

            setStatusBar("Clicked Color: " + StringUtils.capitalize(ColorUtils.getColorName(clickedPixelColor)), true);

            nodeContextMenu.hide();

            if (!clickedNode.isValid()) {
                nodeTooltip.hide();

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

                showNodeTooltip((Node) event.getSource(), event.getScreenX(), event.getScreenY());
            }
        }

        if (event.getButton() == MouseButton.SECONDARY) {
            if (clickedPixelColor == null) return;

            // hide the tooltip if it is showing
            // this is necessary so that tooltip will be hidden when the context menu is shown
            nodeTooltip.hide();

            // display the context menu on right click of the mouse
            nodeContextMenu.show((Node) event.getSource(), event.getScreenX(), event.getScreenY());
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

        // make the tooltip invisible to mouse clicks
        content.setMouseTransparent(true);

        nodeTooltip.setGraphic(content);
        nodeTooltip.show(source, x, y);
    }

    public void setNodePixelColor(WritableImage source, PixelNode node, Color color) {
        PixelWriter writer = source.getPixelWriter();

        for (Integer index : node.pixelIndexes()) {
            int x = index % fixedWidth;
            int y = index / fixedWidth;

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

    public void autoSelectColors() {
        // clear the current selection
        clearColorSelection();

        if (selectedNode != null) {
            clearNodeSelection();
        }

        PixelReader reader = displayedImage.getPixelReader();

        for (int x = 0; x < fixedWidth; x++) {
            for (int y = 0; y < fixedHeight; y++) {
                Color pixelColor = reader.getColor(x, y);

                if (pixelColor == null) continue;

                // add the color to the color picker if it is an autumn leaf
                if (ImageUtils.isAutumnLeaf(pixelColor)) {
                    addCustomColor(pixelColor);
                }
            }
        }
    }

    public void createNodesFromImage(WritableImage source) {
        // black and white image based on user-selected colors
        displayBlackAndWhiteImage = ImageUtils.getBlackAndWhite(source,
                hasCustomColorsSelected() ? colorPicker.getCustomColors() : Collections.singletonList(colorPicker.getValue()),
                0.3,
                0.2);

        PixelReader reader = displayBlackAndWhiteImage.getPixelReader();

        // create clusters from the black and white image
        if (!nodeController.hasNodes() && hasColorsSelected()) {
            nodeController.createNodes(fixedWidth, fixedHeight, MIN_NODE_SIZE, reader, Color.BLACK);
        }

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

    public void addCustomColor(Color color) {
        if (color == null || colorPicker.getCustomColors().size() >= MAX_CUSTOM_COLORS) return;

        if (!colorPicker.getCustomColors().contains(color)) {
            colorPicker.getCustomColors().add(color);
        }
    }

    public boolean hasColorsSelected() {
        return colorPicker.getValue() != null || hasCustomColorsSelected();
    }

    public boolean hasCustomColorsSelected() {
        return !colorPicker.getCustomColors().isEmpty();
    }

    public void setStatusBar(String message, boolean visible) {
        if (statusLabel == null) return;

        if (message.isBlank()) {
            throw new IllegalArgumentException("Message cannot be blank!");
        }

        String statusText = "\n" + message;

        statusLabel.setText(statusText);
        statusLabel.setVisible(visible);
    }

    public void setClickableControlsActive(boolean value) {
        autoSelectColorsButton.setDisable(!value);
        clearColorSelectionButton.setDisable(!value);
        colorPicker.setDisable(!value);
    }

    public boolean isImageLoaded() {
        return imageFile != null;
    }

    public void initializeNodeController(int imageSize) {
        nodeController = new NodeController(imageSize);
    }

    // Initialization //
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colorPicker.getCustomColors().addListener(this::onCustomColorsListChanged);
        colorPicker.setOnHidden(this::onColorPickerClosed);

        imageView.imageProperty().addListener(this::onImageChanged);

        imageView.setOnMouseClicked(this::onImageViewMouseClicked);
        autoSelectColorsButton.setOnAction(this::onAutoSelectButtonClicked);
        addToColorPickerMenuItem.setOnAction(this::onAddToColorPickerClicked);

        // add hover scale animation to the buttons
        FXUtils.setHoverScaleAnimation(autoSelectColorsButton, HOVER_SCALE_FACTOR, HOVER_DURATION_MILLISECONDS);
        FXUtils.setHoverScaleAnimation(clearColorSelectionButton, HOVER_SCALE_FACTOR, HOVER_DURATION_MILLISECONDS);
        FXUtils.setHoverScaleAnimation(colorPicker, HOVER_SCALE_FACTOR, HOVER_DURATION_MILLISECONDS);

        setClickableControlsActive(false);
        setStatusBar(ALERT_NO_IMAGE_LOADED, true);
    }
}
