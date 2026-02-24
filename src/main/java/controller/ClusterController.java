package controller;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

import model.UnionFind;
import util.ArrayUtils;

/**
 * The cluster controller uses the union-find algorithm to find clusters of pixels.
 */
public class ClusterController {
    // cluster bounds
    private final int[] boundsMinX;
    private final int[] boundsMinY;
    private final int[] boundsMaxX;
    private final int[] boundsMaxY;

    // all cluster pixels
    private final int[] clusterPixels;

    // min number of pixels in a cluster
    private int minClusterPixels;

    // union-find data structure
    private final UnionFind uf;

    public ClusterController(int width, int height) {
        int size = width * height;
        setMinClusterSize(50);

        boundsMinX = new int[size];
        boundsMinY = new int[size];
        boundsMaxX = new int[size];
        boundsMaxY = new int[size];

        clusterPixels = new int[size];

        uf = new UnionFind(size);

        ArrayUtils.fill(boundsMinX, width);
        ArrayUtils.fill(boundsMinY, height);

        ArrayUtils.fill(boundsMaxX, -1);
        ArrayUtils.fill(boundsMaxY, -1);
    }

    public void unionNeighboringPixels(int width, int height, PixelReader reader, Color excludeColor) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (reader.getColor(x, y).equals(excludeColor)) {
                    continue; // ignore excluded pixels
                }

                // flatten to 1D
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

        setBounds(width, height, reader, excludeColor);
    }

    private void setBounds(int width, int height, PixelReader reader, Color excludeColor) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (reader.getColor(x, y).equals(excludeColor)) {
                    continue; // ignore excluded pixels
                }

                // flatten to 1D
                int index = y * width + x;

                // find the root of each cluster
                int root = uf.find(index);

                // add pixels to each cluster
                clusterPixels[root]++;

                // set cluster bounds
                if (x < boundsMinX[root]) {
                    boundsMinX[root] = x;
                }
                if (y < boundsMinY[root]) {
                    boundsMinY[root] = y;
                }

                if (x > boundsMaxX[root]) {
                    boundsMaxX[root] = x;
                }
                if (y > boundsMaxY[root]) {
                    boundsMaxY[root] = y;
                }
            }
        }
    }

    public int[] getClusterPixels(int width, int height, int x, int y) {
        if (x < 0 || y < 0 || x > width || y > height) return new int[0];

        int index = y * width + x;
        int root = uf.find(index);

        if (!isValidCluster(root)) return new int[0];

        return new int[clusterPixels[root]];
    }

    public int[] getClusterPixels(Image source, Point2D coordinates) {
        int width = (int) source.getWidth();
        int height = (int) source.getHeight();
        int x = (int) coordinates.getX();
        int y = (int) coordinates.getY();

        return getClusterPixels(width, height, x, y);
    }

    public int getClusterNumber(int width, int height, int x, int y) {
        if (x < 0 || y < 0 || x > width || y > height) return -1;

        int index = y * width + x;
        int root = uf.find(index);

        int clusterNumber = 0;
        for (int i = 0; i < clusterPixels.length; i++) {
            if (clusterPixels[i] < getMinClusterSize()) continue;

            clusterNumber++;

            if (uf.find(i) == root) {
                return clusterNumber;
            }
        }

        return -1;
    }

    public int getClusterNumber(Image source, Point2D coordinates) {
        int width = (int) source.getWidth();
        int height = (int) source.getHeight();
        int x = (int) coordinates.getX();
        int y = (int) coordinates.getY();

        return getClusterNumber(width, height, x, y);
    }

    public int[] getAllClusterPixels() {
        return clusterPixels;
    }

    public int getClusterCount() {
        int count = 0;

        for (int i = 0; i < clusterPixels.length; i++) {
            if (isValidCluster(i)) {
                count++;
            }
        }

        return count;
    }

    public int getMinClusterSize() {
        return minClusterPixels;
    }

    public void setMinClusterSize(int pixelCount) {
        if (pixelCount >= 1) {
            minClusterPixels = pixelCount;
        }
    }

    public int[] getBoundsMinX() {
        return boundsMinX;
    }

    public int[] getBoundsMinY() {
        return boundsMinY;
    }

    public int[] getBoundsMaxX() {
        return boundsMaxX;
    }

    public int[] getBoundsMaxY() {
        return boundsMaxY;
    }

    public boolean isValidCluster(int index) {
        return clusterPixels[index] >= minClusterPixels; // setting a min pixel count helps with noise
    }
}
