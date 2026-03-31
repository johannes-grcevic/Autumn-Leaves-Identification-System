package util;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.List;

public class ImageUtils {
    public static WritableImage getBlackAndWhite(Image source, List<Color> compareColors, double saturationThreshold, double brightnessThreshold) {
        int width = (int) source.getWidth();
        int height = (int) source.getHeight();

        WritableImage writableImage = new WritableImage(width, height);
        PixelReader reader = source.getPixelReader();
        PixelWriter writer = writableImage.getPixelWriter();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {

                Color pixelColor = reader.getColor(x, y);

                if (isValidColor(pixelColor, compareColors, saturationThreshold, brightnessThreshold)) {
                    writer.setColor(x, y, Color.WHITE);
                }
                else {
                    writer.setColor(x, y, Color.BLACK);
                }
            }
        }

        return writableImage;
    }

    public static boolean isValidColor(Color pixel, List<Color> compareColors, double saturationThreshold, double brightnessThreshold) {
        double hue = pixel.getHue();

        for (Color color : compareColors) {
            if (color.getHue() >= hue &&
                    color.getSaturation() > saturationThreshold &&
                    color.getBrightness() > brightnessThreshold)

                return true;
        }

        return false;
    }

    public static boolean isAutumnLeaf(Color pixel) {
        double hue = pixel.getHue();
        double saturation = pixel.getSaturation();
        double brightness = pixel.getBrightness();

        // Detect brown/orange leaves
        return hue >= 15 && hue <= 50 && // orange/brown range
                saturation > 0.3 && // avoid gray areas
                brightness > 0.2; // avoid dark areas
    }
}
