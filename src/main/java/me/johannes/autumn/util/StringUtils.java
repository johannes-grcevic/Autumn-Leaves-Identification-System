package me.johannes.autumn.util;

public final class StringUtils {
    private StringUtils() {}

    /**
     * Capitalizes the first letter of {@code str} and lowercases the rest.
     *
     * @param str the string to capitalize; must not be {@code null}
     * @return the capitalized string, or an empty string if {@code str} is empty
     * @throws NullPointerException if {@code str} is {@code null}
     */
    public static String capitalize(String str) {
        requireNonNull(str, "str");

        if (str.isEmpty()) return str;

        return Character.toUpperCase(str.charAt(0)) + str.substring(1).toLowerCase();
    }

    /**
     * Capitalizes the first letter of every word in {@code str}.
     * Whitespace delimits words.
     *
     * @param str the string to convert; must not be {@code null}
     * @return the title-cased string
     * @throws NullPointerException if {@code str} is {@code null}
     */
    public static String toTitleCase(String str) {
        requireNonNull(str, "str");
        if (str.isEmpty()) return str;

        StringBuilder sb = new StringBuilder(str.length());
        boolean capitalizeNext = true;

        // Iterate through each character in the string
        for (char character : str.toCharArray()) {
            if (Character.isWhitespace(character)) {
                capitalizeNext = true;
                sb.append(character);
            }
            else {
                sb.append(capitalizeNext ? Character.toUpperCase(character) : Character.toLowerCase(character));
                capitalizeNext = false;
            }
        }

        return sb.toString();
    }

    /**
     * Formats an integer as a percentage string (e.g. {@code 75} → {@code "75%"}).
     *
     * @param value the percentage value
     * @return the formatted percentage string
     */
    public static String toPercentageOf(int value) {
        return value + "%";
    }

    /**
     * Formats a ratio in the range [0.0, 1.0] as a percentage string
     * with two decimal places (e.g. {@code 0.753} → {@code "75.30%"}).
     *
     * @param value the ratio to convert; expected to be between 0.0 and 1.0
     * @return the formatted percentage string
     */
    public static String toPercentageOf(double value) {
        return toPercentageOf(value, 2);
    }

    /**
     * Formats a ratio in the range [0.0, 1.0] as a percentage string
     * with the given number of decimal places (e.g. {@code 0.753, 1} → {@code "75.3%"}).
     *
     * @param value    the ratio to convert; expected to be between 0.0 and 1.0
     * @param decimals the number of decimal places; must be ≥ 0
     * @return the formatted percentage string
     * @throws IllegalArgumentException if {@code decimals} is negative
     */
    public static String toPercentageOf(double value, int decimals) {
        if (decimals < 0) throw new IllegalArgumentException("decimals must be >= 0, got " + decimals);

        String format = "%." + decimals + "f%%";

        return String.format(format, value * 100);
    }

    // -------------------------------------------------------------------------
    // Internal helpers
    // -------------------------------------------------------------------------
    private static void requireNonNull(Object value, String name) {
        if (value == null) throw new NullPointerException("'" + name + "' must not be null");
    }
}