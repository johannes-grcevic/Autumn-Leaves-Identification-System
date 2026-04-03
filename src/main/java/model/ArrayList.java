package model;

import java.util.*;

@SuppressWarnings("unchecked")
public class ArrayList<T> implements Iterable<T>, Collection<T> {
    private T[] data;
    private int size;

    private static final int INITIAL_CAPACITY = 10;

    public ArrayList(int initialCapacity) {
        data = (T[]) new Object[initialCapacity];
        size = 0;
    }

    public ArrayList() {
        this(INITIAL_CAPACITY);
    }

    public ArrayList(Collection<? extends T> collection) {
        Object[] array = collection.toArray();
        size = array.length;

        if (size != 0) {
            data = (T[]) Arrays.copyOf(array, size, Object[].class);
        }
        else {
            // replace it with an empty array of initial capacity
            data = (T[]) new Object[INITIAL_CAPACITY];
        }
    }

    // adds an element to the array
    @Override
    public boolean add(T element) {
        if (size == data.length) {
            resize();
        }
        data[size] = element;
        size++;

        return true;
    }

    // adds an array at a specified index
    public void add(int index, T element) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        if (size == data.length) {
            resize();
        }

        if (index < size) {
            // Shift all elements one to the right starting from the last element before the one being added.
            System.arraycopy(data, index, data, index + 1, size - index);
        }
        data[index] = element;
        size++;
    }

    // adds all elements from the collection to the array
    @SuppressWarnings("SuspiciousSystemArraycopy")
    @Override
    public boolean addAll(Collection<? extends T> collection) {
        if (collection.isEmpty()) return false;

        Object[] array = collection.toArray();
        int numNew = array.length;
        ensureCapacity(size + numNew);
        System.arraycopy(array, 0, data, size, numNew);
        size += numNew;
        return true;
    }

    // ensures that the array has at least the specified capacity
    private void ensureCapacity(int minCapacity) {
        if (minCapacity > data.length) {
            int oldCapacity = data.length;
            int newCapacity = oldCapacity + (oldCapacity >> 1);

            if (newCapacity < minCapacity) {
                newCapacity = minCapacity;
            }

            if (newCapacity < INITIAL_CAPACITY) {
                newCapacity = INITIAL_CAPACITY;
            }
            data = Arrays.copyOf(data, newCapacity);
        }
    }

    // removes the element at the specified index
    public void remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        int numMoved = size - index - 1;
        if (numMoved > 0) {
            System.arraycopy(data, index + 1, data, index, numMoved);
        }
        data[--size] = null;
    }

    // removes the first occurrence of the element from the array
    public boolean remove(Object object) {
        for (int i = 0; i < size; i++) {
            if (Objects.equals(object, data[i])) {
                remove(i);
                return true;
            }
        }
        return false;
    }

    // removes all elements from the array that are in the collection
    @Override
    public boolean removeAll(Collection<?> collection) {
        boolean modified = false;

        for (int i = 0; i < size; i++) {
            if (collection.contains(data[i])) {
                remove(i);
                i--;
                modified = true;
            }
        }
        return modified;
    }

    // returns the element at the specified index
    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        return data[index];
    }

    // returns the first element in the array
    public T getFirst() {
        if (size == 0) {
            throw new NoSuchElementException();
        }
        else {
            return get(0);
        }
    }

    // returns the last element in the array
    public T getLast() {
        int last = size - 1;

        if (last < 0) {
            throw new NoSuchElementException();
        }
        else {
            return get(last);
        }
    }

    // sets the element at the specified index
    public void set(int index, T element) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        data[index] = element;
    }

    // returns the number of elements in the array
    public int size() {
        return size;
    }

    // returns true if the array is empty
    public boolean isEmpty() {
        return size == 0;
    }

    // returns true if the array contains the element
    @Override
    public boolean contains(Object object) {
        for (int i = 0; i < size; i++) {
            if (Objects.equals(object, data[i])) {
                return true;
            }
        }
        return false;
    }

    // returns true if the array contains all the elements in the collection
    @Override
    public boolean containsAll(Collection<?> collection) {
        for (Object element : collection) {
            if (!contains(element)) {
                return false;
            }
        }
        return true;
    }

    // removes all elements from the array
    public void clear() {
        Arrays.fill(data, null);
        size = 0;
    }

    // doubles the size of the array
    private void resize() {
        // create a new array that's double the size of the old one
        // copy everything from the old array into the new one
        ensureCapacity(data.length == 0 ? INITIAL_CAPACITY : data.length * 2);
    }

    // returns an array containing all the elements
    @SuppressWarnings("SuspiciousSystemArraycopy")
    @Override
    public <E> E[] toArray(E[] array) {
        if (array.length < size) {
            // If array is too small, allocate array new one of the same runtime type
            return (E[]) Arrays.copyOf(data, size, array.getClass());
        }

        System.arraycopy(data, 0, array, 0, size);
        if (array.length > size) {
            array[size] = null;
        }
        return array;
    }

    // returns an array containing all the elements
    @Override
    public Object[] toArray() {
        return Arrays.copyOf(data, size);
    }

    // removes all elements from the array that are not in the collection
    @Override
    public boolean retainAll(Collection<?> collection) {
        boolean modified = false;

        for (int i = 0; i < size; i++) {
            if (!collection.contains(data[i])) {
                remove(i);
                i--;
                modified = true;
            }
        }
        return modified;
    }

    // returns an iterator over the elements in the array
    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < size;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return data[currentIndex++];
            }
        };
    }
}
