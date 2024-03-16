package org.addario;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.StructuredTaskScope;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IoLoomExample {
    private static final Pattern pattern = Pattern.compile("(?<=first_name=).*?(?=,)");

    public String getName(String fileName, int batchSize) {
        try (var batchScope = new BatchScope()) {
            try (var lines = Files.lines(Paths.get(fileName))) {
                final var iterator = lines.iterator();
                try (var fileReadingScope = new StructuredTaskScope.ShutdownOnFailure()) {
                    while (iterator.hasNext()) {
                        final var batchFuture = fileReadingScope.fork(() -> prepareBatch(batchSize, iterator));
                        fileReadingScope.join();
                        batchScope.fork(prepareBatchProcessing(batchFuture.get()));
                    }
                }
            }
            batchScope.join();
            return batchScope.mostFrequentName();
        } catch (Exception e) {
            return STR."Error: \{e.getMessage()}";
        }
    }

    private static ArrayList<String> prepareBatch(int batchSize, Iterator<String> iterator) {
        System.out.println(STR."\{LocalDateTime.now()}: \{Thread.currentThread().getName()} [virtual=\{Thread.currentThread().isVirtual()}] Preparing batch...");
        ArrayList<String> batch = new ArrayList<>(batchSize);
        while (iterator.hasNext() && batch.size() < batchSize) {
            batch.add(iterator.next());
        }
        return batch;
    }

    private static Callable<Map<String, Long>> prepareBatchProcessing(List<String> batch) {
        return () -> {
            Map<String, Long> localCounts = new ConcurrentHashMap<>();
            System.out.println(STR."\{LocalDateTime.now()}: \{Thread.currentThread().getName()} [virtual=\{Thread.currentThread().isVirtual()}] Processing batch...");
            for (String name : batch) {
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
