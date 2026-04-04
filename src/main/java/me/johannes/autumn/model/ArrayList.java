package me.johannes.autumn.model;

import java.util.*;

@SuppressWarnings("unchecked")
public class ArrayList<T> implements List<T> {

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
            size = 0;
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
    @Override
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

    // adds all elements from the collection at the specified index
    @SuppressWarnings("SuspiciousSystemArraycopy")
    @Override
    public boolean addAll(int index, Collection<? extends T> collection) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        if (collection.isEmpty()) return false;

        Object[] array = collection.toArray();
        int numNew = array.length;

        ensureCapacity(size + numNew);

        int numMoved = size - index;
        if (numMoved > 0) {
            System.arraycopy(data, index, data, index + numNew, numMoved);
        }

        System.arraycopy(array, 0, data, index, numNew);
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
    @Override
    public T remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        T removed = data[index];
        int numMoved = size - index - 1;

        if (numMoved > 0) {
            System.arraycopy(data, index + 1, data, index, numMoved);
        }

        data[--size] = null;

        return removed;
    }

    // removes the first occurrence of the element from the array
    @Override
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
    @Override
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
    @Override
    public T set(int index, T element) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        T old = data[index];
        data[index] = element;

        return old;
    }

    // returns the number of elements in the array
    @Override
    public int size() {
        return size;
    }

    // returns true if the array is empty
    @Override
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
    @Override
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

    // returns the index of the first occurrence of the element, or -1 if not found
    @Override
    public int indexOf(Object object) {
        for (int i = 0; i < size; i++) {
            if (Objects.equals(object, data[i])) {
                return i;
            }
        }
        return -1;
    }

    // returns the index of the last occurrence of the element, or -1 if not found
    @Override
    public int lastIndexOf(Object object) {
        for (int i = size - 1; i >= 0; i--) {
            if (Objects.equals(object, data[i])) {
                return i;
            }
        }
        return -1;
    }

    // returns a list iterator over the elements in proper sequence
    @Override
    public ListIterator<T> listIterator() {
        return listIterator(0);
    }

    // returns a list iterator starting at the specified index
    @Override
    public ListIterator<T> listIterator(int index) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        return new ListIterator<>() {
            private int cursor = index;
            private int lastReturned = -1;

            @Override
            public boolean hasNext() { return cursor < size; }

            @Override
            public T next() {
                if (!hasNext()) throw new NoSuchElementException();
                lastReturned = cursor;
                return data[cursor++];
            }

            @Override
            public boolean hasPrevious() { return cursor > 0; }

            @Override
            public T previous() {
                if (!hasPrevious()) throw new NoSuchElementException();
                lastReturned = --cursor;
                return data[cursor];
            }

            @Override
            public int nextIndex() { return cursor; }

            @Override
            public int previousIndex() { return cursor - 1; }

            @Override
            public void remove() {
                if (lastReturned < 0) throw new IllegalStateException();
                ArrayList.this.remove(lastReturned);
                cursor = lastReturned;
                lastReturned = -1;
            }

            @Override
            public void set(T t) {
                if (lastReturned < 0) throw new IllegalStateException();
                ArrayList.this.set(lastReturned, t);
            }

            @Override
            public void add(T t) {
                ArrayList.this.add(cursor++, t);
                lastReturned = -1;
            }
        };
    }

    // returns a view of the portion of this list between fromIndex (inclusive) and toIndex (exclusive)
    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        if (fromIndex < 0 || toIndex > size || fromIndex > toIndex) {
            throw new IndexOutOfBoundsException("fromIndex: " + fromIndex + ", toIndex: " + toIndex + ", Size: " + size);
        }

        ArrayList<T> subList = new ArrayList<>(toIndex - fromIndex);
        subList.addAll(Arrays.asList(data).subList(fromIndex, toIndex));

        return subList;
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
