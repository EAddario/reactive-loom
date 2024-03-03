package org.addario;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.math.MathFlux;
import reactor.util.function.Tuples;
import reactor.util.function.Tuple2;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class IoReactiveExample {
    private static final Scheduler IO = Schedulers.newParallel("IO");

    public String getName(String fileName, int batchSize) throws IOException {
        var finalCounts = Flux.fromStream(Files.lines(Paths.get(fileName)))
                // Split to batches
                .buffer(batchSize)
                .subscribeOn(IO)
                //.doOnNext(__ -> System.out.println(STR."[\{Thread.currentThread().getName()}] Processing batch..."))
                // Aggregate intermediate counts asynchronously
                .flatMap(IoReactiveExample::processBatch)
                .reduce(new HashMap<>(), IoReactiveExample::mergeIntermediateCount)
                .flatMapIterable(HashMap::entrySet);

        return MathFlux.max(finalCounts, Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .block();
    }

    private static HashMap<String, Long> mergeIntermediateCount(HashMap<String, Long> acc, Map<String, Long> intermediateResult) {
        intermediateResult.forEach((name, intermediateCount) -> acc.merge(name, intermediateCount, Long::sum));
        return acc;
    }

    private static Mono<Map<String, Long>> processBatch(List<String> batch) {
        return Flux.fromIterable(batch)
                .groupBy(Function.identity())
                .flatMap(group -> group.count().map(count -> Tuples.of(group.key(), count)))
                .collectMap(Tuple2::getT1, Tuple2::getT2)
                //.doOnSubscribe(__ -> System.out.println(STR."[\{Thread.currentThread().getName()}] Processing batch..."))
                .subscribeOn(Schedulers.boundedElastic());
    }
}
