package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class ArrayListTest {

    private ArrayList<String> list;

    @BeforeEach
    void setUp() {
        list = new ArrayList<>();
    }

    @Test
    void testConstructorEmpty() {
        ArrayList<Integer> intList = new ArrayList<>();

        assertEquals(0, intList.size());
        assertTrue(intList.isEmpty());
    }

    @Test
    void testConstructorInitialCapacity() {
        ArrayList<Integer> intList = new ArrayList<>(20);

        assertEquals(0, intList.size());
        assertTrue(intList.isEmpty());
    }

    @Test
    void testConstructorWithCollection() {
        List<String> source = List.of("A", "B", "C");
        ArrayList<String> newList = new ArrayList<>(source);

        assertEquals(3, newList.size());
        assertEquals("A", newList.get(0));
        assertEquals("B", newList.get(1));
        assertEquals("C", newList.get(2));
    }

    @Test
    void testConstructorWithEmptyCollection() {
        ArrayList<String> newList = new ArrayList<>(Collections.emptyList());

        assertEquals(0, newList.size());
        assertTrue(newList.isEmpty());
    }

    @Test
    void testAdd() {
        assertTrue(list.add("A"));
        assertEquals(1, list.size());
        assertEquals("A", list.getFirst());
        
        assertTrue(list.add("B"));
        assertEquals(2, list.size());
        assertEquals("B", list.get(1));
    }

    @Test
    void testAddAtIndex() {
        list.add("A");
        list.add("C");
        list.add(1, "B");
        
        assertEquals(3, list.size());
        assertEquals("A", list.get(0));
        assertEquals("B", list.get(1));
        assertEquals("C", list.get(2));
        
        list.add(0, "Start");
        assertEquals("Start", list.get(0));
        assertEquals(4, list.size());
        
        list.add(4, "End");
        assertEquals("End", list.get(4));
        assertEquals(5, list.size());
    }

    @Test
    void testAddAtIndexOutOfBounds() {
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(-1, "X"));
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(1, "X"));
    }

    @Test
    void testAddAll() {
        List<String> toAdd = List.of("A", "B", "C");

        assertTrue(list.addAll(toAdd));
        assertEquals(3, list.size());
        assertEquals("A", list.get(0));
        assertEquals("B", list.get(1));
        assertEquals("C", list.get(2));
        
        assertFalse(list.addAll(Collections.emptyList()));
    }

    @Test
    void testRemoveAtIndex() {
        list.add("A");
        list.add("B");
        list.add("C");
        
        list.remove(1);
        assertEquals(2, list.size());
        assertEquals("A", list.get(0));
        assertEquals("C", list.get(1));
        
        list.removeFirst();
        assertEquals(1, list.size());
        assertEquals("C", list.getFirst());
        
        list.removeFirst();
        assertEquals(0, list.size());
        assertTrue(list.isEmpty());
    }

    @Test
    void testRemoveAtIndexOutOfBounds() {
        assertThrows(IndexOutOfBoundsException.class, () -> list.removeFirst());

        list.add("A");
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(-1));
    }

    @Test
    void testRemoveByObject() {
        list.add("A");
        list.add("B");
        list.add("A");
        
        assertTrue(list.remove("A"));
        assertEquals(2, list.size());
        assertEquals("B", list.get(0));
        assertEquals("A", list.get(1));
        
        assertFalse(list.remove("X"));
    }

    @Test
    void testRemoveAll() {
        list.add("A");
        list.add("B");
        list.add("A");
        list.add("C");
        
        assertTrue(list.removeAll(List.of("A")));
        assertEquals(2, list.size());
        assertFalse(list.contains("A"));
        assertEquals("B", list.get(0));
        assertEquals("C", list.get(1));
    }

    @Test
    void testGet() {
        list.add("A");

        assertEquals("A", list.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(1));
    }

    @Test
    void testGetFirst() {
        assertThrows(NoSuchElementException.class, () -> list.getFirst());

        list.add("A");
        list.add("B");
        assertEquals("A", list.getFirst());
    }

    @Test
    void testGetLast() {
        assertThrows(NoSuchElementException.class, () -> list.getLast());

        list.add("A");
        list.add("B");
        assertEquals("B", list.getLast());
    }

    @Test
    void testSet() {
        list.add("A");
        list.set(0, "B");

        assertEquals("B", list.getFirst());
        assertThrows(IndexOutOfBoundsException.class, () -> list.set(1, "C"));
    }

    @Test
    void testIsEmpty() {
        assertTrue(list.isEmpty());

        list.add("A");
        assertFalse(list.isEmpty());

        list.clear();
        assertTrue(list.isEmpty());
    }

    @Test
    void testContains() {
        assertFalse(list.contains("A"));

        list.add("A");

        assertTrue(list.contains("A"));
        assertFalse(list.contains("B"));
    }

    @Test
    void testContainsAll() {
        list.add("A");
        list.add("B");

        assertTrue(list.containsAll(List.of("A", "B")));
        assertTrue(list.contains("A"));
        assertFalse(list.containsAll(List.of("A", "C")));
    }

    @Test
    void testClear() {
        list.add("A");
        list.add("B");
        list.clear();

        assertEquals(0, list.size());
        assertTrue(list.isEmpty());
    }

    @Test
    void testToArray() {
        list.add("A");
        list.add("B");

        Object[] arr = list.toArray();

        assertEquals(2, arr.length);
        assertEquals("A", arr[0]);
        assertEquals("B", arr[1]);
    }

    @Test
    void testToArrayGeneric() {
        list.add("A");
        list.add("B");

        String[] arr = new String[2];
        String[] result = list.toArray(arr);

        assertSame(arr, result);
        assertEquals("A", result[0]);
        assertEquals("B", result[1]);

        String[] smallArr = new String[0];
        result = list.toArray(smallArr);

        assertNotSame(smallArr, result);
        assertEquals(2, result.length);
        assertEquals("A", result[0]);
        assertEquals("B", result[1]);
    }

    @Test
    void testRetainAll() {
        list.add("A");
        list.add("B");
        list.add("C");
        
        assertTrue(list.retainAll(List.of("A", "C")));
        assertEquals(2, list.size());
        assertTrue(list.contains("A"));
        assertTrue(list.contains("C"));
        assertFalse(list.contains("B"));
        
        assertFalse(list.retainAll(List.of("A", "C")));
    }

    @Test
    void testIterator() {
        list.add("A");
        list.add("B");
        
        var it = list.iterator();
        assertTrue(it.hasNext());
        assertEquals("A", it.next());
        assertTrue(it.hasNext());
        assertEquals("B", it.next());
        assertFalse(it.hasNext());
        assertThrows(NoSuchElementException.class, it::next);
    }

    @Test
    void testResize() {
        // Default capacity is 10.
        for (int i = 0; i < 15; i++) {
            list.add("Element " + i);
        }
        assertEquals(15, list.size());
        for (int i = 0; i < 15; i++) {
            assertEquals("Element " + i, list.get(i));
        }
    }
}
