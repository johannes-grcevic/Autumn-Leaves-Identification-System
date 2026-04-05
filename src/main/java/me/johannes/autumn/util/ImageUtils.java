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
     * Converts the source image to black and white, where white pixels match
     * any of the reference colors within the saturation and brightness thresholds.
     *
     * @param source The source image.
     * @param referenceColors The reference colors to compare against.
     * @param hueTolerance The maximum hue difference allowed.
     * @param saturationThreshold The minimum saturation a pixel must have.
     * @param brightnessThreshold The minimum brightness a pixel must have.
     * @return A new image with white pixels where a color matched, black otherwise.
     */
    public static WritableImage getBlackAndWhite(Image source, List<Color> referenceColors, double hueTolerance, double saturationThreshold, double brightnessThreshold) {
        int width  = (int) source.getWidth();
        int height = (int) source.getHeight();

        WritableImage writableImage = new WritableImage(width, height);
        PixelReader reader = source.getPixelReader();
        PixelWriter writer = writableImage.getPixelWriter();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Color color = reader.getColor(x, y);

                Color output = isValidColor(color, referenceColors, hueTolerance, saturationThreshold, brightnessThreshold)
                        ? Color.WHITE
                        : Color.BLACK;

                writer.setColor(x, y, output);
            }
        }

        return writableImage;
    }

    /**
     * Returns true if the color's hue is within the hue tolerance of any reference
     * color, and the color's saturation and brightness exceed their thresholds.
     * @param color The pixel color to evaluate.
     * @param compareColors The reference colors to compare against.
     * @param hueTolerance The maximum hue difference allowed.
     * @param saturationThreshold The minimum saturation the pixel must have.
     * @param brightnessThreshold The minimum brightness the pixel must have.
     * @return True if the pixel matches any reference color within the thresholds.
     */
    public static boolean isValidColor(Color color, List<Color> compareColors, double hueTolerance, double saturationThreshold, double brightnessThreshold) {
        // Check the color saturation and brightness first
        if (color.getSaturation() <= saturationThreshold ||
                color.getBrightness() <= brightnessThreshold) {
            return false;
        }

        double colorHue = color.getHue();

        for (Color compareColor : compareColors) {
            double hueDifference = Math.abs(colorHue - compareColor.getHue());

            // Hue wraps around at 360°
            // This ensures that the hue difference is always within the tolerance window
            if (hueDifference > 180.0) {
                hueDifference = 360.0 - hueDifference;
            }

            // check if the hue difference is within the tolerance window
            if (hueDifference <= hueTolerance) {
                return true;
            }
        }

        return false;
    }
}