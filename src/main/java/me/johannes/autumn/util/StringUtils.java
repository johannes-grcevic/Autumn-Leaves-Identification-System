package me.johannes.autumn.util;

public class StringUtils {
    // Prevent instantiation
    private StringUtils() {}

    /**
     * @param str The string to capitalize.
     * @return The first letter of the string is capitalized and the rest is lowercase.
     */
    public static String capitalize(String str) {
        return Character.toUpperCase(str.charAt(0)) + str.substring(1).toLowerCase();
    }
}
