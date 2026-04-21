package me.johannes.autumn.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class MyHashtableTest {

    private MyHashtable<String, Integer> hashtable;

    @BeforeEach
    void setUp() {
        hashtable = new MyHashtable<>();
    }

    @AfterEach
    void tearDown() {
        hashtable = null;
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
        hashtable.put("One", 1);
        hashtable.put("Two", 2);
        hashtable.put("Three", 3);

        assertEquals(3, hashtable.size());
        assertEquals(1, hashtable.get("One"));
        assertEquals(2, hashtable.get("Two"));
        assertEquals(3, hashtable.get("Three"));
    }

    @Test
    void testPutUpdate() {
        hashtable.put("Key", 100);
        assertEquals(1, hashtable.size());
        assertEquals(100, hashtable.get("Key"));

        hashtable.put("Key", 200);
        assertEquals(1, hashtable.size());
        assertEquals(200, hashtable.get("Key"));
    }

    @Test
    void testPutNullKey() {
        assertThrows(IllegalArgumentException.class, () -> hashtable.put(null, 1));
    }

    @Test
    void testGetNullKey() {
        assertThrows(NullPointerException.class, () -> hashtable.get(null));
    }

    @Test
    void testGetNonExistent() {
        assertNull(hashtable.get("Missing"));
    }

    @Test
    void testRemove() {
        hashtable.put("A", 1);
        hashtable.put("B", 2);
        hashtable.put("C", 3);

        hashtable.remove("B");
        assertEquals(2, hashtable.size());
        assertNull(hashtable.get("B"));
        assertTrue(hashtable.containsKey("A"));
        assertTrue(hashtable.containsKey("C"));
        assertFalse(hashtable.containsKey("B"));

        // Removing non-existent key should not change size or throw error
        hashtable.remove("X");
        assertEquals(2, hashtable.size());
    }

    @Test
    void testRemoveNullKey() {
        assertThrows(NullPointerException.class, () -> hashtable.remove(null));
    }

    @Test
    void testSizeAndIsEmpty() {
        assertTrue(hashtable.isEmpty());
        assertEquals(0, hashtable.size());

        hashtable.put("A", 1);
        assertFalse(hashtable.isEmpty());
        assertEquals(1, hashtable.size());

        hashtable.remove("A");
        assertTrue(hashtable.isEmpty());
        assertEquals(0, hashtable.size());
    }

    @Test
    void testContainsKey() {
        assertFalse(hashtable.containsKey("A"));

        hashtable.put("A", 1);
        assertTrue(hashtable.containsKey("A"));

        hashtable.remove("A");
        assertFalse(hashtable.containsKey("A"));
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
                if (!(obj instanceof MockKey key)) return false;

                return name.equals(key.name);
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
        for (int i = 0; i < 20; i++) {
            hashtable.put("Key" + i, i);
        }

        assertEquals(20, hashtable.size());
        for (int i = 0; i < 20; i++) {
            assertEquals(i, hashtable.get("Key" + i));
        }
    }

    @Test
    void testTombstoneRehash() {
        // rehash if tombstones > size
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
        smallTable.put("A", 1); // Index 1
        smallTable.put("E", 5); // Index 2 (probed)

        smallTable.remove("A"); // Index 1 becomes tombstone
        smallTable.put("F", 6); // Should reuse tombstone at Index 1 if hash matches, or it's first deleted

        // use a key that hashes to 1
        smallTable.put("I", 9); // should reuse index 1.

        assertEquals(9, smallTable.get("I"));
        assertEquals(5, smallTable.get("E"));
    }

    @Test
    void testIterator() {
        hashtable.put("A", 1);
        hashtable.put("B", 2);
        hashtable.put("C", 3);
        hashtable.remove("B");

        Iterator<MyHashtable.Entry<String, Integer>> iterator = hashtable.iterator();
        assertTrue(iterator.hasNext());
        MyHashtable.Entry<String, Integer> e1 = iterator.next();
        assertTrue(e1.getKey().equals("A") || e1.getKey().equals("C"));
        
        assertTrue(iterator.hasNext());
        MyHashtable.Entry<String, Integer> e2 = iterator.next();
        assertTrue(e2.getKey().equals("A") || e2.getKey().equals("C"));
        assertNotEquals(e1.getKey(), e2.getKey());

        assertFalse(iterator.hasNext());
        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    void testEntryGetters() {
        MyHashtable.Entry<String, String> entry = new MyHashtable.Entry<>("Key", "Value");

        assertEquals("Key", entry.getKey());
        assertEquals("Value", entry.getValue());
    }
}
