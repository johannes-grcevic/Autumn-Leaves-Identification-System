package me.johannes.autumn.util;

import java.util.Random;

public class BenchmarkUtils {
    public static int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    // Source - https://stackoverflow.com/a/4952045
    // Posted by Argote, modified by community. See post 'Timeline' for change history
    // Retrieved 2026-02-10, License - CC BY-SA 2.5
    public static String generateRandomWord(int wordLength) {
        Random r = new Random(); // Initialize a Random Number Generator with SysTime as the seed
        StringBuilder sb = new StringBuilder(wordLength);
        for (int i = 0; i < wordLength; i++) { // For each letter in the word
            char tmp = (char) ('a' + r.nextInt('z' - 'a')); // Generate a letter between a and z
            sb.append(tmp); // Add it to the String
        }
        return sb.toString();
    }
}
