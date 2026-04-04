package me.johannes.autumn.model;

import java.util.*;

@SuppressWarnings("unchecked")
public class MyHashtable<K, V> implements Iterable<MyHashtable.Entry<K, V>> {
    // entry is a container that holds a key and value
    public static class Entry<K, V> {
        private final K key;
        private V value;
        private boolean deleted;

        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
            this.deleted = false;
        }

        public K getKey() { return key; }
        public V getValue() { return value; }
    }

    private Entry<K, V>[] table;
    private int size;
    private int capacity;
    private int tombstones;

    private static final double DEFAULT_LOAD_FACTOR = 0.75;

    public MyHashtable(int initialCapacity) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("Initial capacity must be greater than 0.");
        }

        this.capacity = initialCapacity;
        this.table = (Entry<K, V>[]) new Entry[initialCapacity];
        this.size = 0;
        this.tombstones = 0;
    }

    public MyHashtable() {
        this(16);
    }

    // returns the index of an existing key, or -1 if not found
    private int findKeyIndex(K key) {
        if (key == null) {
            throw new NullPointerException("Key cannot be null.");
        }

        int index = Math.floorMod(key.hashCode(), capacity);

        for (int i = 0; i < capacity; i++) {
            if (table[index] == null) {
                return -1;
            }

            if (!table[index].deleted && table[index].key.equals(key)) {
                return index;
            }

            index = (index + 1) % capacity;
        }

        return -1;
    }

    // returns the best slot for inserting a new key
    // prefers a tombstone, otherwise uses the first empty slot
    private int findInsertIndex(K key) {
        if (key == null) {
            throw new NullPointerException("Key cannot be null.");
        }

        int index = Math.floorMod(key.hashCode(), capacity);
        int firstDeleted = -1;

        for (int i = 0; i < capacity; i++) {
            if (table[index] == null) {
                return (firstDeleted != -1) ? firstDeleted : index;
            }

            if (!table[index].deleted && table[index].key.equals(key)) {
                return index;
            }

            if (table[index].deleted && firstDeleted == -1) {
                firstDeleted = index;
            }

            index = (index + 1) % capacity;
        }

        // No empty slot was found.
        // If we saw a tombstone, reuse it; otherwise signal failure.
        return firstDeleted;
    }

    // makes the table bigger when it gets too full,
    // so we just double the size and reinsert it all
    private void resize() {
        rehash(capacity * 2);
    }

    // rebuilds the table and removes tombstones without changing capacity
    private void rehash() {
        rehash(capacity);
    }

    // rebuild helper used by both resize and tombstone cleanup
    private void rehash(int newCapacity) {
        Entry<K, V>[] oldTable = table;
        capacity = newCapacity;
        table = (Entry<K, V>[]) new Entry[capacity];
        size = 0;
        tombstones = 0;

        for (Entry<K, V> entry : oldTable) {
            if (entry != null && !entry.deleted) {
                int index = findInsertIndex(entry.key);
                table[index] = entry;
                size++;
            }
        }
    }

    // puts a key-value pair into the table
    public void put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null.");
        }

        // resize if active entries are too many
        if ((double) (size + tombstones) / capacity >= DEFAULT_LOAD_FACTOR) {
            resize();
        } else if (tombstones > size) {
            // clean up tombstones if they start piling up
            rehash();
        }

        int existingIndex = findKeyIndex(key);

        if (existingIndex != -1) {
            table[existingIndex].value = value;
            return;
        }

        int insertIndex = findInsertIndex(key);

        if (insertIndex == -1) {
            resize();
            insertIndex = findInsertIndex(key);
        }

        table[insertIndex] = new Entry<>(key, value);
        size++;
    }

    // gets the value attached to a key
    public V get(K key) {
        int index = findKeyIndex(key);

        if (index == -1) {
            return null;
        }

        return table[index].value;
    }

    // marks an entry as deleted so we can probe past it (tombstone)
    // we can't delete it completely, or it'll break the search chain
    public void remove(K key) {
        int index = findKeyIndex(key);

        if (index == -1) {
            return;
        }

        table[index].deleted = true;
        size--;
        tombstones++;
    }

    // returns the number of entries in the table
    public int size() {
        return size;
    }

    // checks if the table is empty
    public boolean isEmpty() {
        return size == 0;
    }

    // checks if a key is in the table
    public boolean containsKey(K key) {
        return findKeyIndex(key) != -1;
    }

    public Collection<K> keys() {
        Collection<K> keys = Collections.synchronizedCollection(new MyArrayList<>());

        for (Entry<K, V> entry : table) {
            if (entry != null) {
                keys.add(entry.key);
            }
        }

        return keys;
    }

    public Collection<V> values() {
        Collection<V> values = Collections.synchronizedCollection(new MyArrayList<>());

        for (Entry<K, V> entry : table) {
            if (entry != null) {
                values.add(entry.value);
            }
        }

        return values;
    }

    // removes all entries from the table
    public void clear() {
        for (int i = 0; i < capacity; i++) {
            table[i] = null;
        }
        size = 0;
        tombstones = 0;
    }

    @Override
    public Iterator<Entry<K, V>> iterator() {
        return new Iterator<>() {
            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                // Find the next non-null and non-deleted entry
                while (currentIndex < capacity) {
                    if (table[currentIndex] != null && !table[currentIndex].deleted) {
                        return true;
                    }
                    currentIndex++;
                }
                return false;
            }

            @Override
            public Entry<K, V> next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return table[currentIndex++];
            }
        };
    }
}
