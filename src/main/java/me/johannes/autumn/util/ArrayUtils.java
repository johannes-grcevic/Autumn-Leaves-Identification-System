package me.johannes.autumn.util;

import java.util.Comparator;
import java.util.List;

public class ArrayUtils {
    // Prevent instantiation
    private ArrayUtils() {}

    /**
     * Sorts an int array using the Insertion Sort algorithm.
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
     * Sorts a primitive array using the Insertion Sort algorithm.
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

    /**
     * Sorts a List of type T using the Insertion Sort algorithm.
     */
    public static <T> void sort(List<T> list, Comparator<T> comparator) {
        int size = list.size();

        // Start from the second element (index 1),
        // since a single element is already "sorted"
        for (int i = 1; i < size; i++) {
            T key = list.get(i);
            int j = i - 1;

            /*
             * Shift elements that are greater than 'key'
             * one position to the right
             */
            while (j >= 0 && comparator.compare(list.get(j), key) > 0) {
                list.set(j + 1, list.get(j)); // Shift element right
                j = j - 1; // Move left in the list
            }
            list.set(j + 1, key); // Insert the key into its correct position
        }
    }
}
