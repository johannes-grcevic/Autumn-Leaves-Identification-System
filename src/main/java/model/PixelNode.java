package model;

import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;

public record PixelNode(int root, List<Integer> pixels, int minSize, int capacity, int imageWidth) {
    public PixelNode(int root, int minSize, int capacity, int imageWidth) {
        this(root, new ArrayList<>(capacity), minSize, capacity, imageWidth);
    }

    public void addPixel(int index) {
        pixels.add(index);
    }

    public int getRoot() {
        return root;
    }

    public int getMinSize() {
        return minSize;
    }

    public int getPixelCount() {
        return pixels.size();
    }

    public Point2D getCenter() {
        double sumX = 0;
        double sumY = 0;

        for (int pixel : pixels) {
            int x = pixel % imageWidth;
            int y = pixel / imageWidth;
            sumX += x;
            sumY += y;
        }

        int count = pixels.size();
        return new Point2D(sumX / count, sumY / count);
    }

    public static PixelNode getEmpty() {
        return new PixelNode(-1, 0, 0, 0);
    }

    public boolean isValid() {
        return root >= 0 && !pixels.isEmpty() && pixels.size() >= minSize;
    }

    public boolean isEmpty() {
        return pixels.isEmpty();
    }

    public void clear() {
        pixels.clear();
    }
}
