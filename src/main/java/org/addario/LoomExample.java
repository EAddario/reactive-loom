package org.addario;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.StructuredTaskScope;
import java.util.stream.IntStream;

public class LoomExample {
    private static Callable<Map<String, Long>> prepareBatch(List<String> namesList, int batchStart, int batchSize) {
        return () -> {
            Map<String, Long> localCounts = new ConcurrentHashMap<>();
            int batchEnd = Math.min((batchStart + batchSize), namesList.size());
            System.out.printf("[virtual=%s] Processing batch... \n", Thread.currentThread().isVirtual());

            for (String name : namesList.subList(batchStart, batchEnd)) {
                localCounts.compute(name, (n, c) -> c == null ? 1L : c + 1);
            }

            return localCounts;
        };
    }

    public String getName(int quantity, int batchSize) throws InterruptedException {
        var namesList = new Util().getNames(quantity);
        try (var scope = new BatchScope()) {
            IntStream.iterate(0, batchStart -> batchStart < namesList.size(), batchStart -> batchStart + batchSize)
                    .mapToObj(batchStart -> prepareBatch(namesList, batchStart, batchSize))
                    .forEach(scope::fork);

            scope.join();
            return scope.mostFrequentName();
        }
        catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private static class BatchScope extends StructuredTaskScope<Map<String, Long>> {
        private final ConcurrentHashMap<String, Long> result = new ConcurrentHashMap<>();

        @Override
        protected void handleComplete(Future<Map<String, Long>> future) {
            final var intermediateResult = future.resultNow();
            for (var stringLongEntry : intermediateResult.entrySet()) {
                result.compute(stringLongEntry.getKey(), (n, c) -> updateCount(stringLongEntry.getValue(), c));
            }
        }

        private long updateCount(Long newCount, Long existingCount) {
            return existingCount == null ? newCount : existingCount + newCount;
        }

        public String mostFrequentName() {
            return result.entrySet()
                    .stream()
                    .max(Map.Entry.comparingByValue())
                    .get()
                    .getKey();
        }
    }
}
