package controller;

import javafx.geometry.Point2D;
import javafx.geometry.Pos;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import model.Bounds;
import model.PixelNode;

import util.ArrayUtils;
import java.util.Arrays;

public class NodeBoundsController {
    private final Bounds[] bounds;
    private final Point2D defaultMin;
    private final Point2D defaultMax;

    private Pane drawPane;
    private Scene targetScene;

    public NodeBoundsController(double width, double height, NodeController controller) {
        int size = (int) (width * height);

        defaultMin = new Point2D(width, height);
        defaultMax = new Point2D(-1, -1);

        bounds = new Bounds[size];
        Arrays.setAll(bounds, _ -> new Bounds(defaultMin, defaultMax));

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {

                // get the root of each node
                PixelNode node = controller.getNode(x, y, (int) width, (int) height);
                if (!node.isValid()) continue;

                int rootNode = node.getRoot();

                setNodeBounds(rootNode, x, y);
            }
        }
    }

    public Pane drawNodeBounds(Image source, Color textColor, Color boundryColor, double cornerRadius, Scene target) {
        if (targetScene == null) {
            setTargetScene(target);
        }

        drawPane = new Pane(new ImageView(source));
        drawPane.setPrefSize(source.getWidth(), source.getHeight());

        // sort in ascending order
        ArrayUtils.sort(bounds, Bounds::compareTo);

        int boundsCount = 0;
        for (Bounds boundaryBox : bounds) {
            if (boundaryBox.getMin().equals(defaultMin) || boundaryBox.getMax().equals(defaultMax)) continue;
            if (boundaryBox.getMin().getX() == boundaryBox.getMax().getX() ||
                    boundaryBox.getMin().getY() == boundaryBox.getMax().getY())
                continue;

            boundsCount++;

            int x = boundaryBox.getMinX();
            int y = boundaryBox.getMinY();
            int width = boundaryBox.getWidth();
            int height = boundaryBox.getHeight();

            Rectangle boundaryRect = new Rectangle(x, y, width, height);
            boundaryRect.setFill(Color.TRANSPARENT);
            boundaryRect.setStroke(boundryColor);
            boundaryRect.setArcWidth(cornerRadius);
            boundaryRect.setArcHeight(cornerRadius);

            Label boundsNumber = new Label(String.valueOf(boundsCount));
            boundsNumber.setVisible(false);
            boundsNumber.setFont(Font.font(boundsNumber.getFont().getFamily(), FontWeight.BOLD, boundsNumber.getFont().getSize()));
            boundsNumber.setTextFill(textColor);
            boundsNumber.setAlignment(Pos.CENTER);
            boundsNumber.setPrefSize(width, height);
            boundsNumber.setLayoutX(x);
            boundsNumber.setLayoutY(y);

            drawPane.getChildren().addAll(boundaryRect, boundsNumber);
        }

        return drawPane;
    }

    private void setNodeBounds(int index, int x, int y) {
        Bounds currentBounds = bounds[index];

        // set min x and y bounds
        if (x < currentBounds.getMinX()) {
            currentBounds.setMin(new Point2D(x, currentBounds.getMinY()));
        }
        if (y < currentBounds.getMinY()) {
            currentBounds.setMin(new Point2D(currentBounds.getMinX(), y));
        }

        // set max x and y bounds
        if (x > currentBounds.getMaxX()) {
            currentBounds.setMax(new Point2D(x, currentBounds.getMaxY()));
        }
        if (y > currentBounds.getMaxY()) {
            currentBounds.setMax(new Point2D(currentBounds.getMaxX(), y));
        }
    }

    public void setTargetScene(Scene target) {
        targetScene = target;

        // trigger scene changed event
        if (target != null) {
            onTargetSceneChanged(target);
        }
    }

    public Scene getTargetScene() {
        return targetScene;
    }

    public Bounds getNodeBounds(int index) {
        return bounds[index];
    }

    // Events
    protected void onTargetSceneChanged(Scene scene) {
        scene.setOnKeyPressed(this::onKeyPressed);
    }

    protected void onKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.N) {
            for (Node node : drawPane.getChildren()) {
                if (node instanceof Label) {
                    node.setVisible(!node.isVisible());
                }
            }
        }
    }
}
