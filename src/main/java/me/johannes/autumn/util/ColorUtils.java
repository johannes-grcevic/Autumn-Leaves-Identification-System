package me.johannes.autumn.util;

import javafx.scene.paint.Color;

import me.johannes.autumn.model.MyHashtable;

import java.util.Random;

@SuppressWarnings("SpellCheckingInspection")
public class ColorUtils {
    private static final MyHashtable<Color, String> CSS_COLOR_NAMES = new MyHashtable<>();

    // Prevent instantiation
    private ColorUtils() {}

    static {
        CSS_COLOR_NAMES.put(Color.web("aliceblue"), "aliceblue");
        CSS_COLOR_NAMES.put(Color.web("antiquewhite"), "antiquewhite");
        CSS_COLOR_NAMES.put(Color.web("aqua"), "aqua");
        CSS_COLOR_NAMES.put(Color.web("aquamarine"), "aquamarine");
        CSS_COLOR_NAMES.put(Color.web("azure"), "azure");
        CSS_COLOR_NAMES.put(Color.web("beige"), "beige");
        CSS_COLOR_NAMES.put(Color.web("bisque"), "bisque");
        CSS_COLOR_NAMES.put(Color.web("black"), "black");
        CSS_COLOR_NAMES.put(Color.web("blanchedalmond"), "blanchedalmond");
        CSS_COLOR_NAMES.put(Color.web("blue"), "blue");
        CSS_COLOR_NAMES.put(Color.web("blueviolet"), "blueviolet");
        CSS_COLOR_NAMES.put(Color.web("brown"), "brown");
        CSS_COLOR_NAMES.put(Color.web("burlywood"), "burlywood");
        CSS_COLOR_NAMES.put(Color.web("cadetblue"), "cadetblue");
        CSS_COLOR_NAMES.put(Color.web("chartreuse"), "chartreuse");
        CSS_COLOR_NAMES.put(Color.web("chocolate"), "chocolate");
        CSS_COLOR_NAMES.put(Color.web("coral"), "coral");
        CSS_COLOR_NAMES.put(Color.web("cornflowerblue"), "cornflowerblue");
        CSS_COLOR_NAMES.put(Color.web("cornsilk"), "cornsilk");
        CSS_COLOR_NAMES.put(Color.web("crimson"), "crimson");
        CSS_COLOR_NAMES.put(Color.web("cyan"), "cyan");
        CSS_COLOR_NAMES.put(Color.web("darkblue"), "darkblue");
        CSS_COLOR_NAMES.put(Color.web("darkcyan"), "darkcyan");
        CSS_COLOR_NAMES.put(Color.web("darkgoldenrod"), "darkgoldenrod");
        CSS_COLOR_NAMES.put(Color.web("darkgray"), "darkgray");
        CSS_COLOR_NAMES.put(Color.web("darkgreen"), "darkgreen");
        CSS_COLOR_NAMES.put(Color.web("darkkhaki"), "darkkhaki");
        CSS_COLOR_NAMES.put(Color.web("darkmagenta"), "darkmagenta");
        CSS_COLOR_NAMES.put(Color.web("darkolivegreen"), "darkolivegreen");
        CSS_COLOR_NAMES.put(Color.web("darkorange"), "darkorange");
        CSS_COLOR_NAMES.put(Color.web("darkorchid"), "darkorchid");
        CSS_COLOR_NAMES.put(Color.web("darkred"), "darkred");
        CSS_COLOR_NAMES.put(Color.web("darksalmon"), "darksalmon");
        CSS_COLOR_NAMES.put(Color.web("darkseagreen"), "darkseagreen");
        CSS_COLOR_NAMES.put(Color.web("darkslateblue"), "darkslateblue");
        CSS_COLOR_NAMES.put(Color.web("darkslategray"), "darkslategray");
        CSS_COLOR_NAMES.put(Color.web("darkturquoise"), "darkturquoise");
        CSS_COLOR_NAMES.put(Color.web("darkviolet"), "darkviolet");
        CSS_COLOR_NAMES.put(Color.web("deeppink"), "deeppink");
        CSS_COLOR_NAMES.put(Color.web("deepskyblue"), "deepskyblue");
        CSS_COLOR_NAMES.put(Color.web("dimgray"), "dimgray");
        CSS_COLOR_NAMES.put(Color.web("dodgerblue"), "dodgerblue");
        CSS_COLOR_NAMES.put(Color.web("firebrick"), "firebrick");
        CSS_COLOR_NAMES.put(Color.web("floralwhite"), "floralwhite");
        CSS_COLOR_NAMES.put(Color.web("forestgreen"), "forestgreen");
        CSS_COLOR_NAMES.put(Color.web("fuchsia"), "fuchsia");
        CSS_COLOR_NAMES.put(Color.web("gainsboro"), "gainsboro");
        CSS_COLOR_NAMES.put(Color.web("ghostwhite"), "ghostwhite");
        CSS_COLOR_NAMES.put(Color.web("gold"), "gold");
        CSS_COLOR_NAMES.put(Color.web("goldenrod"), "goldenrod");
        CSS_COLOR_NAMES.put(Color.web("gray"), "gray");
        CSS_COLOR_NAMES.put(Color.web("green"), "green");
        CSS_COLOR_NAMES.put(Color.web("greenyellow"), "greenyellow");
        CSS_COLOR_NAMES.put(Color.web("honeydew"), "honeydew");
        CSS_COLOR_NAMES.put(Color.web("hotpink"), "hotpink");
        CSS_COLOR_NAMES.put(Color.web("indianred"), "indianred");
        CSS_COLOR_NAMES.put(Color.web("indigo"), "indigo");
        CSS_COLOR_NAMES.put(Color.web("ivory"), "ivory");
        CSS_COLOR_NAMES.put(Color.web("khaki"), "khaki");
        CSS_COLOR_NAMES.put(Color.web("lavender"), "lavender");
        CSS_COLOR_NAMES.put(Color.web("lavenderblush"), "lavenderblush");
        CSS_COLOR_NAMES.put(Color.web("lawngreen"), "lawngreen");
        CSS_COLOR_NAMES.put(Color.web("lemonchiffon"), "lemonchiffon");
        CSS_COLOR_NAMES.put(Color.web("lightblue"), "lightblue");
        CSS_COLOR_NAMES.put(Color.web("lightcoral"), "lightcoral");
        CSS_COLOR_NAMES.put(Color.web("lightcyan"), "lightcyan");
        CSS_COLOR_NAMES.put(Color.web("lightgoldenrodyellow"), "lightgoldenrodyellow");
        CSS_COLOR_NAMES.put(Color.web("lightgray"), "lightgray");
        CSS_COLOR_NAMES.put(Color.web("lightgreen"), "lightgreen");
        CSS_COLOR_NAMES.put(Color.web("lightpink"), "lightpink");
        CSS_COLOR_NAMES.put(Color.web("lightsalmon"), "lightsalmon");
        CSS_COLOR_NAMES.put(Color.web("lightseagreen"), "lightseagreen");
        CSS_COLOR_NAMES.put(Color.web("lightskyblue"), "lightskyblue");
        CSS_COLOR_NAMES.put(Color.web("lightslategray"), "lightslategray");
        CSS_COLOR_NAMES.put(Color.web("lightsteelblue"), "lightsteelblue");
        CSS_COLOR_NAMES.put(Color.web("lightyellow"), "lightyellow");
        CSS_COLOR_NAMES.put(Color.web("lime"), "lime");
        CSS_COLOR_NAMES.put(Color.web("limegreen"), "limegreen");
        CSS_COLOR_NAMES.put(Color.web("linen"), "linen");
        CSS_COLOR_NAMES.put(Color.web("magenta"), "magenta");
        CSS_COLOR_NAMES.put(Color.web("maroon"), "maroon");
        CSS_COLOR_NAMES.put(Color.web("mediumaquamarine"), "mediumaquamarine");
        CSS_COLOR_NAMES.put(Color.web("mediumblue"), "mediumblue");
        CSS_COLOR_NAMES.put(Color.web("mediumorchid"), "mediumorchid");
        CSS_COLOR_NAMES.put(Color.web("mediumpurple"), "mediumpurple");
        CSS_COLOR_NAMES.put(Color.web("mediumseagreen"), "mediumseagreen");
        CSS_COLOR_NAMES.put(Color.web("mediumslateblue"), "mediumslateblue");
        CSS_COLOR_NAMES.put(Color.web("mediumspringgreen"), "mediumspringgreen");
        CSS_COLOR_NAMES.put(Color.web("mediumturquoise"), "mediumturquoise");
        CSS_COLOR_NAMES.put(Color.web("mediumvioletred"), "mediumvioletred");
        CSS_COLOR_NAMES.put(Color.web("midnightblue"), "midnightblue");
        CSS_COLOR_NAMES.put(Color.web("mintcream"), "mintcream");
        CSS_COLOR_NAMES.put(Color.web("mistyrose"), "mistyrose");
        CSS_COLOR_NAMES.put(Color.web("moccasin"), "moccasin");
        CSS_COLOR_NAMES.put(Color.web("navajowhite"), "navajowhite");
        CSS_COLOR_NAMES.put(Color.web("navy"), "navy");
        CSS_COLOR_NAMES.put(Color.web("oldlace"), "oldlace");
        CSS_COLOR_NAMES.put(Color.web("olive"), "olive");
        CSS_COLOR_NAMES.put(Color.web("olivedrab"), "olivedrab");
        CSS_COLOR_NAMES.put(Color.web("orange"), "orange");
        CSS_COLOR_NAMES.put(Color.web("orangered"), "orangered");
        CSS_COLOR_NAMES.put(Color.web("orchid"), "orchid");
        CSS_COLOR_NAMES.put(Color.web("palegoldenrod"), "palegoldenrod");
        CSS_COLOR_NAMES.put(Color.web("palegreen"), "palegreen");
        CSS_COLOR_NAMES.put(Color.web("paleturquoise"), "paleturquoise");
        CSS_COLOR_NAMES.put(Color.web("palevioletred"), "palevioletred");
        CSS_COLOR_NAMES.put(Color.web("papayawhip"), "papayawhip");
        CSS_COLOR_NAMES.put(Color.web("peachpuff"), "peachpuff");
        CSS_COLOR_NAMES.put(Color.web("peru"), "peru");
        CSS_COLOR_NAMES.put(Color.web("pink"), "pink");
        CSS_COLOR_NAMES.put(Color.web("plum"), "plum");
        CSS_COLOR_NAMES.put(Color.web("powderblue"), "powderblue");
        CSS_COLOR_NAMES.put(Color.web("purple"), "purple");
        CSS_COLOR_NAMES.put(Color.web("red"), "red");
        CSS_COLOR_NAMES.put(Color.web("rosybrown"), "rosybrown");
        CSS_COLOR_NAMES.put(Color.web("royalblue"), "royalblue");
        CSS_COLOR_NAMES.put(Color.web("saddlebrown"), "saddlebrown");
        CSS_COLOR_NAMES.put(Color.web("salmon"), "salmon");
        CSS_COLOR_NAMES.put(Color.web("sandybrown"), "sandybrown");
        CSS_COLOR_NAMES.put(Color.web("seagreen"), "seagreen");
        CSS_COLOR_NAMES.put(Color.web("seashell"), "seashell");
        CSS_COLOR_NAMES.put(Color.web("sienna"), "sienna");
        CSS_COLOR_NAMES.put(Color.web("silver"), "silver");
        CSS_COLOR_NAMES.put(Color.web("skyblue"), "skyblue");
        CSS_COLOR_NAMES.put(Color.web("slateblue"), "slateblue");
        CSS_COLOR_NAMES.put(Color.web("slategray"), "slategray");
        CSS_COLOR_NAMES.put(Color.web("snow"), "snow");
        CSS_COLOR_NAMES.put(Color.web("springgreen"), "springgreen");
        CSS_COLOR_NAMES.put(Color.web("steelblue"), "steelblue");
        CSS_COLOR_NAMES.put(Color.web("tan"), "tan");
        CSS_COLOR_NAMES.put(Color.web("teal"), "teal");
        CSS_COLOR_NAMES.put(Color.web("thistle"), "thistle");
        CSS_COLOR_NAMES.put(Color.web("tomato"), "tomato");
        CSS_COLOR_NAMES.put(Color.web("turquoise"), "turquoise");
        CSS_COLOR_NAMES.put(Color.web("violet"), "violet");
        CSS_COLOR_NAMES.put(Color.web("wheat"), "wheat");
        CSS_COLOR_NAMES.put(Color.web("white"), "white");
        CSS_COLOR_NAMES.put(Color.web("whitesmoke"), "whitesmoke");
        CSS_COLOR_NAMES.put(Color.web("yellow"), "yellow");
        CSS_COLOR_NAMES.put(Color.web("yellowgreen"), "yellowgreen");
    }

    /**
     * @param rand Random number generator to use
     * @return A random color from 0-255 in the rgb range.
     */
    public static Color getRandomColor(Random rand) {
        return Color.rgb(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
    }

    /**
     * @param color The color to get the name of.
     * @return The name of the color, or null if the color is not found.
     */
    public static String getColorName(Color color) {
        if (color == null) return null;

        // try to match the exact color first
        String name = CSS_COLOR_NAMES.get(color);
        if (name != null) return name;

        // fall back to closest color match
        String closestColor = null;
        double minDistance = Double.MAX_VALUE;

        for (MyHashtable.Entry<Color, String> entry : CSS_COLOR_NAMES.entries()) {
            double distance = colorDistance(entry.getKey(), color);

            if (distance < minDistance) {
                minDistance = distance;
                closestColor = entry.getValue();
            }
        }

        return closestColor;
    }

    /**
     * @param a The first color.
     * @param b The second color.
     * @return The distance between the two colors.
     */
    private static double colorDistance(Color a, Color b) {
        double distRed = a.getRed() - b.getRed();
        double distGreen = a.getGreen() - b.getGreen();
        double distBlue = a.getBlue() - b.getBlue();

        return distRed * distRed + distGreen * distGreen + distBlue * distBlue;
    }
}
