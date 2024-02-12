package org.addario;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.math.MathFlux;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ReactiveExample {
    public String getName(List<String> namesList, int batchSize) throws InterruptedException {
        var finalCounts = Flux.fromIterable(namesList)
                // Split to batches
                .buffer(batchSize)
                // Aggregate intermediate counts asynchronously
                .flatMap(ReactiveExample::processBatch)
                .reduce(new HashMap<>(), ReactiveExample::mergeIntermediateCount)
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
