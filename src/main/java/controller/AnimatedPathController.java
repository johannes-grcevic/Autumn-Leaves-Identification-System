package controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import model.ArrayList;
import model.NearestNeighbor;

public class AnimatedPathController {
    // path to animate along
    private final Path path;
    // nearest neighbor algorithm
    private final NearestNeighbor nn;

    public AnimatedPathController(Point2D[] nodePoints) {
        path = new Path();
        nn = new NearestNeighbor(nodePoints);
    }

    public void drawAnimatedPath(Point2D start, Pane target, Duration duration, Color strokeColor, double strokeWidth) {
        clearPath(); // clear the path before drawing a new one

        path.setStroke(strokeColor);
        path.setStrokeWidth(strokeWidth);
        path.setFill(Color.TRANSPARENT);

        // draw the animated path
        target.getChildren().add(path);

        // get the shortest path between the start and end points using the nearest neighbor algorithm
        ArrayList<Point2D> shortestPath = nn.findShortestPath(start);

        // get all the rectangles in the target pane
        ArrayList<Rectangle> unordered = new ArrayList<>();

        for (Node node : target.getChildren()) {
            if (node instanceof Rectangle rectangle) {
                unordered.add(rectangle);
            }
        }

        // get all the rectangles in the target pane
        // sort rectangles to match the order of the path points by matching centers
        ArrayList<Rectangle> rectangles = new ArrayList<>(shortestPath.size());
        ArrayList<Rectangle> remaining = new ArrayList<>(unordered);

        for (Point2D current : shortestPath) {
            Rectangle nearestRect = null;
            double minDistance = Double.MAX_VALUE;

            for (Rectangle rectangle : remaining) {
                double centerX = rectangle.getX() + rectangle.getWidth() / 2;
                double centerY = rectangle.getY() + rectangle.getHeight() / 2;

                double distance = current.distance(centerX, centerY);

                if (distance < minDistance) {
                    minDistance = distance;
                    nearestRect = rectangle;
                }
            }

            if (nearestRect != null) {
                rectangles.add(nearestRect);
                remaining.remove(nearestRect);
            }
        }

        // set the duration of each step in the path
        Duration stepDuration = duration.divide(shortestPath.size());
        Timeline timeline = new Timeline();

        // move to the start point of the path
        MoveTo startPoint = new MoveTo(shortestPath.getFirst().getX(), shortestPath.getFirst().getY());
        path.getElements().add(startPoint);

        // set the first rectangle to yellow because it's skipped in the path
        rectangles.getFirst().setStroke(Color.YELLOW);

        // skip the first point in the path since it's already drawn
        for (int i = 1; i < shortestPath.size(); i++) {
            // get the next point in the path
            Point2D nextPoint = shortestPath.get(i);

            int index = i;

            // create a key frame for each step
            KeyFrame keyFrame = new KeyFrame (
                    // add a delay between each step
                    stepDuration.multiply(i + 1),
                    _ -> {

                        // draw a line to the next point
                        LineTo lineToNextPoint = new LineTo(nextPoint.getX(), nextPoint.getY());

                        // set the current rectangle to yellow and the previous one to blue
                        rectangles.get(index).setStroke(Color.YELLOW);
                        rectangles.get(index - 1).setStroke(Color.BLUE);

                        // add each point to the path
                        path.getElements().add(lineToNextPoint);

                    });

            timeline.getKeyFrames().add(keyFrame);
        }

        timeline.play();

        // set the last rectangle to blue when the animation is finished
        timeline.setOnFinished((_ -> rectangles.getLast().setStroke(Color.BLUE)));
    }

    public Path getPath() {
        return path;
    }

    public void clearPath() {
        path.getElements().clear();
    }
}
