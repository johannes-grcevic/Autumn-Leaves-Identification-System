package me.johannes.autumn.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class MyArrayListTest {

    private MyArrayList<String> arrayList;

    @BeforeEach
    void setUp() {
        arrayList = new MyArrayList<>();
    }

    @AfterEach
    void tearDown() {
        arrayList = null;
    }

    @Test
    void testConstructorEmpty() {
        MyArrayList<Integer> intList = new MyArrayList<>();

        assertEquals(0, intList.size());
        assertTrue(intList.isEmpty());
    }

    @Test
    void testConstructorInitialCapacity() {
        MyArrayList<Integer> intList = new MyArrayList<>(20);

        assertEquals(0, intList.size());
        assertTrue(intList.isEmpty());
    }

    @Test
    void testConstructorWithCollection() {
        List<String> source = List.of("A", "B", "C");
        MyArrayList<String> newList = new MyArrayList<>(source);

        assertEquals(3, newList.size());
        assertEquals("A", newList.get(0));
        assertEquals("B", newList.get(1));
        assertEquals("C", newList.get(2));
    }

    @Test
    void testConstructorWithEmptyCollection() {
        MyArrayList<String> emptyCollection = new MyArrayList<>(Collections.emptyList());

        assertEquals(0, emptyCollection.size());
        assertTrue(emptyCollection.isEmpty());
    }

    @Test
    void testAdd() {
        assertTrue(arrayList.add("A"));
        assertEquals(1, arrayList.size());
        assertEquals("A", arrayList.getFirst());
        
        assertTrue(arrayList.add("B"));
        assertEquals(2, arrayList.size());
        assertEquals("B", arrayList.get(1));
    }

    @Test
    void testAddAtIndex() {
        arrayList.add("A");
        arrayList.add("C");
        arrayList.add(1, "B");
        
        assertEquals(3, arrayList.size());
        assertEquals("A", arrayList.get(0));
        assertEquals("B", arrayList.get(1));
        assertEquals("C", arrayList.get(2));
        
        arrayList.add(0, "Start");
        assertEquals("Start", arrayList.get(0));
        assertEquals(4, arrayList.size());
        
        arrayList.add(4, "End");
        assertEquals("End", arrayList.get(4));
        assertEquals(5, arrayList.size());
    }

    @Test
    void testAddAtIndexOutOfBounds() {
        assertThrows(IndexOutOfBoundsException.class, () -> arrayList.add(-1, "X"));
        assertThrows(IndexOutOfBoundsException.class, () -> arrayList.add(1, "X"));
    }

    @Test
    void testAddAll() {
        List<String> toAdd = List.of("A", "B", "C");

        assertTrue(arrayList.addAll(toAdd));
        assertEquals(3, arrayList.size());
        assertEquals("A", arrayList.get(0));
        assertEquals("B", arrayList.get(1));
        assertEquals("C", arrayList.get(2));
        
        assertFalse(arrayList.addAll(Collections.emptyList()));
    }

    @Test
    void testRemoveAtIndex() {
        arrayList.add("A");
        arrayList.add("B");
        arrayList.add("C");
        
        arrayList.remove(1);
        assertEquals(2, arrayList.size());
        assertEquals("A", arrayList.get(0));
        assertEquals("C", arrayList.get(1));
        
        arrayList.removeFirst();
        assertEquals(1, arrayList.size());
        assertEquals("C", arrayList.getFirst());
        
        arrayList.removeFirst();
        assertEquals(0, arrayList.size());
        assertTrue(arrayList.isEmpty());
    }

    @Test
    void testRemoveAtIndexOutOfBounds() {
        assertThrows(NoSuchElementException.class, () -> arrayList.removeFirst());

        arrayList.add("A");
        assertThrows(IndexOutOfBoundsException.class, () -> arrayList.remove(1));
        assertThrows(IndexOutOfBoundsException.class, () -> arrayList.remove(-1));
    }

    @Test
    void testRemoveByObject() {
        arrayList.add("A");
        arrayList.add("B");
        arrayList.add("A");
        
        assertTrue(arrayList.remove("A"));
        assertEquals(2, arrayList.size());
        assertEquals("B", arrayList.get(0));
        assertEquals("A", arrayList.get(1));
        
        assertFalse(arrayList.remove("X"));
    }

    @Test
    void testRemoveAll() {
        arrayList.add("A");
        arrayList.add("B");
        arrayList.add("A");
        arrayList.add("C");
        
        assertTrue(arrayList.removeAll(List.of("A")));
        assertEquals(2, arrayList.size());
        assertFalse(arrayList.contains("A"));
        assertEquals("B", arrayList.get(0));
        assertEquals("C", arrayList.get(1));
    }

    @Test
    void testGet() {
        arrayList.add("A");

        assertEquals("A", arrayList.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> arrayList.get(1));
    }

    @Test
    void testGetFirst() {
        assertThrows(NoSuchElementException.class, () -> arrayList.getFirst());

        arrayList.add("A");
        arrayList.add("B");
        assertEquals("A", arrayList.getFirst());
    }

    @Test
    void testGetLast() {
        assertThrows(NoSuchElementException.class, () -> arrayList.getLast());

        arrayList.add("A");
        arrayList.add("B");
        assertEquals("B", arrayList.getLast());
    }

    @Test
    void testSet() {
        arrayList.add("A");
        arrayList.set(0, "B");

        assertEquals("B", arrayList.getFirst());
        assertThrows(IndexOutOfBoundsException.class, () -> arrayList.set(1, "C"));
    }

    @Test
    void testIsEmpty() {
        assertTrue(arrayList.isEmpty());

        arrayList.add("A");
        assertFalse(arrayList.isEmpty());

        arrayList.clear();
        assertTrue(arrayList.isEmpty());
    }

    @Test
    void testContains() {
        assertFalse(arrayList.contains("A"));

        arrayList.add("A");

        assertTrue(arrayList.contains("A"));
        assertFalse(arrayList.contains("B"));
    }

    @Test
    void testContainsAll() {
        arrayList.add("A");
        arrayList.add("B");

        assertTrue(arrayList.containsAll(List.of("A", "B")));
        assertTrue(arrayList.contains("A"));
        assertFalse(arrayList.containsAll(List.of("A", "C")));
    }

    @Test
    void testClear() {
        arrayList.add("A");
        arrayList.add("B");
        arrayList.clear();

        assertEquals(0, arrayList.size());
        assertTrue(arrayList.isEmpty());
    }

    @Test
    void testToArray() {
        arrayList.add("A");
        arrayList.add("B");

        Object[] arr = arrayList.toArray();

        assertEquals(2, arr.length);
        assertEquals("A", arr[0]);
        assertEquals("B", arr[1]);
    }

    @Test
    void testToArrayGeneric() {
        arrayList.add("A");
        arrayList.add("B");

        String[] strArray = new String[2];
        String[] result = arrayList.toArray(strArray);

        assertSame(strArray, result);
        assertEquals("A", result[0]);
        assertEquals("B", result[1]);

        String[] emptyStrArray = new String[0];
        result = arrayList.toArray(emptyStrArray);

        assertNotSame(emptyStrArray, result);
        assertEquals(2, result.length);
        assertEquals("A", result[0]);
        assertEquals("B", result[1]);
    }

    @Test
    void testRetainAll() {
        arrayList.add("A");
        arrayList.add("B");
        arrayList.add("C");
        
        assertTrue(arrayList.retainAll(List.of("A", "C")));
        assertEquals(2, arrayList.size());
        assertTrue(arrayList.contains("A"));
        assertTrue(arrayList.contains("C"));
        assertFalse(arrayList.contains("B"));
        
        assertFalse(arrayList.retainAll(List.of("A", "C")));
    }

    @Test
    void testIterator() {
        arrayList.add("A");
        arrayList.add("B");
        
        var iterator = arrayList.iterator();

        assertTrue(iterator.hasNext());
        assertEquals("A", iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals("B", iterator.next());
        assertFalse(iterator.hasNext());
        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    void testResize() {
        // Default capacity is 10.
        for (int i = 0; i < 15; i++) {
            arrayList.add("Element " + i);
        }
        assertEquals(15, arrayList.size());
        for (int i = 0; i < 15; i++) {
            assertEquals("Element " + i, arrayList.get(i));
        }
    }
}
