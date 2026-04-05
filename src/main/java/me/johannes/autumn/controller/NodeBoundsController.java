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
import me.johannes.autumn.model.MyHashtable;
import me.johannes.autumn.model.PixelNode;
import me.johannes.autumn.util.ArrayUtils;

import java.util.List;

public class NodeBoundsController {
    private final MyHashtable<Integer, Bounds> nodeBounds;
    private final MyHashtable<Integer, Text> boundsNumbers;
    private final List<PixelNode> pixelNodes;

    private int boundsNumber = 1;

    public NodeBoundsController(List<PixelNode> nodes, double width, double height) {
        // sort nodes in ascending order by pixel count
        ArrayUtils.sort(nodes, PixelNode::compareTo);

        // reverse the sort order of nodes so that the largest node is the first one
        pixelNodes = nodes.reversed();

        nodeBounds = new MyHashtable<>();
        boundsNumbers = new MyHashtable<>();

        Point2D defaultMin = new Point2D(width, height);
        Point2D defaultMax = new Point2D(-1, -1);

        for (PixelNode node : pixelNodes) {
            nodeBounds.put(node.getRoot(), new Bounds(defaultMin, defaultMax));
        }

        for (PixelNode node : pixelNodes) {
            List<Integer> indexes = node.pixelIndexes();

            for (Integer index : indexes) {
                int x = index % (int) width;
                int y = index / (int) width;

                setNodeBounds(node.getRoot(), x, y);
            }
        }
    }

    public void drawNodeBoundary(PixelNode node, Pane target, Scene scene, Color numberColor, Color boundryColor, int offset) {
        if (target == null || scene == null) return;
        onTargetSceneChanged(scene);

        Bounds boundary = nodeBounds.get(node.getRoot());

        int x = boundary.getMinX();
        int y = boundary.getMinY();
        int width = boundary.getWidth() + offset;
        int height = boundary.getHeight() + offset;

        Rectangle boundaryRect = new Rectangle(x, y, width, height);
        boundaryRect.setFill(Color.TRANSPARENT);
        boundaryRect.setStroke(boundryColor);
        boundaryRect.getStyleClass().add("bounds-rectangle");

        Text numberText = new Text(String.valueOf(boundsNumber));
        numberText.setVisible(false);
        numberText.getStyleClass().add("bounds-number");
        numberText.setMouseTransparent(true);
        numberText.setFill(numberColor);
        numberText.setStrokeType(StrokeType.OUTSIDE);
        numberText.setStroke(Color.WHITE);
        numberText.setStrokeWidth(1.2);

        double textWidth = numberText.getLayoutBounds().getWidth();
        double textHeight = numberText.getLayoutBounds().getHeight();

        numberText.setX(x + (width - textWidth) / 2);
        numberText.setY(y + (height + textHeight) / 2);

        boundsNumbers.put(boundsNumber++, numberText);
        target.getChildren().addAll(boundaryRect, numberText);
    }

    public void drawNodeBounds(Pane target, Scene scene, Color numberColor, Color boundryColor, int offset) {
        for (PixelNode node : pixelNodes) {
            drawNodeBoundary(node, target, scene, numberColor, boundryColor, offset);
        }
    }

    protected void setNodeBounds(int rootNode, int x, int y) {
        Bounds current = nodeBounds.get(rootNode);

        // set min x and y bounds
        if (x < current.getMinX()) {
            current.setMin(new Point2D(x, current.getMinY()));
        }
        if (y < current.getMinY()) {
            current.setMin(new Point2D(current.getMinX(), y));
        }

        // set max x and y bounds
        if (x > current.getMaxX()) {
            current.setMax(new Point2D(x, current.getMaxY()));
        }
        if (y > current.getMaxY()) {
            current.setMax(new Point2D(current.getMaxX(), y));
        }
    }

    public void setNodeBoundary(int rootNode, Bounds bound) {
        nodeBounds.get(rootNode).setMinMax(bound.getMin(), bound.getMax());
    }

    public Bounds getNodeBoundary(int rootNode) {
        return nodeBounds.get(rootNode);
    }

    public boolean HasBounds() {
        return !nodeBounds.isEmpty();
    }

    public void clearBounds() {
        nodeBounds.clear();
        pixelNodes.clear();
        boundsNumbers.clear();
        boundsNumber = 1;
    }

    // Events
    protected void onTargetSceneChanged(Scene scene) {
        scene.setOnKeyPressed(this::onKeyPressed);
    }

    protected void onKeyPressed(KeyEvent event) {
        if (event.getCode() != KeyCode.N) return;

        for (Text number : boundsNumbers.values()) {
            number.setVisible(!number.isVisible());
        }
    }
}
