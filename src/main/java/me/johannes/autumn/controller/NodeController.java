package me.johannes.autumn.controller;

import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

import me.johannes.autumn.model.MyArrayList;
import me.johannes.autumn.model.MyHashtable;
import me.johannes.autumn.model.PixelNode;
import me.johannes.autumn.model.UnionFind;
import me.johannes.autumn.util.ArrayUtils;

import java.util.Comparator;
import java.util.List;

/**
 * The node controller uses the union-find algorithm to group nodes of pixels.
 */
public class NodeController {
    // hashtable to store pixel nodes
    private final MyHashtable<Integer, PixelNode> nodes;

    // union-find data structure
    private final UnionFind uf;

    public NodeController(int size) {
        nodes = new MyHashtable<>();
        uf = new UnionFind(Math.max(size, 0));
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

                // initialize each node
                if (!nodes.containsKey(rootNode)) {
                    nodes.put(rootNode, new PixelNode(rootNode, minSize, width));
                }

                // add pixels to each node
                nodes.get(rootNode).addPixelIndex(index);
            }
        }
    }

    public PixelNode getNode(int x, int y, int width, int height) {
        if (x < 0 || y < 0 || x > width || y > height) return PixelNode.EMPTY;

        int index = y * width + x;
        int rootNode = uf.find(index);

        if (rootNode < 0) return PixelNode.EMPTY;

        PixelNode node = nodes.get(rootNode);

        if (node == null || !node.isValid()) return PixelNode.EMPTY;

        return node;
    }

    public List<PixelNode> getNodes() {
        List<PixelNode> nodes = new MyArrayList<>();

        for (PixelNode node : this.nodes.values()) {
            if (node != null && node.isValid()) {
                nodes.add(node);
            }
        }

        return nodes;
    }

    public int getNodeCount() {
        int count = 0;

        for (PixelNode node : nodes.values()) {
            if (node != null && node.isValid()) count++;
        }

        return count;
    }

    public int getNodeIndex(PixelNode node) {
        if (node == null || !node.isValid()) return 0;

        List<PixelNode> nodes = getNodes();

        // sort nodes by pixel count in descending order
        ArrayUtils.sort(nodes, Comparator.comparingInt(PixelNode::getPixelCount).reversed());

        return nodes.indexOf(node);
    }

    public void clearNodes() {
        // clear pixel data for each node
        for (PixelNode node : nodes.values()) {
            if (node != null) node.clear();
        }

        nodes.clear();
        uf.clear();
    }

    public boolean hasNodes() {
        return getNodeCount() > 0;
    }
}
