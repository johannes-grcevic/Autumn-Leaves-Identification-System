package model;

import java.util.ArrayList;
import java.util.List;

public record PixelNode(int root, List<Integer> pixels) {
    public PixelNode(int root, int size) {
        this(root, new ArrayList<>(size));
    }

    public void addPixel(int pixel) {
        pixels.add(pixel);
    }

    public void addPixels(int[] pixels) {
        for (int pixel : pixels) {
            addPixel(pixel);
        }
    }

    public void clear() {
        pixels.clear();
    }

    public int getCount() {
        return pixels.size();
    }
}
