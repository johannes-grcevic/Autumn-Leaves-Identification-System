package util;

public class Utils {
    public static double truncateToTwoDecimalPlaces(double value) {
        return (int) (value * 100) / 100.0;
    }

    public static String capitalize(String str) {
        return str.charAt(0) + str.substring(1).toLowerCase();
    }
}
