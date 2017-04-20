package org.lenskit.bench;

import org.lenskit.data.dao.DataAccessObject;
import org.lenskit.data.dao.file.StaticDataSource;
import org.lenskit.data.entities.CommonAttributes;
import org.lenskit.data.entities.CommonTypes;
import org.lenskit.data.entities.Entity;
import org.lenskit.data.ratings.Rating;
import org.lenskit.util.io.ObjectStream;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * Created by michaelekstrand on 4/19/2017.
 */
@State(Scope.Benchmark)
public class ReadCsvFile {
    private StaticDataSource source;

    @Setup
    public void createSource() throws IOException {
        source = StaticDataSource.load(Paths.get("ml-latest-small.yml"));
    }

    @Benchmark
    public void benchLoad(Blackhole bh) {
        bh.consume(source.get());
    }

    @Benchmark
    public void benchAverageRatingViews(Blackhole bh) {
        double sum = 0;
        int n = 0;
        DataAccessObject dao = source.get();
        ObjectStream<Rating> stream = dao.query(Rating.class)
                                         .stream();
        for (Rating r: stream) {
            sum += r.getValue();
            n += 1;
        }

        bh.consume(sum / n);
    }

    @Benchmark
    public void benchAverageRatingEntities(Blackhole bh) {
        double sum = 0;
        int n = 0;
        DataAccessObject dao = source.get();
        ObjectStream<Entity> stream = dao.query(CommonTypes.RATING)
                                         .stream();
        for (Entity r: stream) {
            sum += r.getDouble(CommonAttributes.RATING);
            n += 1;
        }

        bh.consume(sum / n);
    }
}
