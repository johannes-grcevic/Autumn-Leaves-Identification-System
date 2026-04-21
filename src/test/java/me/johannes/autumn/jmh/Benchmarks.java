package me.johannes.autumn.jmh;

import javafx.geometry.Point2D;

import me.johannes.autumn.model.Bounds;
import me.johannes.autumn.model.MyArrayList;
import me.johannes.autumn.model.MyHashtable;
import me.johannes.autumn.util.BenchmarkUtils;

import org.openjdk.jmh.annotations.*;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
public class Benchmarks {

    private final MyArrayList<Integer> arrayListInt = new MyArrayList<>();
    private final MyHashtable<Integer, Bounds> Hashtable = new MyHashtable<>();
    private final int iterations = 100000;

    @Setup(Level.Invocation)
    public void setup() {
        for (int i = 0; i < iterations; i++) {
            // set up array list with random numbers
            arrayListInt.add(BenchmarkUtils.getRandomNumber(0, Integer.MAX_VALUE));

            // set up hashtable with random keys
            Hashtable.put(BenchmarkUtils.getRandomNumber(0, Integer.MAX_VALUE),
                    new Bounds(new Point2D(0, 0), new Point2D(Double.MAX_VALUE, Double.MAX_VALUE)));
        }
    }

    @TearDown
    public void tearDown() {
        arrayListInt.clear();
        Hashtable.clear();
    }

    @Benchmark
    @Fork(value = 1, warmups = 0)
    @BenchmarkMode(Mode.Throughput)
    public void testArrayListContains() {
        for (int i = 0; i < iterations; i++) {
            int value = BenchmarkUtils.getRandomNumber(0, iterations * 10);

            arrayListInt.contains(value);
        }
    }

    @Benchmark
    @Fork(value = 1, warmups = 0)
    @BenchmarkMode(Mode.Throughput)
    public void testHashtableContainsKey() {
        for (int i = 0; i < iterations; i++) {
            int value = BenchmarkUtils.getRandomNumber(0, iterations * 10);

            Hashtable.containsKey(value);
        }
    }
}
