package me.johannes.autumn.controller;

import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;

import me.johannes.autumn.model.Bounds;
import me.johannes.autumn.model.PixelNode;
import me.johannes.autumn.model.ArrayList;

import me.johannes.autumn.util.ArrayUtils;

import java.util.List;

public class NodeBoundsController {
    private final ArrayList<Bounds> bounds;
    private final Point2D defaultMin;
    private final Point2D defaultMax;

    private Pane targetPane;
    private Scene targetScene;

    private final List<Text> boundsNumbers;

    public NodeBoundsController(Pane target, List<PixelNode> nodes, double width, double height) {
        targetPane = target;
        boundsNumbers = new ArrayList<>(nodes.size());

        defaultMin = new Point2D(width, height);
        defaultMax = new Point2D(-1, -1);

        bounds = new ArrayList<>(nodes.size());
        for (int i = 0; i < nodes.size(); i++) {
            bounds.add(new Bounds(defaultMin, defaultMax));
        }

        for (int i = 0; i < nodes.size(); i++) {
            List<Integer> indexes = nodes.get(i).pixelIndexes();

            for (Integer index : indexes) {
                int x = index % (int) width;
                int y = index / (int) width;

                setNodeBounds(i, x, y);
            }
        }
    }

    public void drawNodeBounds(Color textColor, Color boundryColor, int sizeOffset, double cornerRadius, Scene target) {
        if (targetScene == null) {
            setTargetScene(target);
        }

        // sort in ascending order
        ArrayUtils.sort(bounds, Bounds::compareTo);

        int boundsCount = 0;
        for (int i = bounds.size() - 1; i >= 0; i--) {
            Bounds boundaryBox = bounds.get(i);

            if (boundaryBox.getMin().equals(defaultMin) || boundaryBox.getMax().equals(defaultMax)) continue;
            if (boundaryBox.getMin().getX() == boundaryBox.getMax().getX() ||
                    boundaryBox.getMin().getY() == boundaryBox.getMax().getY())
                continue;

            boundsCount++;

            int x = boundaryBox.getMinX();
            int y = boundaryBox.getMinY();
            int width = boundaryBox.getWidth() + sizeOffset;
            int height = boundaryBox.getHeight() + sizeOffset;

            Rectangle boundaryRect = new Rectangle(x, y, width, height);
            boundaryRect.setFill(Color.TRANSPARENT);
            boundaryRect.setStroke(boundryColor);
            boundaryRect.setArcWidth(cornerRadius);
            boundaryRect.setArcHeight(cornerRadius);

            Text boundsNumber = new Text(String.valueOf(boundsCount));
            boundsNumber.setVisible(false);
            boundsNumber.getStyleClass().add("bounds-number");
            boundsNumber.setMouseTransparent(true);
            boundsNumber.setFill(textColor);
            boundsNumber.setStrokeType(StrokeType.OUTSIDE);
            boundsNumber.setStroke(Color.WHITE);
            boundsNumber.setStrokeWidth(1.2);

            double textWidth = boundsNumber.getLayoutBounds().getWidth();
            double textHeight = boundsNumber.getLayoutBounds().getHeight();

            boundsNumber.setX(x + (width - textWidth) / 2);
            boundsNumber.setY(y + (height + textHeight) / 2);

            boundsNumbers.add(boundsNumber);
            targetPane.getChildren().addAll(boundaryRect, boundsNumber);
        }
    }

    protected void setNodeBounds(int index, int x, int y) {
        Bounds currentBounds = bounds.get(index);

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

    public void setNodeBounds(int index, Bounds bound) {
        this.bounds.set(index, bound);
    }

    public Bounds getNodeBounds(int index) {
        return bounds.get(index);
    }

    public void setTargetPane(Pane target) {
        targetPane = target;
    }

    public Pane getTargetPane() {
        return targetPane;
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

    // Events
    protected void onTargetSceneChanged(Scene scene) {
        scene.setOnKeyPressed(this::onKeyPressed);
    }

    protected void onKeyPressed(KeyEvent event) {
        if (event.getCode() != KeyCode.N) return;

        for (Text number : boundsNumbers) {
            number.setVisible(!number.isVisible());
        }
    }
}
