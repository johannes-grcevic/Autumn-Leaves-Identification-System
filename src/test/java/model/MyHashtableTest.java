package model;

import me.johannes.autumn.model.MyHashtable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class MyHashtableTest {

    private MyHashtable<String, Integer> table;

    @BeforeEach
    void setUp() {
        table = new MyHashtable<>();
    }

    @Test
    void testConstructorDefault() {
        MyHashtable<String, Integer> defaultTable = new MyHashtable<>();

        assertEquals(0, defaultTable.size());
        assertTrue(defaultTable.isEmpty());
    }

    @Test
    void testConstructorCustomCapacity() {
        MyHashtable<String, Integer> customTable = new MyHashtable<>(10);

        assertEquals(0, customTable.size());
        assertTrue(customTable.isEmpty());
    }

    @Test
    void testConstructorInvalidCapacity() {
        assertThrows(IllegalArgumentException.class, () -> new MyHashtable<>(0));
        assertThrows(IllegalArgumentException.class, () -> new MyHashtable<>(-1));
    }

    @Test
    void testPutAndGet() {
        table.put("One", 1);
        table.put("Two", 2);
        table.put("Three", 3);

        assertEquals(3, table.size());
        assertEquals(1, table.get("One"));
        assertEquals(2, table.get("Two"));
        assertEquals(3, table.get("Three"));
    }

    @Test
    void testPutUpdate() {
        table.put("Key", 100);
        assertEquals(1, table.size());
        assertEquals(100, table.get("Key"));

        table.put("Key", 200);
        assertEquals(1, table.size());
        assertEquals(200, table.get("Key"));
    }

    @Test
    void testPutNullKey() {
        assertThrows(IllegalArgumentException.class, () -> table.put(null, 1));
    }

    @Test
    void testGetNullKey() {
        assertThrows(NullPointerException.class, () -> table.get(null));
    }

    @Test
    void testGetNonExistent() {
        assertNull(table.get("Missing"));
    }

    @Test
    void testRemove() {
        table.put("A", 1);
        table.put("B", 2);
        table.put("C", 3);

        table.remove("B");
        assertEquals(2, table.size());
        assertNull(table.get("B"));
        assertTrue(table.containsKey("A"));
        assertTrue(table.containsKey("C"));
        assertFalse(table.containsKey("B"));

        // Removing non-existent key should not change size or throw error
        table.remove("X");
        assertEquals(2, table.size());
    }

    @Test
    void testRemoveNullKey() {
        assertThrows(NullPointerException.class, () -> table.remove(null));
    }

    @Test
    void testSizeAndIsEmpty() {
        assertTrue(table.isEmpty());
        assertEquals(0, table.size());

        table.put("A", 1);
        assertFalse(table.isEmpty());
        assertEquals(1, table.size());

        table.remove("A");
        assertTrue(table.isEmpty());
        assertEquals(0, table.size());
    }

    @Test
    void testContainsKey() {
        assertFalse(table.containsKey("A"));

        table.put("A", 1);
        assertTrue(table.containsKey("A"));

        table.remove("A");
        assertFalse(table.containsKey("A"));
    }

    @Test
    void testCollisionHandling() {
        // Force collision by using keys that hash to the same bucket
        // If we have capacity 16, hashes are index = hash % 16

        record MockKey(String name, int hash) {
            @Override
            public int hashCode() {
                return hash;
            }

            @Override
            public boolean equals(Object obj) {
                if (!(obj instanceof MockKey)) return false;

                return name.equals(((MockKey) obj).name);
            }
        }

        MyHashtable<MockKey, Integer> collisionTable = new MyHashtable<>(16);
        MockKey k1 = new MockKey("k1", 1);
        MockKey k2 = new MockKey("k2", 1); // Collision with k1
        MockKey k3 = new MockKey("k3", 17); // Collision with k1 (17 % 16 = 1)

        collisionTable.put(k1, 10);
        collisionTable.put(k2, 20);
        collisionTable.put(k3, 30);

        assertEquals(3, collisionTable.size());
        assertEquals(10, collisionTable.get(k1));
        assertEquals(20, collisionTable.get(k2));
        assertEquals(30, collisionTable.get(k3));

        collisionTable.remove(k2);
        assertNull(collisionTable.get(k2));
        assertEquals(30, collisionTable.get(k3)); // Probe past tombstone
    }

    @Test
    void testResize() {
        // Default capacity 16, load factor 0.75. 16 * 0.75 = 12.
        // It should resize on the 13th element or so (depending on implementation detail of when the check happens)
        for (int i = 0; i < 20; i++) {
            table.put("Key" + i, i);
        }

        assertEquals(20, table.size());
        for (int i = 0; i < 20; i++) {
            assertEquals(i, table.get("Key" + i));
        }
    }

    @Test
    void testTombstoneRehash() {
        // Implementation rehashes if tombstones > size
        MyHashtable<String, Integer> smallTable = new MyHashtable<>(4);

        smallTable.put("A", 1);
        smallTable.put("B", 2);
        smallTable.remove("A");
        smallTable.remove("B");

        // size = 0, tombstones = 2. tombstones > size, should rehash on next put
        smallTable.put("C", 3);
        assertEquals(1, smallTable.size());
        assertEquals(3, smallTable.get("C"));
    }

    @Test
    void testTombstoneReuse() {
        MyHashtable<String, Integer> smallTable = new MyHashtable<>(4);
        // Keys that collide
        // String "A" hash is 65, "E" is 69. 65%4 = 1, 69%4 = 1.
        smallTable.put("A", 1); // Index 1
        smallTable.put("E", 5); // Index 2 (probed)
        
        smallTable.remove("A"); // Index 1 becomes tombstone
        smallTable.put("F", 6); // Should reuse tombstone at Index 1 if hash matches, or it's first deleted
        // "F" hash is 70. 70%4 = 2. Index 2 is "E". Next is 3.
        
        // use a key that hashes to 1. "A" hashes to 65.
        smallTable.put("I", 9); // "I" is 73. 73%4 = 1. Should reuse index 1.
        
        assertEquals(9, smallTable.get("I"));
        assertEquals(5, smallTable.get("E"));
    }

    @Test
    void testIterator() {
        table.put("A", 1);
        table.put("B", 2);
        table.put("C", 3);
        table.remove("B");

        Iterator<MyHashtable.Entry<String, Integer>> it = table.iterator();
        assertTrue(it.hasNext());
        MyHashtable.Entry<String, Integer> e1 = it.next();
        assertTrue(e1.getKey().equals("A") || e1.getKey().equals("C"));
        
        assertTrue(it.hasNext());
        MyHashtable.Entry<String, Integer> e2 = it.next();
        assertTrue(e2.getKey().equals("A") || e2.getKey().equals("C"));
        assertNotEquals(e1.getKey(), e2.getKey());

        assertFalse(it.hasNext());
        assertThrows(NoSuchElementException.class, it::next);
    }

    @Test
    void testEntryGetters() {
        MyHashtable.Entry<String, String> entry = new MyHashtable.Entry<>("Key", "Value");

        assertEquals("Key", entry.getKey());
        assertEquals("Value", entry.getValue());
    }
}
