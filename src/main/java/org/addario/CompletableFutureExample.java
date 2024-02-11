package org.addario;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

public class CompletableFutureExample {
    private static CompletableFuture<Map<String, Long>> combineFeatures(
            CompletableFuture<Map<String, Long>> firstFeature,
            CompletableFuture<Map<String, Long>> secondFeature) {
        return firstFeature.thenCombineAsync(secondFeature, CompletableFutureExample::mergeCounts);
    }

    private static Map<String, Long> mergeCounts(Map<String, Long> stringLongMap, Map<String, Long> stringLongMap2) {
        System.out.printf("[%s] Merging counts... \n", Thread.currentThread().getName());

        Map<String, Long> accumulator = new HashMap<>(stringLongMap);
        stringLongMap2.forEach((key, value) -> accumulator.compute(key, (n, c) -> c == null ? value : c + value));
        return accumulator;
    }

    private static CompletableFuture<Map<String, Long>> prepareBatch(List<String> namesList, int batchStart, int batchSize) {
        return CompletableFuture.supplyAsync(() -> {
            Map<String, Long> localCounts = new ConcurrentHashMap<>();
            int batchEnd = Math.min((batchStart + batchSize), namesList.size());
            System.out.printf("[%s] Processing batch... \n", Thread.currentThread().getName());

            for (String name : namesList.subList(batchStart, batchEnd)) {
                localCounts.compute(name, (n, c) -> c == null ? 1L : c + 1);
            }

            return localCounts;
        });
    }

    public String getName(int quantity, int batchSize) throws InterruptedException {
        var namesList = new Util().getNames(quantity);

        // Split into batches
        CompletableFuture<Map<String, Long>> finalCountsFuture =
                IntStream.iterate(0, batchStart -> batchStart < namesList.size(), batchStart -> batchStart + batchSize)
                        .mapToObj(batchStart -> prepareBatch(namesList, batchStart, batchSize))
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
}
