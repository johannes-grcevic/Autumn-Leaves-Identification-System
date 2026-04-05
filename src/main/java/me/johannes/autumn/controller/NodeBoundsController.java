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
import me.johannes.autumn.model.MyArrayList;
import me.johannes.autumn.util.ArrayUtils;

import java.util.List;

public class NodeBoundsController {
    private final MyHashtable<Integer, Bounds> nodeBounds;
    private final List<PixelNode> pixelNodes;
    private final List<Text> boundsNumbers;

    public NodeBoundsController(List<PixelNode> nodes, double width, double height) {
        // sort nodes in ascending order by pixel count
        ArrayUtils.sort(nodes, PixelNode::compareTo);

        // reverse the sort order of nodes so that the largest node is the first one
        pixelNodes = nodes.reversed();

        nodeBounds = new MyHashtable<>(nodes.size());
        boundsNumbers = new MyArrayList<>(nodes.size());

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

    public void drawNodeBounds(Pane target, Scene scene, Color textColor, Color boundryColor, int sizeOffset, double cornerRadius) {
        if (target == null || scene == null) return;
        onTargetSceneChanged(scene);

        int count = 0;

        for (PixelNode pixelNode : pixelNodes) {
            Bounds boundaryBox = nodeBounds.get(pixelNode.getRoot());

            count++;

            int x = boundaryBox.getMinX();
            int y = boundaryBox.getMinY();
            int width = boundaryBox.getWidth() + sizeOffset;
            int height = boundaryBox.getHeight() + sizeOffset;

            Rectangle boundaryRect = new Rectangle(x, y, width, height);
            boundaryRect.setFill(Color.TRANSPARENT);
            boundaryRect.setStroke(boundryColor);
            boundaryRect.setArcWidth(cornerRadius);
            boundaryRect.setArcHeight(cornerRadius);

            Text boundsNumber = new Text(String.valueOf(count));
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
            target.getChildren().addAll(boundaryRect, boundsNumber);
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

    public void setNodeBounds(int rootNode, Bounds bound) {
        nodeBounds.get(rootNode).setMinMax(bound.getMin(), bound.getMax());
    }

    public Bounds getNodeBounds(int rootNode) {
        return nodeBounds.get(rootNode);
    }

    public boolean HasBounds() {
        return !nodeBounds.isEmpty();
    }

    public void clearBounds() {
        nodeBounds.clear();
        pixelNodes.clear();
        boundsNumbers.clear();
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
