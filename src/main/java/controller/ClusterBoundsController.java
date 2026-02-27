package controller;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class ClusterBoundsController {
    private Pane drawPane;
    private Scene targetScene;

    public Pane drawBounds(Image source, Color textFillColor, Color strokeColor, Scene target, ClusterController controller) {
        if (targetScene == null) {
            setTargetScene(target);
        }

        if (controller == null) {
            throw new IllegalArgumentException("ClusterController cannot be null!");
        }

        ImageView imageView = new ImageView(source);
        imageView.setPreserveRatio(false);
        imageView.setFitWidth(source.getWidth());
        imageView.setFitHeight(source.getHeight());

        drawPane = new Pane(imageView);
        drawPane.setPrefSize(source.getWidth(), source.getHeight());

        int[] clusterPixels = controller.getAllClusterPixels();
        int[] minX = controller.getBoundsMinX();
        int[] minY = controller.getBoundsMinY();
        int[] maxX = controller.getBoundsMaxX();
        int[] maxY = controller.getBoundsMaxY();
        int getMinClusterSize = controller.getMinClusterSize();

        // sort in ascending order //todo fix sorting
        //ArrayUtils.sort(clusterPixels);

        int clusterNumber = 0;

        for (int i = 0; i < clusterPixels.length; i++) {
            if (clusterPixels[i] < getMinClusterSize) continue;

            //System.out.println("Index: " + i + " -- " + "Pixels: " + clusterPixels[i]);

            clusterNumber++;

            int x = minX[i];
            int y = minY[i];
            int width = (maxX[i] - minX[i]) + 1;
            int height = (maxY[i] - minY[i]) + 1;

            Rectangle boundsRectangle = new Rectangle(x, y, width, height);
            boundsRectangle.setStroke(strokeColor);
            boundsRectangle.setFill(Color.TRANSPARENT);

            Label numberLabel = new Label(String.valueOf(clusterNumber));
            numberLabel.setVisible(false);
            numberLabel.setTextFill(textFillColor);
            numberLabel.setLayoutX(x);
            numberLabel.setLayoutY(y);

            drawPane.getChildren().addAll(boundsRectangle, numberLabel);
        }

        return drawPane;
    }

    public void setTargetScene(Scene target) {
        targetScene = target;

        // trigger scene changed event
        if (target != null) {
            onTargetSceneChanged(target);
        }
    }

    public Scene getTargetScene() {
        return targetScene;
    }

    // Events
    private void onTargetSceneChanged(Scene scene) {
        scene.setOnKeyPressed(this::onKeyPressed);
    }

    private void onKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.N) {
            for (Node node : drawPane.getChildren()) {
                if (node instanceof Label) {
                    node.setVisible(!node.isVisible());
                }
            }
        }
    }
}
