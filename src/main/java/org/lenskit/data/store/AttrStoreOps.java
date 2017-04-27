package org.lenskit.data.store;

import net.java.quickcheck.Generator;
import net.java.quickcheck.generator.iterable.Iterables;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import static net.java.quickcheck.generator.PrimitiveGenerators.integers;
import static net.java.quickcheck.generator.PrimitiveGenerators.longs;
import static net.java.quickcheck.generator.PrimitiveGenerators.strings;

/**
 * Created by michaelekstrand on 4/26/2017.
 */
@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
public class AttrStoreOps {
    private AttrStore store;
    private AttrStore longStore;
    private int size, longSize;
    private Random random = new Random();

    @Setup
    public void createStore() {
        AttrStoreBuilder asb = new AttrStoreBuilder();

        Generator<Integer> shardCounts = integers(2, 5);
        Generator<Integer> extraCounts = integers(10, 500);

        int count = Shard.SHARD_SIZE * shardCounts.next() + extraCounts.next();
        for (String str: Iterables.toIterable(strings(), count)) {
            asb.add(str);
        }
        store = asb.build();
        size = store.size();
    }

    @Setup
    public void createLongStore() {
        AttrStoreBuilder asb = new LongAttrStoreBuilder();

        Generator<Integer> shardCounts = integers(2, 5);
        Generator<Integer> extraCounts = integers(10, 500);

        int count = Shard.SHARD_SIZE * shardCounts.next() + extraCounts.next();
        for (Long num: Iterables.toIterable(longs(0, Integer.MAX_VALUE - 10), count)) {
            asb.add(num);
        }
        longStore = asb.build();
        longSize = longStore.size();
    }

    @Benchmark
    public void get(Blackhole bh) {
        int i = random.nextInt(size);
        bh.consume(store.get(i));
    }

    @Benchmark
    public void longGet(Blackhole bh) {
        int i = random.nextInt(longSize);
        bh.consume(longStore.get(i));
    }
}
