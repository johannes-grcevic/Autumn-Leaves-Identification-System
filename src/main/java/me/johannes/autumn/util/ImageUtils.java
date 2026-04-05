package me.johannes.autumn.util;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.List;

public class ImageUtils {
    // Prevent instantiation
    private ImageUtils() {}

    /**
     * @param source The source image.
     * @param selectedColors The colors to compare against.
     * @param saturationThreshold The minimum saturation threshold for a given color.
     * @param brightnessThreshold The minimum brightness threshold for a given color.
     * @return A new image with black and white pixels based on the given colors.
     */
    public static WritableImage getBlackAndWhite(Image source, List<Color> selectedColors, double saturationThreshold, double brightnessThreshold) {
        int width = (int) source.getWidth();
        int height = (int) source.getHeight();

        WritableImage writableImage = new WritableImage(width, height);
        PixelReader reader = source.getPixelReader();
        PixelWriter writer = writableImage.getPixelWriter();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {

                Color pixelColor = reader.getColor(x, y);

                if (isValidColor(pixelColor, selectedColors, saturationThreshold, brightnessThreshold)) {
                    writer.setColor(x, y, Color.WHITE);
                }
                else {
                    writer.setColor(x, y, Color.BLACK);
                }
            }
        }

        return writableImage;
    }

    /**
     * @param color The color to check.
     * @param compareColors The colors to compare against.
     * @param saturationThreshold The minimum saturation threshold for a given color.
     * @param brightnessThreshold The minimum brightness threshold for a given color.
     * @return True if the hue, saturation, and brightness of the color are within the specified thresholds for any of the given colors.
     */
    public static boolean isValidColor(Color color, List<Color> compareColors, double saturationThreshold, double brightnessThreshold) {
        double hue = color.getHue();

        for (Color compareColor : compareColors) {
            if (compareColor.getHue() >= hue &&
                    compareColor.getSaturation() > saturationThreshold &&
                    compareColor.getBrightness() > brightnessThreshold)

                return true;
        }

        return false;
    }

    /**
     * @param pixel The pixel to check.
     * @return True if the pixel is in the autumn leaf color range.
     */
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
