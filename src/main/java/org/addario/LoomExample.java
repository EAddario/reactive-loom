package org.addario;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.StructuredTaskScope;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class LoomExample {
    private static final Pattern pattern = Pattern.compile("(?<=first_name=).*?(?=,)");

    public String getName(List<String> list, int batchSize) {
        try (var scope = new BatchScope()) {
            IntStream.iterate(0, batchStart -> batchStart < list.size(), batchStart -> batchStart + batchSize)
                    .mapToObj(batchStart -> prepareBatch(list, batchStart, batchSize))
                    .forEach(scope::fork);

            scope.join();
            return scope.mostFrequentName();
        } catch (Exception e) {
            return STR."Error: \{e.getMessage()}";
        }
    }

    private static Callable<Map<String, Long>> prepareBatch(List<String> list, int batchStart, int batchSize) {
        return () -> {
            Map<String, Long> localCounts = new ConcurrentHashMap<>();
            int batchEnd = Math.min((batchStart + batchSize), list.size());
            System.out.println(STR."\{LocalDateTime.now()}: \{Thread.currentThread().getName()} [virtual=\{Thread.currentThread().isVirtual()}] Processing batch...");

            for (String name : list.subList(batchStart, batchEnd)) {
                Matcher matcher = pattern.matcher(name);
                if (matcher.find())
                    localCounts.compute(matcher.group(), (_, c) -> c == null ? 1L : c + 1);
            }

            return localCounts;
        };
    }

    private static class BatchScope extends StructuredTaskScope<Map<String, Long>> {
        private final ConcurrentHashMap<String, Long> result = new ConcurrentHashMap<>();

        @Override
        protected void handleComplete(Subtask<? extends Map<String, Long>> subtask) {
            Map<String, Long> intermediateResult = new HashMap<>();

            switch (subtask.state()) {
                case UNAVAILABLE -> System.out.println("Error: Subtask is unavailable");
                case SUCCESS -> intermediateResult = subtask.get();
                case FAILED -> System.out.println(STR."Error: \{subtask.exception().getMessage()}");
            }

            for (var stringLongEntry : intermediateResult.entrySet()) {
                result.compute(stringLongEntry.getKey(), (_, c) -> updateCount(stringLongEntry.getValue(), c));
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
