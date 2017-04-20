package org.lenskit.bench;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import net.java.quickcheck.generator.iterable.Iterables;
import org.lenskit.data.dao.file.StaticDataSource;
import org.lenskit.data.ratings.Rating;
import org.lenskit.data.ratings.Ratings;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.lenskit.util.test.LenskitGenerators.ratings;

/**
 * Created by michaelekstrand on 4/20/2017.
 */
@State(Scope.Benchmark)
public class SortRatings {
    List<Rating> ratings;

    @Setup
    public void createRatings() throws IOException {
        ratings = Lists.newArrayList(Iterables.toIterable(ratings(), 100000));
    }

    @Benchmark
    public void collectionsSort(Blackhole bh) {
        Collections.sort(ratings, Ratings.TIMESTAMP_COMPARATOR);
        bh.consume(ratings);
    }

    @Benchmark
    public void listSort(Blackhole bh) {
        ratings.sort(Ratings.TIMESTAMP_COMPARATOR);
        bh.consume(ratings);
    }

    @Benchmark
    public void streamSort(Blackhole bh) {
        List<Rating> sorted = ratings.stream()
                                     .sorted(Ratings.TIMESTAMP_COMPARATOR)
                                     .collect(Collectors.toList());
        bh.consume(sorted);
    }

    @Benchmark
    public void parallelStreamSort(Blackhole bh) {
        List<Rating> sorted = ratings.parallelStream()
                                     .sorted(Ratings.TIMESTAMP_COMPARATOR)
                                     .collect(Collectors.toList());
        bh.consume(sorted);
    }

    @Benchmark
    public void collectAndSort(Blackhole bh) {
        List<Rating> sorted = ratings.stream()
                                     .collect(Collectors.toList());
        sorted.sort(Ratings.TIMESTAMP_COMPARATOR);
        bh.consume(sorted);
    }

    @Benchmark
    public void copyAndSort(Blackhole bh) {
        List<Rating> sorted = new ArrayList<>(ratings);
        sorted.sort(Ratings.TIMESTAMP_COMPARATOR);
        bh.consume(sorted);
    }

    @Benchmark
    public void sortedCopy(Blackhole bh) {
        List<Rating> sorted = Ordering.from(Ratings.TIMESTAMP_COMPARATOR)
                                      .sortedCopy(ratings);
        bh.consume(sorted);
    }

    @Benchmark
    public void immutableSortedCopy(Blackhole bh) {
        List<Rating> sorted = Ordering.from(Ratings.TIMESTAMP_COMPARATOR)
                                      .immutableSortedCopy(ratings);
        bh.consume(sorted);
    }
}
