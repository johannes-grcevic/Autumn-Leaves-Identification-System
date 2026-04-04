package model;

import javafx.geometry.Point2D;

import java.util.List;

public class NearestNeighbor {
    private final List<Point2D> points;

    public NearestNeighbor(List<Point2D> points) {
        this.points = points;
    }

    // find the shortest path from the start point to all other points
    public List<Point2D> findShortestPath(Point2D start) {
        if (start == null || start.equals(Point2D.ZERO) || points.isEmpty()) return List.of();

        List<Point2D> path = new ArrayList<>(points.size());
        List<Point2D> unvisited = new ArrayList<>(points);

        // set the current point to the start point
        Point2D current = start;

        // add the start point to the path
        path.add(current);

        // remove the start point from the unvisited set
        unvisited.remove(current);

        // find the shortest path from the start point
        while (!unvisited.isEmpty()) {
            Point2D nearestNeighbour = getNearestNeighbour(current, unvisited);

            if (nearestNeighbour.equals(Point2D.ZERO)) continue;

            path.add(nearestNeighbour);
            unvisited.remove(nearestNeighbour);

            // move to the next nearest point
            current = nearestNeighbour;
        }

        return path;
    }

    // get the nearest neighbor to the current point based on the distance
    // we have to traverse the unvisited points each time to find the nearest neighbor
    public Point2D getNearestNeighbour(Point2D current, List<Point2D> unvisited) {
        if (unvisited.isEmpty() || current.equals(Point2D.ZERO)) return Point2D.ZERO;

        Point2D nearestNeighbour = Point2D.ZERO;
        double nearestDistance = Double.MAX_VALUE;

        // find the nearest neighbor in the set of unvisited points
        for (Point2D point : unvisited) {
            // calculate the distance between the current point and the neighbor
            double distance = distance(current, point);

            // update the nearest point if the distance is smaller than the current nearest neighbor
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearestNeighbour = point;
            }
        }

        return nearestNeighbour;
    }

    // distance between two points
    public double distance(Point2D a, Point2D b) {
        return a.distance(b);
    }
}