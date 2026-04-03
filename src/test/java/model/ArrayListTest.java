package model;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class ArrayListTest {

    @Test
    void testRemoveObjectBug() {
        ArrayList<String> list = new ArrayList<>();
        list.add("A");
        list.add("A");
        list.add("B");
        
        // This should remove both "A"s if it's meant to remove all, or one if it's meant to remove one.
        // The current implementation tries to remove all but has a bug with index shifting.
        list.remove("A");
        
        // If it was meant to remove ALL "A"s, the size should be 1.
        // If it was meant to remove FIRST "A", size should be 2.
        // In current implementation, it skips the second "A" because of i++.
        assertEquals(1, list.size(), "Should remove all 'A's");
        assertFalse(list.contains("A"));
    }

    @Test
    void testContainsAllBug() {
        ArrayList<String> list = new ArrayList<>();
        list.add("A");
        list.add("B");
        
        assertTrue(list.containsAll(List.of("A", "B")), "containsAll should return true when all elements are present");
    }

    @Test
    void testIteratorNextException() {
        ArrayList<String> list = new ArrayList<>();
        var it = list.iterator();
        assertThrows(NoSuchElementException.class, it::next, "Iterator.next() should throw NoSuchElementException when empty");
    }

    @Test
    void testResizeZero() {
        ArrayList<String> list = new ArrayList<>(0);
        assertDoesNotThrow(() -> list.add("A"), "Should be able to add to an initially empty list");
        assertEquals(1, list.size());
        assertEquals("A", list.get(0));
    }

    @Test
    void testToArrayGeneric() {
        ArrayList<String> list = new ArrayList<>();
        list.add("A");
        String[] arr = new String[1];
        String[] result = list.toArray(arr);
        assertNotNull(result);
        assertEquals("A", result[0]);
    }
}
