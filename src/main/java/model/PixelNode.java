package model;

import java.util.ArrayList;
import java.util.List;

public record PixelNode(int root, List<Integer> pixels, int minSize, int capacity) {
    public PixelNode(int root, int minSize, int capacity) {
        this(root, new ArrayList<>(capacity), minSize, capacity);
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

    public static PixelNode getEmpty() {
        return new PixelNode(-1, 0, 0);
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
