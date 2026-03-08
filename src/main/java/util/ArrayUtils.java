package util;

import java.util.Comparator;

public class ArrayUtils {
    /**
     * Sorts an array of ints using the Insertion sort algorithm.
     */
    public static void sort(int[] array) {
        int size = array.length;

        // Start from the second element (index 1),
        // since a single element is already "sorted"
        for (int i = 1; i < size; i++) {
            int key = array[i];
            int j = i - 1; // Index of the last element in the sorted portion

            /*
             * Shift elements that are greater than 'key'
             * one position to the right
             */
            while (j >= 0 && array[j] > key) {
                array[j + 1] = array[j]; // Shift element right
                j = j - 1; // Move left in the array
            }
            array[j + 1] = key; // Insert the key into its correct position
        }
    }

    /**
     * Sorts a generic array using the Insertion sort algorithm.
     */
    public static <T> void sort(T[] array, Comparator<T> comparator) {
        int size = array.length;

        // Start from the second element (index 1),
        // since a single element is already "sorted"
        for (int i = 1; i < size; i++) {
            T key = array[i];
            int j = i - 1;  // Index of the last element in the sorted portion

            /*
             * Shift elements that are greater than 'key'
             * one position to the right
             */
            while (j >= 0 && comparator.compare(array[j], key) > 0) {
                array[j + 1] = array[j]; // Shift element right
                j = j - 1; // Move left in the array
            }
            array[j + 1] = key; // Insert the key into its correct position
        }
    }
}
