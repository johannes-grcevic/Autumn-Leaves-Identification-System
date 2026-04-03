package controller;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

import model.PixelNode;
import model.ArrayList;
import model.UnionFind;

import java.util.Arrays;

/**
 * The node controller uses the union-find algorithm to group nodes of pixels.
 */
public class NodeController {
    // nodes containing pixels
    private final PixelNode[] nodes;
    // number of pixels in each node
    private final int[] pixelCounts;

    // union-find data structure
    private final UnionFind uf;

    public NodeController(int size) {
        if (size <= 0) {
            nodes = new PixelNode[0];
            pixelCounts = new int[0];
            uf = new UnionFind(0);
            return;
        }

        nodes = new PixelNode[size];
        pixelCounts = new int[size];
        uf = new UnionFind(size);
    }

    protected void unionNeighboringPixels(int width, int height, PixelReader reader, Color excludeColor) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (reader.getColor(x, y).equals(excludeColor)) {
                    continue; // ignore excluded pixels (eg. black pixels)
                }

                // convert 2D (x, y) coordinates to 1D array index
                int index = y * width + x;

                // join right neighbor
                if (x + 1 < width && !reader.getColor(x + 1, y).equals(excludeColor)) {
                    uf.union(index, y * width + (x + 1));
                }

                // join down neighbor
                if (y + 1 < height && !reader.getColor(x, y + 1).equals(excludeColor)) {
                    uf.union(index, (y + 1) * width + x);
                }
            }
        }
    }

    public void createNodes(int width, int height, int minSize, PixelReader reader, Color excludeColor) {
        // union neighboring pixels before creating clusters
        unionNeighboringPixels(width, height, reader, excludeColor);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (reader.getColor(x, y).equals(excludeColor)) {
                    continue;
                }

                int index = y * width + x;

                // find the parent of each node
                int rootNode = uf.find(index);

                // count pixels in each node
                pixelCounts[rootNode]++;

                // initialize each node
                if (nodes[rootNode] == null) {
                    nodes[rootNode] = new PixelNode(rootNode, minSize, pixelCounts[rootNode], width);
                }

                // add pixels to each node
                nodes[rootNode].addPixel(index);
            }
        }
    }

    public PixelNode getNode(int x, int y, int width, int height) {
        if ((x < 0 && y < 0) || (x > width && y > height)) return PixelNode.getEmpty();

        int index = y * width + x;
        int rootNode = uf.find(index);

        if (rootNode < 0) return PixelNode.getEmpty();

        PixelNode node = nodes[rootNode];

        if (node == null || !node.isValid()) return PixelNode.getEmpty();

        return node;
    }

    public PixelNode getNode(Image source, Point2D coordinates) {
        int width = (int) source.getWidth();
        int height = (int) source.getHeight();
        int x = (int) coordinates.getX();
        int y = (int) coordinates.getY();

        return getNode(x, y, width, height);
    }

    public PixelNode[] getNodes() {
        ArrayList<PixelNode> validNodes = new ArrayList<>();

        for (PixelNode node : nodes) {
            if (node != null && node.isValid()) {
                validNodes.add(node);
            }
        }

        return validNodes.toArray(new PixelNode[0]);
    }

    public int getNodeCount() {
        int count = 0;
        for (PixelNode node : nodes) {
            if (node != null && node.isValid()) count++;
        }

        return count;
    }

    public int getNodeSequenceNumber(PixelNode node) {
        if (node == null || !node.isValid()) return 0;

        int sequenceNumber = 0;
        for (int i = pixelCounts.length - 1; i >= 0; i--) {
            if (pixelCounts[i] < node.getMinSize()) continue;

            sequenceNumber++;
            if (i == node.getRoot()) {
                return sequenceNumber;
            }
        }

        return 0;
    }

    public void clearNodes() {
        // clear pixel data for each node
        for (PixelNode node : nodes) {
            if (node != null) node.clear();
        }

        Arrays.fill(nodes, null);
        Arrays.fill(pixelCounts, 0);

        uf.clear();
    }
}
