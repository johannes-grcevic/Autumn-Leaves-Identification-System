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
        CSS_COLOR_NAMES.put(Color.web("aliceblue"), "Alice Blue");
        CSS_COLOR_NAMES.put(Color.web("antiquewhite"), "Antique White");
        CSS_COLOR_NAMES.put(Color.web("aqua"), "Aqua");
        CSS_COLOR_NAMES.put(Color.web("aquamarine"), "Aquamarine");
        CSS_COLOR_NAMES.put(Color.web("azure"), "Azure");
        CSS_COLOR_NAMES.put(Color.web("beige"), "Beige");
        CSS_COLOR_NAMES.put(Color.web("bisque"), "Bisque");
        CSS_COLOR_NAMES.put(Color.web("black"), "Black");
        CSS_COLOR_NAMES.put(Color.web("blanchedalmond"), "Blanched Almond");
        CSS_COLOR_NAMES.put(Color.web("blue"), "Blue");
        CSS_COLOR_NAMES.put(Color.web("blueviolet"), "Blue Violet");
        CSS_COLOR_NAMES.put(Color.web("brown"), "Brown");
        CSS_COLOR_NAMES.put(Color.web("burlywood"), "Burly Wood");
        CSS_COLOR_NAMES.put(Color.web("cadetblue"), "Cadet Blue");
        CSS_COLOR_NAMES.put(Color.web("chartreuse"), "Chartreuse");
        CSS_COLOR_NAMES.put(Color.web("chocolate"), "Chocolate");
        CSS_COLOR_NAMES.put(Color.web("coral"), "Coral");
        CSS_COLOR_NAMES.put(Color.web("cornflowerblue"), "Cornflower Blue");
        CSS_COLOR_NAMES.put(Color.web("cornsilk"), "Corn Silk");
        CSS_COLOR_NAMES.put(Color.web("crimson"), "Crimson");
        CSS_COLOR_NAMES.put(Color.web("cyan"), "Cyan");
        CSS_COLOR_NAMES.put(Color.web("darkblue"), "Dark Blue");
        CSS_COLOR_NAMES.put(Color.web("darkcyan"), "Dark Cyan");
        CSS_COLOR_NAMES.put(Color.web("darkgoldenrod"), "Dark Goldenrod");
        CSS_COLOR_NAMES.put(Color.web("darkgray"), "Dark Gray");
        CSS_COLOR_NAMES.put(Color.web("darkgreen"), "Dark Green");
        CSS_COLOR_NAMES.put(Color.web("darkkhaki"), "Dark Khaki");
        CSS_COLOR_NAMES.put(Color.web("darkmagenta"), "Dark Magenta");
        CSS_COLOR_NAMES.put(Color.web("darkolivegreen"), "Dark Olive Green");
        CSS_COLOR_NAMES.put(Color.web("darkorange"), "Dark Orange");
        CSS_COLOR_NAMES.put(Color.web("darkorchid"), "Dark Orchid");
        CSS_COLOR_NAMES.put(Color.web("darkred"), "Dark Red");
        CSS_COLOR_NAMES.put(Color.web("darksalmon"), "Dark Salmon");
        CSS_COLOR_NAMES.put(Color.web("darkseagreen"), "Dark Sea Green");
        CSS_COLOR_NAMES.put(Color.web("darkslateblue"), "dark Slate Blue");
        CSS_COLOR_NAMES.put(Color.web("darkslategray"), "Dark Slate Gray");
        CSS_COLOR_NAMES.put(Color.web("darkturquoise"), "Dark Turquoise");
        CSS_COLOR_NAMES.put(Color.web("darkviolet"), "Dark Violet");
        CSS_COLOR_NAMES.put(Color.web("deeppink"), "Deep Pink");
        CSS_COLOR_NAMES.put(Color.web("deepskyblue"), "Deep Skyblue");
        CSS_COLOR_NAMES.put(Color.web("dimgray"), "Dim Gray");
        CSS_COLOR_NAMES.put(Color.web("dodgerblue"), "Dodger Blue");
        CSS_COLOR_NAMES.put(Color.web("firebrick"), "Fire Brick");
        CSS_COLOR_NAMES.put(Color.web("floralwhite"), "Floral White");
        CSS_COLOR_NAMES.put(Color.web("forestgreen"), "Forest Green");
        CSS_COLOR_NAMES.put(Color.web("fuchsia"), "Fuchsia");
        CSS_COLOR_NAMES.put(Color.web("gainsboro"), "Gainsboro");
        CSS_COLOR_NAMES.put(Color.web("ghostwhite"), "Ghost White");
        CSS_COLOR_NAMES.put(Color.web("gold"), "Gold");
        CSS_COLOR_NAMES.put(Color.web("goldenrod"), "Goldenrod");
        CSS_COLOR_NAMES.put(Color.web("gray"), "Gray");
        CSS_COLOR_NAMES.put(Color.web("green"), "Green");
        CSS_COLOR_NAMES.put(Color.web("greenyellow"), "GreenYellow");
        CSS_COLOR_NAMES.put(Color.web("honeydew"), "Honeydew");
        CSS_COLOR_NAMES.put(Color.web("hotpink"), "Hot Pink");
        CSS_COLOR_NAMES.put(Color.web("indianred"), "Indian Red");
        CSS_COLOR_NAMES.put(Color.web("indigo"), "Indigo");
        CSS_COLOR_NAMES.put(Color.web("ivory"), "Ivory");
        CSS_COLOR_NAMES.put(Color.web("khaki"), "Khaki");
        CSS_COLOR_NAMES.put(Color.web("lavender"), "Lavender");
        CSS_COLOR_NAMES.put(Color.web("lavenderblush"), "LavenderBlush");
        CSS_COLOR_NAMES.put(Color.web("lawngreen"), "Lawn Green");
        CSS_COLOR_NAMES.put(Color.web("lemonchiffon"), "Lemon Chiffon");
        CSS_COLOR_NAMES.put(Color.web("lightblue"), "Light Blue");
        CSS_COLOR_NAMES.put(Color.web("lightcoral"), "Light Coral");
        CSS_COLOR_NAMES.put(Color.web("lightcyan"), "Light Cyan");
        CSS_COLOR_NAMES.put(Color.web("lightgoldenrodyellow"), "Light Goldenrod Yellow");
        CSS_COLOR_NAMES.put(Color.web("lightgray"), "Light Gray");
        CSS_COLOR_NAMES.put(Color.web("lightgreen"), "Light Green");
        CSS_COLOR_NAMES.put(Color.web("lightpink"), "Light Pink");
        CSS_COLOR_NAMES.put(Color.web("lightsalmon"), "Light Salmon");
        CSS_COLOR_NAMES.put(Color.web("lightseagreen"), "Light Sea Green");
        CSS_COLOR_NAMES.put(Color.web("lightskyblue"), "Light Sky Blue");
        CSS_COLOR_NAMES.put(Color.web("lightslategray"), "Light Slate Gray");
        CSS_COLOR_NAMES.put(Color.web("lightsteelblue"), "Light Steel Blue");
        CSS_COLOR_NAMES.put(Color.web("lightyellow"), "Light Yellow");
        CSS_COLOR_NAMES.put(Color.web("lime"), "Lime");
        CSS_COLOR_NAMES.put(Color.web("limegreen"), "Lime Green");
        CSS_COLOR_NAMES.put(Color.web("linen"), "Linen");
        CSS_COLOR_NAMES.put(Color.web("magenta"), "Magenta");
        CSS_COLOR_NAMES.put(Color.web("maroon"), "Maroon");
        CSS_COLOR_NAMES.put(Color.web("mediumaquamarine"), "Medium Aquamarine");
        CSS_COLOR_NAMES.put(Color.web("mediumblue"), "Bedium Blue");
        CSS_COLOR_NAMES.put(Color.web("mediumorchid"), "Medium Orchid");
        CSS_COLOR_NAMES.put(Color.web("mediumpurple"), "Medium Purple");
        CSS_COLOR_NAMES.put(Color.web("mediumseagreen"), "Medium Sea Green");
        CSS_COLOR_NAMES.put(Color.web("mediumslateblue"), "Medium Slate Blue");
        CSS_COLOR_NAMES.put(Color.web("mediumspringgreen"), "Medium Spring Green");
        CSS_COLOR_NAMES.put(Color.web("mediumturquoise"), "Medium Turquoise");
        CSS_COLOR_NAMES.put(Color.web("mediumvioletred"), "Medium Violet Red");
        CSS_COLOR_NAMES.put(Color.web("midnightblue"), "Midnight Blue");
        CSS_COLOR_NAMES.put(Color.web("mintcream"), "Mint Cream");
        CSS_COLOR_NAMES.put(Color.web("mistyrose"), "Misty Rose");
        CSS_COLOR_NAMES.put(Color.web("moccasin"), "Moccasin");
        CSS_COLOR_NAMES.put(Color.web("navajowhite"), "Navajo White");
        CSS_COLOR_NAMES.put(Color.web("navy"), "Navy");
        CSS_COLOR_NAMES.put(Color.web("oldlace"), "OldLace");
        CSS_COLOR_NAMES.put(Color.web("olive"), "Olive");
        CSS_COLOR_NAMES.put(Color.web("olivedrab"), "Olive Drab");
        CSS_COLOR_NAMES.put(Color.web("orange"), "Orange");
        CSS_COLOR_NAMES.put(Color.web("orangered"), "Orange Red");
        CSS_COLOR_NAMES.put(Color.web("orchid"), "Orchid");
        CSS_COLOR_NAMES.put(Color.web("palegoldenrod"), "Pale Goldenrod");
        CSS_COLOR_NAMES.put(Color.web("palegreen"), "Pale Green");
        CSS_COLOR_NAMES.put(Color.web("paleturquoise"), "Pale Turquoise");
        CSS_COLOR_NAMES.put(Color.web("palevioletred"), "Pale Violet Red");
        CSS_COLOR_NAMES.put(Color.web("papayawhip"), "Papaya Whip");
        CSS_COLOR_NAMES.put(Color.web("peachpuff"), "Peach Puff");
        CSS_COLOR_NAMES.put(Color.web("peru"), "Peru");
        CSS_COLOR_NAMES.put(Color.web("pink"), "Pink");
        CSS_COLOR_NAMES.put(Color.web("plum"), "Plum");
        CSS_COLOR_NAMES.put(Color.web("powderblue"), "Powder Blue");
        CSS_COLOR_NAMES.put(Color.web("purple"), "Purple");
        CSS_COLOR_NAMES.put(Color.web("red"), "Red");
        CSS_COLOR_NAMES.put(Color.web("rosybrown"), "Rosy Brown");
        CSS_COLOR_NAMES.put(Color.web("royalblue"), "Royal Blue");
        CSS_COLOR_NAMES.put(Color.web("saddlebrown"), "Saddle Brown");
        CSS_COLOR_NAMES.put(Color.web("salmon"), "Salmon");
        CSS_COLOR_NAMES.put(Color.web("sandybrown"), "Sandy Brown");
        CSS_COLOR_NAMES.put(Color.web("seagreen"), "Sea Green");
        CSS_COLOR_NAMES.put(Color.web("seashell"), "SeaShell");
        CSS_COLOR_NAMES.put(Color.web("sienna"), "Sienna");
        CSS_COLOR_NAMES.put(Color.web("silver"), "Silver");
        CSS_COLOR_NAMES.put(Color.web("skyblue"), "Sky Blue");
        CSS_COLOR_NAMES.put(Color.web("slateblue"), "Slate Blue");
        CSS_COLOR_NAMES.put(Color.web("slategray"), "Slate Gray");
        CSS_COLOR_NAMES.put(Color.web("snow"), "Snow");
        CSS_COLOR_NAMES.put(Color.web("springgreen"), "Spring Green");
        CSS_COLOR_NAMES.put(Color.web("steelblue"), "Steel Blue");
        CSS_COLOR_NAMES.put(Color.web("tan"), "Tan");
        CSS_COLOR_NAMES.put(Color.web("teal"), "Teal");
        CSS_COLOR_NAMES.put(Color.web("thistle"), "Thistle");
        CSS_COLOR_NAMES.put(Color.web("tomato"), "Tomato");
        CSS_COLOR_NAMES.put(Color.web("turquoise"), "Turquoise");
        CSS_COLOR_NAMES.put(Color.web("violet"), "Violet");
        CSS_COLOR_NAMES.put(Color.web("wheat"), "Wheat");
        CSS_COLOR_NAMES.put(Color.web("white"), "White");
        CSS_COLOR_NAMES.put(Color.web("whitesmoke"), "White Smoke");
        CSS_COLOR_NAMES.put(Color.web("yellow"), "Yellow");
        CSS_COLOR_NAMES.put(Color.web("yellowgreen"), "YellowGreen");
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
