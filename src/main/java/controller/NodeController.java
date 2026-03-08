package controller;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

import model.PixelNode;
import model.UnionFind;

/**
 * The node controller uses the union-find algorithm to group nodes of pixels.
 */
public class NodeController {
    // node pixel counts
    private final int[] pixelCounts;

    // min number of pixels in a node
    private int minPixelCount;

    // union-find data structure
    private final UnionFind uf;

    public NodeController(int width, int height, int minNodeSize) {
        int size = width * height;

        setMinNodeSize(minNodeSize);

        pixelCounts = new int[size];
        uf = new UnionFind(size);
    }

    public void unionNeighboringPixels(int width, int height, PixelReader reader, Color excludeColor) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (reader.getColor(x, y).equals(excludeColor)) {
                    continue; // ignore excluded pixels
                }

                // flatten the tree
                int index = y * width + x;

                // unionize right neighbor
                if (x + 1 < width && !reader.getColor(x + 1, y).equals(excludeColor)) {
                    uf.union(index, y * width + (x + 1));
                }

                // unionize down neighbor
                if (y + 1 < height && !reader.getColor(x, y + 1).equals(excludeColor)) {
                    uf.union(index, (y + 1) * width + x);
                }
            }
        }

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (reader.getColor(x, y).equals(excludeColor)) {
                    continue;
                }

                // flatten the tree
                int index = y * width + x;

                // find the parent of each node
                int rootNode = uf.find(index);

                // add the number of pixels in each node
                pixelCounts[rootNode]++;
            }
        }
    }

    public PixelNode getNode(int width, int height, int x, int y) {
        if (!isValidPixel(width, height, x, y)) return null;

        int index = y * width + x;
        int rootNode = uf.find(index);
        int[] parent = uf.getParent();

        PixelNode node = new PixelNode(rootNode, pixelCounts[rootNode]);

        for (int i = 0; i < parent.length; i++) {
            if (uf.find(i) == rootNode) {
                node.addPixel(i);
            }
        }

        return node;
    }

    public int getNodePixelCount(int width, int height, int x, int y) {
        if (!isValidPixel(width, height, x, y)) return -1;

        int index = y * width + x;
        int rootNode = uf.find(index);

        if (!isValidNode(rootNode)) return -1;

        return pixelCounts[rootNode];
    }

    public int getNodePixelCount(Image source, Point2D coordinates) {
        int width = (int) source.getWidth();
        int height = (int) source.getHeight();
        int x = (int) coordinates.getX();
        int y = (int) coordinates.getY();

        return getNodePixelCount(width, height, x, y);
    }

    public int getNodeSequenceNumber(int width, int height, int x, int y) {
        if (!isValidPixel(width, height, x, y)) return -1;

        int index = y * width + x;
        int rootNode = uf.find(index);

        int sequenceNumber = 0;
        for (int i = 0; i < pixelCounts.length; i++) {
            if (!isValidNode(i)) continue;

            sequenceNumber++;
            if (i == rootNode) return sequenceNumber;
        }

        return -1;
    }

    public int getNodeSequenceNumber(Image source, Point2D coordinates) {
        int width = (int) source.getWidth();
        int height = (int) source.getHeight();
        int x = (int) coordinates.getX();
        int y = (int) coordinates.getY();

        return getNodeSequenceNumber(width, height, x, y);
    }

    public int getNodeCount() {
        int count = 0;

        for (int i = 0; i < pixelCounts.length; i++) {
            if (!isValidNode(i)) continue;

            count++;
        }

        return count;
    }

    public int getNodeRoot(int width, int height, int x, int y) {
        if (!isValidPixel(width, height, x, y)) return -1;

        int index = y * width + x;

        return uf.find(index);
    }

    public int getNodeRoot(int index) {
        return uf.find(index);
    }

    public void setMinNodeSize(int pixelCount) {
        if (pixelCount > 0) {
            minPixelCount = pixelCount;
        }
    }

    public boolean isValidNode(int root) {
        return pixelCounts[root] >= minPixelCount; // setting a min pixel count reduces noise
    }

    public boolean isValidPixel(int width, int height, int x, int y) {
        return (x >= 0 && y >= 0 && x < width && y < height);
    }
}
