package org.lenskit.bench;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@State(Scope.Benchmark)
public class HashArrayLookups {
    public String[] array = {"foo", "bar", "blatz", "blam", "fishpump"};
    public List<String> list = new ArrayList<>(Arrays.asList(array));
    public Map<String,Integer> map =
            list.stream()
            .collect(Collectors.toMap(Function.identity(), String::length));
    public Map<String,Integer> idMap = new IdentityHashMap<>(map);

    @Param({"foo", "blam", "missing"})
    public String needle;

    @Benchmark
    public void benchListLoc(Blackhole bh) {
        int idx = list.indexOf(needle);
        bh.consume(idx);
    }

    @Benchmark
    public void benchListIdLoc(Blackhole bh) {
        int n = array.length;
        for (int i = 0; i < n; i ++) {
            if (array[i] == needle) {
                bh.consume(i);
                return;
            }
        }
        bh.consume(-1);
    }

    @Benchmark
    public void benchMapLookup(Blackhole bh) {
        bh.consume(map.get(needle));
    }

    @Benchmark
    public void benchIdMapLookup(Blackhole bh) {
        bh.consume(idMap.get(needle));
    }
}
