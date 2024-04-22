package org.addario;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class CompletableFutureExample {
    private static final Pattern pattern = Pattern.compile("(?<=first_name=).*?(?=,)");

    public String getName(List<String> list, int batchSize) {
        // Split into batches
        CompletableFuture<Map<String, Long>> finalCountsFuture =
                IntStream.iterate(0, batchStart -> batchStart < list.size(), batchStart -> batchStart + batchSize)
                        .mapToObj(batchStart -> prepareBatch(list, batchStart, batchSize))
                        .reduce(CompletableFutureExample::combineFeatures)
                        .get();

        // Wait for the result to be computed
        Map<String, Long> finalCounts = finalCountsFuture.join();

        // Find the max count
        return finalCounts.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .get()
                .getKey();
    }

    private static CompletableFuture<Map<String, Long>> prepareBatch(List<String> list, int batchStart, int batchSize) {
        return CompletableFuture.supplyAsync(() -> {
                    Map<String, Long> localCounts = new ConcurrentHashMap<>();
                    var batchEnd = Math.min((batchStart + batchSize), list.size());
                    System.out.println(STR."\{LocalDateTime.now()}: \{Thread.currentThread().getName()} [virtual=\{Thread.currentThread().isVirtual()}] Preparing batch...");

                    for (String name : list.subList(batchStart, batchEnd)) {
                        var matcher = pattern.matcher(name);

                        if (matcher.find())
                            localCounts.compute(matcher.group(), (_, c) -> c == null ? 1L : c + 1L);
                    }

                    return localCounts;
                }
        );
    }

    private static CompletableFuture<Map<String, Long>> combineFeatures(
            CompletableFuture<Map<String, Long>> firstFeature,
            CompletableFuture<Map<String, Long>> secondFeature) {

        return firstFeature.thenCombineAsync(secondFeature, CompletableFutureExample::mergeCounts);
    }

    private static Map<String, Long> mergeCounts(Map<String, Long> stringLongMap, Map<String, Long> stringLongMap2) {
        System.out.println(STR."\{LocalDateTime.now()}: \{Thread.currentThread().getName()} [virtual=\{Thread.currentThread().isVirtual()}] Processing batch...");

        Map<String, Long> accumulator = new HashMap<>(stringLongMap);
        stringLongMap2.forEach((key, value) -> accumulator.compute(key, (_, c) -> c == null ? value : c + value));

        return accumulator;
    }
}
