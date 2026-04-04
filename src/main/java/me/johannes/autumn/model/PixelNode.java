package me.johannes.autumn.model;

import javafx.geometry.Point2D;

import java.util.List;

public record PixelNode(int root, List<Integer> pixelIndexes, int minSize, int imageWidth) {

    public static final PixelNode EMPTY = new PixelNode(-1, 0, 0);

    // constructor for creating a new node
    public PixelNode(int root, int minSize, int imageWidth) {
        this(root, new ArrayList<>(), minSize, imageWidth);
    }

    public void addPixelIndex(int value) {
        pixelIndexes.add(value);
    }

    public int getRoot() {
        return root;
    }

    public int getPixelCount() {
        return pixelIndexes.size();
    }

    public Point2D getCenter() {
        double sumX = 0;
        double sumY = 0;

        for (int index : pixelIndexes) {
            int x = index % imageWidth;
            int y = index / imageWidth;
            sumX += x;
            sumY += y;
        }

        int count = pixelIndexes.size();
        return new Point2D(sumX / count, sumY / count);
    }

    public boolean isValid() {
        return root >= 0 && !isEmpty() && pixelIndexes.size() >= minSize;
    }

    public boolean isEmpty() {
        return pixelIndexes.isEmpty();
    }

    public void clear() {
        pixelIndexes.clear();
    }
}
