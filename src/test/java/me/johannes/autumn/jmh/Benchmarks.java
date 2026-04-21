package me.johannes.autumn.jmh;

import javafx.geometry.Point2D;

import me.johannes.autumn.model.Bounds;
import me.johannes.autumn.model.MyArrayList;
import me.johannes.autumn.model.MyHashtable;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class Benchmarks {
    private MyArrayList<Integer> arrayListInt;
    private MyHashtable<Integer, Bounds> hashtableBounds;

    private int[] prebuiltKeys;
    private int[] missingKeys;
    private static final int SIZE  = 100000;
    private static final Bounds DEFAULT_POINT2D =
            new Bounds(new Point2D(0, 0),
            new Point2D(Double.MAX_VALUE, Double.MAX_VALUE));

    @Setup(Level.Invocation)
    public void setup() {
        arrayListInt = new MyArrayList<>();
        hashtableBounds = new MyHashtable<>();

        prebuiltKeys = new int[SIZE];
        missingKeys  = new int[SIZE];

        for (int i = 0; i < SIZE; i++) {
            prebuiltKeys[i] = i;
            missingKeys[i] = SIZE + i;

            arrayListInt.add(i);
            hashtableBounds.put(i, DEFAULT_POINT2D);
        }
    }

    @TearDown(Level.Invocation)
    public void tearDown() {
        arrayListInt.clear();
        hashtableBounds.clear();
    }

    // =========================================================================
    // MyArrayList benchmarks
    // =========================================================================
    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void arrayList_add() {
        MyArrayList<Integer> arrayList = new MyArrayList<>();

        for (int i = 0; i < SIZE; i++) {
            arrayList.add(i);
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void arrayList_addAtFront() {
        MyArrayList<Integer> arrayList = new MyArrayList<>();

        for (int i = 0; i < 1000; i++) {
            arrayList.add(0, i);
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public int arrayList_get() {
        int sum = 0;

        for (int key : prebuiltKeys) {
            sum += arrayListInt.get(key % SIZE);
        }

        return sum;
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void arrayList_containsHit() {
        for (int key : prebuiltKeys) {
            arrayListInt.contains(key);
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void arrayList_containsMiss() {
        for (int key : missingKeys) {
            arrayListInt.contains(key);
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void arrayList_removeFromEnd() {
        MyArrayList<Integer> list = new MyArrayList<>(arrayListInt);

        if (list.size() > 0) {
            list.subList(0, list.size()).clear();
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void arrayList_removeFromFront() {
        MyArrayList<Integer> list = new MyArrayList<>(arrayListInt);

        while (!list.isEmpty()) {
            list.remove(0);
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void arrayList_indexOf() {
        for (int key : prebuiltKeys) {
            arrayListInt.indexOf(key);
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void arrayList_lastIndexOf() {
        for (int key : prebuiltKeys) {
            arrayListInt.lastIndexOf(key);
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public int arrayList_iterate() {
        int sum = 0;

        for (int value : arrayListInt) {
            sum += value;
        }

        return sum;
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void arrayList_set() {
        for (int i = 0; i < SIZE; i++) {
            arrayListInt.set(i, i * 2);
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public int arrayList_getFirstAndLast() {
        return arrayListInt.getFirst() + arrayListInt.getLast();
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public int arrayList_subList() {
        return arrayListInt.subList(0, SIZE / 2).size();
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public Object[] arrayList_toArray() {
        return arrayListInt.toArray();
    }

    // =========================================================================
    // MyHashtable benchmarks
    // =========================================================================
    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void hashtable_put() {
        MyHashtable<Integer, Bounds> hashtable = new MyHashtable<>();

        for (int i = 0; i < SIZE; i++) {
            hashtable.put(i, DEFAULT_POINT2D);
        }
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void hashtable_putUpdate() {
        for (int key : prebuiltKeys) {
            hashtableBounds.put(key, DEFAULT_POINT2D);
        }
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void hashtable_getHit() {
        for (int key : prebuiltKeys) {
            hashtableBounds.get(key);
        }
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void hashtable_getMiss() {
        for (int key : missingKeys) {
            hashtableBounds.get(key);
        }
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void hashtable_containsKeyHit() {
        for (int key : prebuiltKeys) {
            hashtableBounds.containsKey(key);
        }
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void hashtable_containsKeyMiss() {
        for (int key : missingKeys) {
            hashtableBounds.containsKey(key);
        }
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void hashtable_remove() {
        MyHashtable<Integer, Bounds> hashtable = new MyHashtable<>();

        for (int i = 0; i < SIZE; i++) {
            hashtable.put(i, DEFAULT_POINT2D);
        }

        for (int i = 0; i < SIZE; i++) {
            hashtable.remove(i);
        }
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public void hashtable_mixed() {
        MyHashtable<Integer, Bounds> hashtable = new MyHashtable<>();

        for (int i = 0; i < SIZE; i++) {
            int remainder = i % 10;

            if (remainder < 5) {
                hashtable.put(i, DEFAULT_POINT2D);
            }
            else {
                if (remainder < 8) {
                    hashtable.get(i % (i + 1));
                }
                else {
                    hashtable.remove(i % (i + 1));
                }
            }
        }
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public int hashtable_iterate() {
        int count = 0;

        for (MyHashtable.Entry<Integer, Bounds> entry : hashtableBounds) {
            count++;
        }

        return count;
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public int hashtable_keys() {
        return hashtableBounds.keys().size();
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public int hashtable_values() {
        return hashtableBounds.values().size();
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public int hashtable_entries() {
        return hashtableBounds.entries().size();
    }

    @Benchmark @BenchmarkMode(Mode.Throughput)
    public int hashtable_size() {
        return hashtableBounds.size();
    }
}