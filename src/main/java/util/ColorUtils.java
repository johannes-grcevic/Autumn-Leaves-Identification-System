package util;

import javafx.scene.paint.Color;

import java.util.Random;

public class ColorUtils {
    public static Color getRandomColor(Random rand) {
        return Color.rgb(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
    }
}
