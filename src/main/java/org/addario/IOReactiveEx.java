package org.addario;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.math.MathFlux;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IOReactiveEx {
    private static final Pattern pattern = Pattern.compile("(?<=first_name=).*?(?=,)");

    public String getName(String fileName, int batchSize) throws IOException {
        var finalCounts = Flux.fromStream(Files.lines(Paths.get(fileName)))
                // Split to batches
                .buffer(batchSize)
                .parallel()
                .runOn(Schedulers.parallel())
                .doOnNext(_ -> System.out.println(LocalDateTime.now() + ": " + Thread.currentThread().getName() + " [virtual=" + Thread.currentThread().isVirtual() + "] Preparing batch..."))
                // Aggregate intermediate counts asynchronously
                .flatMap(IOReactiveEx::processBatch)
                .sequential()
                .reduce(new HashMap<>(), IOReactiveEx::mergeIntermediateCount)
                .flatMapIterable(HashMap::entrySet);

        return MathFlux.max(finalCounts, Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .block();
    }

    private static Mono<Map<String, Long>> processBatch(List<String> batch) {
        return Flux.fromIterable(batch)
                .map(pattern::matcher)
                .filter(Matcher::find)
                .map(Matcher::group)
                .groupBy(Function.identity())
                .flatMap(group -> group.count().map(count -> Tuples.of(group.key(), count)))
                .collectMap(Tuple2::getT1, Tuple2::getT2)
                .doOnSubscribe(_ -> System.out.println(LocalDateTime.now() + ": " + Thread.currentThread().getName() + " [virtual=" + Thread.currentThread().isVirtual() + "] Processing batch..."))
                .subscribeOn(Schedulers.boundedElastic());
    }

    private static HashMap<String, Long> mergeIntermediateCount(HashMap<String, Long> totalCount, Map<String, Long> intermediateResult) {
        intermediateResult.forEach((name, intermediateCount) -> totalCount.merge(name, intermediateCount, Long::sum));

        return totalCount;
    }
}
