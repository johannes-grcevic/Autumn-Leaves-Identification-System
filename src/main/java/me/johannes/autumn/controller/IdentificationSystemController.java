package me.johannes.autumn.controller;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;

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
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;

import me.johannes.autumn.main.App;
import me.johannes.autumn.model.Bounds;
import me.johannes.autumn.model.MyArrayList;
import me.johannes.autumn.model.PixelNode;
import me.johannes.autumn.util.ColorUtils;
import me.johannes.autumn.util.FXUtils;
import me.johannes.autumn.util.ImageUtils;
import me.johannes.autumn.util.StringUtils;

import java.io.*;
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
    private Button clearColorSelectionButton, autoSelectColorsButton;
    @FXML
    private Button restoreButton, saveButton;
    @FXML
    private Slider saturationSlider, brightnessSlider;
    @FXML
    private Spinner<Integer> nodeSizeSpinner;

    private final Tooltip nodeTooltip;
    private final ContextMenu nodeContextMenu;
    private final MenuItem addToColorPickerMenuItem;

    private File imageFile;
    private WritableImage displayedImage;
    private WritableImage displayBlackAndWhiteImage;
    private WritableImage unselectedBlackAndWhiteImage;
    private int containerWidth;
    private int containerHeight;

    private NodeController nodeController;
    private PixelNode clickedNode;
    private PixelNode selectedNode;
    private Color clickedPixelColor;

    private NodeBoundsController nodeBoundsController;
    private Bounds selectedNodeBoundary;

    public double saturationThreshold;
    public double brightnessThreshold;
    public int minNodeSize;

    public final SpinnerValueFactory.IntegerSpinnerValueFactory NODE_SIZE_VALUE_FACTORY;
    public final Random RANDOM_GENERATOR = new Random();

    public final int MAX_CUSTOM_COLORS;
    public final double HOVER_SCALE_FACTOR;
    public final Duration HOVER_DURATION_MILLISECONDS;

    public final Color DEFAULT_STATUS_COLOR = Color.BLACK;
    public final int DEFAULT_MIN_NODE_SIZE = 55;
    public final double DEFAULT_SATURATION_THRESHOLD = 0.3;
    public final double DEFAULT_BRIGHTNESS_THRESHOLD = 0.2;

    public final Color SELECTED_NODE_COLOR = Color.RED;
    public final Color NODE_BOUNDARY_COLOR = Color.BLUE;
    public final Color NODE_NUMBER_TEXT_COLOR = Color.BLACK;
    public final List<Color> AUTUMN_COLOR_PALETTE;

    public final double HUE_TOLERANCE;

    public final String ALERT_NO_IMAGE_LOADED;
    public final String ALERT_NO_COLORS_SELECTED;
    public final String ALERT_NO_NODE_SELECTED;
    public final String CONFIRMATION_QUIT_APPLICATION;

    public IdentificationSystemController() {
        // the minimum size for a leaf node to be considered valid
        minNodeSize = DEFAULT_MIN_NODE_SIZE;
        // the maximum number of custom colors that can be selected by the user
        MAX_CUSTOM_COLORS = 120;

        // the scale factor for buttons when hovered over
        HOVER_SCALE_FACTOR = 1.03;
        // the duration of the button hover effect in milliseconds
        HOVER_DURATION_MILLISECONDS = Duration.millis(200);

        // autumn color palette for automatic color selection
        AUTUMN_COLOR_PALETTE = List.of(
                Color.hsb(20, 0.8, 0.7),  // brown
                Color.hsb(30, 0.9, 0.8),  // dark orange
                Color.hsb(40, 0.9, 0.9)  // orange
        );

        // the tolerance in degrees for color matching
        HUE_TOLERANCE = 5;

        // the minimum saturation and brightness threshold for a color to be considered valid
        saturationThreshold = DEFAULT_SATURATION_THRESHOLD;
        brightnessThreshold = DEFAULT_BRIGHTNESS_THRESHOLD;

        ALERT_NO_IMAGE_LOADED = "Click to open an Image!";
        ALERT_NO_COLORS_SELECTED = "No Colors Selected!";
        ALERT_NO_NODE_SELECTED = "No Leaf Selected!";
        CONFIRMATION_QUIT_APPLICATION = "Are you sure you want to Quit? Unsaved changes will be lost!";

        nodeTooltip = new Tooltip();
        nodeTooltip.setAutoHide(true);

        nodeContextMenu = new ContextMenu();
        addToColorPickerMenuItem = new MenuItem("Add to Color Picker");
        nodeContextMenu.getItems().add(addToColorPickerMenuItem);

        NODE_SIZE_VALUE_FACTORY = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, minNodeSize);
    }

    // -------------------------------------------------------------------------
    //  FXML Methods
    // -------------------------------------------------------------------------
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

        // set the image dimensions based on the image center pane container
        Pane imageContainer = (Pane) borderPane.getCenter();
        containerWidth = (int) imageContainer.getWidth();
        containerHeight = (int) imageContainer.getHeight();

        // resize the original image to fit the center pane container
        Image originalColorImage = new Image(imageURL, containerWidth, containerHeight, false, false);

        // initialize the image view with the display image
        displayedImage = new WritableImage(originalColorImage.getPixelReader(), containerWidth, containerHeight);
        imageView.setImage(displayedImage);

        // initialize the node controller with the container width and height
        initNodeController(containerWidth, containerHeight);

        // enable the user controls after the image is loaded
        setUserControlsActive(true);
    }

    @FXML
    private void showBlackAndWhite() {
        if (!isImageLoaded()) {
            showAlert(ALERT_NO_IMAGE_LOADED, AlertType.ERROR);
            return;
        }

        if (!hasColorsSelected() || !isValidColorSelection()) {
            showAlert(ALERT_NO_COLORS_SELECTED, AlertType.ERROR);
            return;
        }

        Pane pane = new Pane(new ImageView(displayBlackAndWhiteImage));
        pane.setPrefSize(containerWidth, containerHeight);

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

        if (!hasColorsSelected() || !isValidColorSelection()) {
            showAlert(ALERT_NO_COLORS_SELECTED, AlertType.ERROR);
            return;
        }

        if (selectedNode != null && selectedNodeBoundary != null) {
            clearNodeSelection();
        }

        if (displayBlackAndWhiteImage == null) return;

        WritableImage randomColoredImage = new WritableImage(displayBlackAndWhiteImage.getPixelReader(), containerWidth, containerHeight);
        List<PixelNode> nodes = nodeController.getNodes();

        for (PixelNode node : nodes) {
            Color randomColor = ColorUtils.getRandomColor(RANDOM_GENERATOR);
            setNodePixelColor(randomColoredImage, node, randomColor);
        }

        Pane pane = new Pane(new ImageView(randomColoredImage));
        pane.setPrefSize(containerWidth, containerHeight);

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

        if (!hasColorsSelected() || !isValidColorSelection()) {
            showAlert(ALERT_NO_COLORS_SELECTED, AlertType.ERROR);
            return;
        }

        // clear the current selection from the displayed image
        if (selectedNode != null && selectedNodeBoundary != null) {
            clearNodeSelection();
        }

        Pane pane = new Pane(new ImageView(displayedImage));
        pane.setPrefSize(containerWidth, containerHeight);

        // display the bound popup window
        Stage stage = FXUtils.showPopupWindow("Bounds | Press 'N' for numbering", pane, pane.getPrefWidth(), pane.getPrefHeight(), false);
        stage.getScene().getStylesheets().add(Objects.requireNonNull(App.getStylesheet("style")));
        stage.getIcons().add(App.getIconImage());

        // draw node boundary rectangles
        NodeBoundsController controller = new NodeBoundsController(nodeController.getNodes(), containerWidth, containerHeight);
        controller.drawNodeBounds(pane, stage.getScene(), NODE_NUMBER_TEXT_COLOR, NODE_BOUNDARY_COLOR);
    }

    @FXML
    private void showAnimatedConnectingPath() {
        if (!isImageLoaded()) {
            showAlert(ALERT_NO_IMAGE_LOADED, AlertType.ERROR);
            return;
        }

        if (!hasColorsSelected() || !isValidColorSelection()) {
            showAlert(ALERT_NO_COLORS_SELECTED, AlertType.ERROR);
            return;
        }

        if (selectedNode == null) {
            showAlert(ALERT_NO_NODE_SELECTED, AlertType.ERROR);
            return;
        }

        // set the duration of the animation in seconds
        TextInputDialog durationDialog = new TextInputDialog("5");
        durationDialog.getDialogPane().getStylesheets().add(Objects.requireNonNull(App.getStylesheet("style")));
        durationDialog.getEditor().getStylesheets().add(Objects.requireNonNull(App.getStylesheet("style")));

        durationDialog.setTitle("Animation Duration");
        durationDialog.setHeaderText("Set the duration of the animation in seconds.");
        durationDialog.setContentText("Enter Duration in seconds: ");

        double duration = 0;

        while (duration <= 0) {
            Optional<String> result = durationDialog.showAndWait();
            // if the user cancels the dialog break the loop
            if (durationDialog.getResult() == null) break;

            // if the user enters an empty string, continue to the next iteration
            if (result.isEmpty()) continue;

            try {
                duration = Double.parseDouble(result.get());
            }
            catch (NumberFormatException e) {
                setStatusBar(e.getMessage(), true);
                continue;
            }

            if (duration <= 0) {
                setStatusBar("Duration must be greater than 0!", true);
                setStatusBarColor(Color.RED, true);
            }
        }

        Duration animationDuration = Duration.seconds(duration);

        if (animationDuration.toSeconds() > 0) {
            setStatusBar("Selected Animation Duration: " + duration + " seconds", true);
        }
        else {
            setStatusBar("Animation cancelled!", true);
        }

        // if the user cancels the dialog or enters an invalid duration
        if (durationDialog.getResult() == null) return;

        // display the popup window
        Pane pane = new Pane(new ImageView(displayedImage));
        pane.setPrefSize(containerWidth, containerHeight);

        Stage stage = FXUtils.showPopupWindow("TSP Path | Press 'N' for numbering", pane, pane.getPrefWidth(), pane.getPrefHeight(), false);
        stage.getScene().getStylesheets().add(Objects.requireNonNull(App.getStylesheet("style")));
        stage.getIcons().add(App.getIconImage());

        // draw node boundary rectangles
        NodeBoundsController controller = new NodeBoundsController(nodeController.getNodes(), containerWidth, containerHeight);
        controller.drawNodeBounds(pane, stage.getScene(), NODE_NUMBER_TEXT_COLOR, NODE_BOUNDARY_COLOR);

        // draw the animated connecting path
        List<Point2D> centerPoints = new MyArrayList<>();
        List<Rectangle> rectangles = new MyArrayList<>();

        for (PixelNode node : nodeController.getNodes()) {
            centerPoints.add(node.getCenter());
        }

        for (Node node : pane.getChildren()) {
            if (node instanceof Rectangle) {
                rectangles.add((Rectangle) node);
            }
        }

        AnimatedPathController pathController = new AnimatedPathController(centerPoints);
        pathController.drawAnimatedPath(selectedNode.getCenter(), rectangles, pane, animationDuration, Color.RED, 1);

        // clear the current selection from the displayed image
        if (selectedNode != null && selectedNodeBoundary != null) {
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

        // only clear the image if the user selects ok
        if (!showConfirmationAlert("Are you sure you want to clear the Image?").equals(ButtonType.OK))
            return;

        // clear cluster data
        if (nodeController.hasNodes()) {
            nodeController.clearNodes();
        }

        // clear node bounds
        if (nodeBoundsController != null && nodeBoundsController.HasBounds()) {
            nodeBoundsController.clearBounds();
        }

        // clear image
        imageFile = null;
        imageView.setImage(null);

        // clear image data
        displayedImage = null;
        displayBlackAndWhiteImage = null;
        unselectedBlackAndWhiteImage = null;
        containerWidth = 0;
        containerHeight = 0;

        // clear node selection
        clickedNode = null;
        selectedNode = null;
        selectedNodeBoundary = null;

        setStatusBar(ALERT_NO_IMAGE_LOADED, true);
        setUserControlsActive(false);

        // clear the color selection
        clearColorSelection();
    }

    @FXML
    private void quitApplication() {
        // quit the application if the user selects ok
        if (!showConfirmationAlert(CONFIRMATION_QUIT_APPLICATION).equals(ButtonType.OK)) return;

        // Close the application
        System.exit(0);
    }

    // -------------------------------------------------------------------------
    // Event Handlers
    // -------------------------------------------------------------------------
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
        if (!isImageLoaded() && !isValidColorSelection()) return;

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
            label.setText(colorName);
        });
    }

    protected void onAddToColorPickerClicked(ActionEvent event) {
        if (clickedPixelColor == null) return;

        addCustomColor(clickedPixelColor);
        setStatusBar("Added Color: " + ColorUtils.getColorName(clickedPixelColor), true);
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

        // track when a color is removed from the custom colors list
        while (newValue.next()) {
            if (newValue.wasRemoved()) {
                onCustomColorRemoved();
            }
        }
    }

    protected void onCustomColorRemoved() {
        if (!isImageLoaded() && !isValidColorSelection()) return;

        if (nodeController.hasNodes()) {
            nodeController.clearNodes();
        }

        createNodesFromImage(displayedImage);
        setStatusBar("Leaf Count: " + nodeController.getNodeCount(), true);
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
        clickedNode = nodeController.getNode(x, y, containerWidth, containerHeight);

        // when the left mouse button is pressed, select the clicked node
        if (event.getButton() == MouseButton.PRIMARY) {
            nodeContextMenu.hide();

            if (!clickedNode.isValid()) {
                nodeTooltip.hide();

                // clear the selection if the clicked node is not valid
                if (selectedNode != null && selectedNodeBoundary != null) {
                    clearNodeSelection();
                }
            }
            else
            {
                // set the clicked node as selected
                if (selectedNode == null && selectedNodeBoundary == null) {
                    selectNode(clickedNode, Color.BLUE);
                }

                showNodeTooltip((Node) event.getSource(), event.getScreenX(), event.getScreenY());
            }
        }

        if (event.getButton() == MouseButton.SECONDARY) {
            if (clickedPixelColor == null) return;

            setStatusBar("Selected Color: " + ColorUtils.getColorName(clickedPixelColor), true);

            // hide the tooltip if it is showing
            // this is necessary so that tooltip will be hidden when the context menu is shown
            nodeTooltip.hide();

            // display the context menu on right click of the mouse
            nodeContextMenu.show((Node) event.getSource(), event.getScreenX(), event.getScreenY());
        }
    }

    protected void onSaturationSliderValueChanged(ObservableValue<?> value, Number oldValue, Number newValue) {
        saturationThreshold = newValue.doubleValue();

        setStatusBar("Saturation Threshold: " + Math.floor(saturationThreshold * 100) / 100 +
                " (" + StringUtils.toPercentageOf(saturationThreshold, 1) + ")", true);
    }

    protected void onBrightnessSliderValueChanged(ObservableValue<?> value, Number oldValue, Number newValue) {
        brightnessThreshold = newValue.doubleValue();
        setStatusBar("Brightness Threshold: " + Math.floor(brightnessThreshold * 100) / 100 +
                " (" + StringUtils.toPercentageOf(brightnessThreshold, 1) + ")", true);
    }

    private void onNodeSizeValueChanged(ObservableValue<?> value, Number oldValue, Number newValue) {
        NODE_SIZE_VALUE_FACTORY.setValue(newValue.intValue());
        minNodeSize = NODE_SIZE_VALUE_FACTORY.getValue();

        setStatusBar("Minimum Node Size: " + minNodeSize, true);
    }

    protected void onBorderPaneMouseClicked(MouseEvent event) {
        if (!isImageLoaded() || (selectedNode == null && selectedNodeBoundary == null)) return;

        // Convert scene coordinates to ImageView's local coordinates
        Point2D localClickedPosition = imageView.sceneToLocal(event.getSceneX(), event.getSceneY());
        boolean isImageViewClicked = imageView.contains(localClickedPosition);

        if (event.getButton() == MouseButton.PRIMARY && !isImageViewClicked) {
            clearNodeSelection();
        }
    }

    protected void onBorderPaneCenterMouseClicked(MouseEvent event) {
        if (!isImageLoaded() && event.getButton() == MouseButton.PRIMARY) {
            loadImageFile();
        }
    }

    protected void onSaveButtonClicked(ActionEvent event) {
        try {
            save();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void onRestoreButtonClicked(ActionEvent event) {
        setDefaultUserControlValues();
    }

    // -------------------------------------------------------------------------
    // Utility Methods
    // -------------------------------------------------------------------------
    public void showNodeTooltip(Node source, double x, double y) {
        int index = nodeController.getNodeIndex(clickedNode) + 1;
        int pixelCount = clickedNode.getPixelCount();

        Label content = new Label("""
                Leaf Number: %d
                Estimated Size (pixel units): %d
                """.formatted(index, pixelCount));

        // make the tooltip invisible to mouse clicks
        content.setMouseTransparent(true);

        nodeTooltip.setGraphic(content);
        nodeTooltip.show(source, x, y);
    }

    public void setNodePixelColor(WritableImage source, PixelNode node, Color color) {
        PixelWriter writer = source.getPixelWriter();

        for (Integer index : node.pixelIndexes()) {
            int x = index % containerWidth;
            int y = index / containerWidth;

            writer.setColor(x, y, color);
        }
    }

    public void selectNode(PixelNode node, Color color) {
        unselectedBlackAndWhiteImage = new WritableImage(displayBlackAndWhiteImage.getPixelReader(), containerWidth, containerHeight);

        setNodePixelColor(displayBlackAndWhiteImage, node, color);

        nodeBoundsController = new NodeBoundsController(nodeController.getNodes(), containerWidth, containerHeight);

        selectedNodeBoundary = nodeBoundsController.addNodeBoundary(node, (Pane)borderPane.getCenter(), borderPane.getScene(), Color.TRANSPARENT, NODE_BOUNDARY_COLOR);
        selectedNode = node;
    }

    public void clearNodeSelection() {
        if (unselectedBlackAndWhiteImage == null) return;

        PixelReader reader = unselectedBlackAndWhiteImage.getPixelReader();
        PixelWriter writer = displayBlackAndWhiteImage.getPixelWriter();

        // restore original image pixels
        for (int x = 0; x < containerWidth; x++) {
            for (int y = 0; y < containerHeight; y++) {
                writer.setColor(x, y, reader.getColor(x, y));
            }
        }

        if (nodeBoundsController != null && nodeBoundsController.HasBounds()) {
            int x = (int) selectedNodeBoundary.getMin().getX();
            int y = (int) selectedNodeBoundary.getMin().getY();

            nodeBoundsController.removeNodeBoundary((Pane)borderPane.getCenter(), selectedNode.getRoot(), x, y);
        }

        selectedNode = null;
        selectedNodeBoundary = null;
        unselectedBlackAndWhiteImage = null;
    }

    public void autoSelectColors() {
        // clear the current selection
        clearColorSelection();

        if (selectedNode != null && selectedNodeBoundary != null) {
            clearNodeSelection();
        }

        PixelReader reader = displayedImage.getPixelReader();

        for (int x = 0; x < containerWidth; x++) {
            for (int y = 0; y < containerHeight; y++) {
                Color pixelColor = reader.getColor(x, y);

                // add the color to the color picker it's in the brown/orange range
                if (isAutumnColor(pixelColor)) {
                    addCustomColor(pixelColor);
                }
            }
        }
    }

    public void createNodesFromImage(WritableImage source) {
        // black and white image based on user-selected colors
        displayBlackAndWhiteImage = ImageUtils.getBlackAndWhite(source, getSelectedColors(), HUE_TOLERANCE, saturationThreshold, brightnessThreshold);

        PixelReader reader = displayBlackAndWhiteImage.getPixelReader();

        // create clusters from the black and white image
        if (!nodeController.hasNodes() && hasColorsSelected()) {
            nodeController.createNodes(containerWidth, containerHeight, minNodeSize, reader, Color.BLACK);
        }

        // create a new black and white image with the processed pixels
        PixelWriter writer = displayBlackAndWhiteImage.getPixelWriter();

        // fill the image with black pixels
        for (int y = 0; y < containerHeight; y++) {
            for (int x = 0; x < containerWidth; x++) {
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

    public List<Color> getSelectedColors() {
        return hasCustomColorsSelected() ? colorPicker.getCustomColors() : Collections.singletonList(colorPicker.getValue());
    }

    public boolean hasColorsSelected() {
        return !colorPicker.getValue().equals(Color.WHITE) || hasCustomColorsSelected();
    }

    public boolean hasCustomColorsSelected() {
        return !colorPicker.getCustomColors().isEmpty();
    }

    public boolean isValidColorSelection() {
        if (!nodeController.hasNodes()) return false;

        return !colorPicker.getValue().equals(Color.WHITE) || hasCustomColorsSelected();
    }

    public boolean isAutumnColor(Color color) {
        return ImageUtils.isValidColor(color, AUTUMN_COLOR_PALETTE, HUE_TOLERANCE, saturationThreshold, brightnessThreshold);
    }

    public void setStatusBar(String message, boolean visible) {
        if (statusLabel == null) return;

        if (message.isBlank()) {
            throw new IllegalArgumentException("Message cannot be blank!");
        }

        statusLabel.setText(message);
        statusLabel.setVisible(visible);

        // set the default color of the status bar label
        setStatusBarColor(DEFAULT_STATUS_COLOR, visible);
    }

    public void setStatusBarColor(Paint color, boolean visible) {
        if (statusLabel == null) return;

        statusLabel.setTextFill(color);
        statusLabel.setVisible(visible);
    }

    public void setUserControlsActive(boolean value) {
        autoSelectColorsButton.setDisable(!value);
        clearColorSelectionButton.setDisable(!value);
        colorPicker.setDisable(!value);
        saturationSlider.setDisable(!value);
        brightnessSlider.setDisable(!value);
        nodeSizeSpinner.setDisable(!value);
        restoreButton.setDisable(!value);
        saveButton.setDisable(!value);
    }

    public void setDefaultUserControlValues() {
        saturationThreshold = DEFAULT_SATURATION_THRESHOLD;
        brightnessThreshold = DEFAULT_BRIGHTNESS_THRESHOLD;

        minNodeSize = DEFAULT_MIN_NODE_SIZE;
        NODE_SIZE_VALUE_FACTORY.setValue(minNodeSize);

        saturationSlider.setValue(saturationThreshold);
        brightnessSlider.setValue(brightnessThreshold);
        nodeSizeSpinner.setValueFactory(NODE_SIZE_VALUE_FACTORY);
    }

    public void save() throws Exception {
        var xstream = new XStream(new PureJavaReflectionProvider());
        File file = App.getSaveFile();

        // serialize to an xml file
        ObjectOutputStream os = xstream.createObjectOutputStream(new FileWriter(file));

        // save the values to the xml file
        os.writeDouble(saturationSlider.getValue());
        os.writeDouble(brightnessSlider.getValue());
        os.writeInt(nodeSizeSpinner.getValue());

        os.close();

        if (file.exists() && file.length() > 0) {
            System.out.println("Saved File: " + "'"+ file.getName() + "'" + " and Size: " + file.length() + " bytes. ");
        }
        else {
            System.err.println("Failed to Save File: " + "'"+ file.getName() + "'" + " and Size: " + file.length() + " bytes. ");
        }
    }

    public void load() throws Exception {
        var xstream = new XStream(new PureJavaReflectionProvider());

        // read the xml file
        ObjectInputStream in = xstream.createObjectInputStream(new FileReader(App.getSaveFile()));

        // load the values from the xml file
        saturationSlider.setValue(in.readDouble());
        brightnessSlider.setValue(in.readDouble());
        NODE_SIZE_VALUE_FACTORY.setValue(in.readInt());

        in.close();
    }

    public boolean isImageLoaded() {
        return imageFile != null;
    }

    // -------------------------------------------------------------------------
    // Initialization Methods
    // -------------------------------------------------------------------------
    public void initNodeController(int width, int height) {
        nodeController = new NodeController(width * height);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colorPicker.getCustomColors().addListener(this::onCustomColorsListChanged);
        colorPicker.setOnHidden(this::onColorPickerClosed);

        imageView.imageProperty().addListener(this::onImageChanged);
        imageView.setOnMouseClicked(this::onImageViewMouseClicked);

        borderPane.setOnMouseClicked(this::onBorderPaneMouseClicked);
        borderPane.getCenter().setOnMouseClicked(this::onBorderPaneCenterMouseClicked);

        autoSelectColorsButton.setOnAction(this::onAutoSelectButtonClicked);
        addToColorPickerMenuItem.setOnAction(this::onAddToColorPickerClicked);

        restoreButton.setOnAction(this::onRestoreButtonClicked);
        saveButton.setOnAction(this::onSaveButtonClicked);

        saturationSlider.valueProperty().addListener(this::onSaturationSliderValueChanged);
        brightnessSlider.valueProperty().addListener(this::onBrightnessSliderValueChanged);
        nodeSizeSpinner.valueProperty().addListener(this::onNodeSizeValueChanged);

        // set the initial values of the sliders and spinner
        setDefaultUserControlValues();

        // add hover scale animation to the buttons
        FXUtils.addHoverScaleAnimation(autoSelectColorsButton, HOVER_SCALE_FACTOR, HOVER_DURATION_MILLISECONDS);
        FXUtils.addHoverScaleAnimation(clearColorSelectionButton, HOVER_SCALE_FACTOR, HOVER_DURATION_MILLISECONDS);
        FXUtils.addHoverScaleAnimation(colorPicker, HOVER_SCALE_FACTOR, HOVER_DURATION_MILLISECONDS);

        // disable the user controls until an image is loaded
        setUserControlsActive(false);

        // show the default status message
        setStatusBar(ALERT_NO_IMAGE_LOADED, true);

        // load user settings
        try {
            load();
        }
        catch (Exception e) {
            System.err.println(Arrays.toString(e.getStackTrace()));
        }
    }
}
