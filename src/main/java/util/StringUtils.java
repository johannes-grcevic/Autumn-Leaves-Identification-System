package util;

public class StringUtils {
    public static String capitalize(String str) {
        return str.charAt(0) + str.substring(1).toLowerCase();
    }
}
