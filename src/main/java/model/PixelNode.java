package model;

import javafx.geometry.Point2D;

public record PixelNode(int root, ArrayList<Integer> indexes, int minSize, int imageWidth) {

    public static final PixelNode EMPTY = new PixelNode(-1, 0, 0);

    // constructor for creating a new node
    public PixelNode(int root, int minSize, int imageWidth) {
        this(root, new ArrayList<>(), minSize, imageWidth);
    }

    public void addIndex(int value) {
        indexes.add(value);
    }

    public int getRoot() {
        return root;
    }

    public int getPixelCount() {
        return indexes.size();
    }

    public Point2D getCenter() {
        double sumX = 0;
        double sumY = 0;

        for (int index : indexes) {
            int x = index % imageWidth;
            int y = index / imageWidth;
            sumX += x;
            sumY += y;
        }

        int count = indexes.size();
        return new Point2D(sumX / count, sumY / count);
    }

    public boolean isValid() {
        return root >= 0 && !isEmpty() && indexes.size() >= minSize;
    }

    public boolean isEmpty() {
        return indexes.isEmpty();
    }

    public void clear() {
        indexes.clear();
    }
}
