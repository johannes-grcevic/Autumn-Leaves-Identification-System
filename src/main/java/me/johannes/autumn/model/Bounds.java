package me.johannes.autumn.model;

import javafx.geometry.Point2D;

public class Bounds implements Comparable<Bounds> {
    private Point2D min;
    private Point2D max;

    public Bounds(Point2D min, Point2D max) {
        this.min = min;
        this.max = max;
    }

    public Point2D getMin() {
        return min;
    }

    public Point2D getMax() {
        return max;
    }

    public void setMin(Point2D min) {
        this.min = min;
    }

    public void setMax(Point2D max) {
        this.max = max;
    }

    public void setMinMax(Point2D min, Point2D max) {
        this.min = min;
        this.max = max;
    }

    public int getMinX() {
        return (int) min.getX();
    }

    public int getMinY() {
        return (int) min.getY();
    }

    public int getMaxX() {
        return (int) max.getX();
    }

    public int getMaxY() {
        return (int) max.getY();
    }

    public int getWidth() {
        return (int) (max.getX() - min.getX());
    }

    public int getHeight() {
        return (int) (max.getY() - min.getY());
    }

    public double getArea() {
        return getWidth() * getHeight();
    }

    public Point2D getCenter(Point2D min, Point2D max) {
        // Create the min and max midpoints
        Point2D minPoint2D = new Point2D(min.getX(), min.getY());
        Point2D maxPoint2D = new Point2D(max.getX(), max.getY());

        // Get the center point of both midpoints
        return minPoint2D.midpoint(maxPoint2D);
    }

    public Point2D getCenter() {
        return getCenter(min, max);
    }

    public int size() {
        return (int) Math.sqrt(max.distance(min));
    }

    @Override
    public int compareTo(Bounds other) {
        return this.size() - other.size();
    }
}
