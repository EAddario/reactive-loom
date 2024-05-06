package org.addario;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CallableEx {
    public String getName(List<String> list, int batchSize) throws InterruptedException {
        var executorService = Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors(),
                runnable -> {
                    Thread t = new Thread(runnable);
                    t.setDaemon(true);
                    return t;
                }
        );

        // Split names into batches
        Map<String, Long> finalCounts = new ConcurrentHashMap<>();
        List<Callable<Void>> tasks = new ArrayList<>();

        for (int i = 0; i < list.size(); i += batchSize) {
            System.out.println(STR."\{LocalDateTime.now()}: \{Thread.currentThread().getName()} [virtual=\{Thread.currentThread().isVirtual()}] Preparing batch...");
            var batchEnd = Math.min((i + batchSize), list.size());
            final List<String> batch = list.subList(i, batchEnd);
            tasks.add(new CountTask(batch, finalCounts));
        }

        // Wait until tasks are done
        executorService.invokeAll(tasks);

        // Find the max count
        return finalCounts.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .get()
                .getKey();
    }

    private record CountTask(List<String> batch, Map<String, Long> finalCounts) implements Callable<Void> {
        private static final Pattern pattern = Pattern.compile("(?<=first_name=).*?(?=,)");

        @Override
        public Void call() {
            Map<String, Long> localCounts = new HashMap<>();
            System.out.println(STR."\{LocalDateTime.now()}: \{Thread.currentThread().getName()} [virtual=\{Thread.currentThread().isVirtual()}] Processing batch...");

            for (String name : batch) {
                Matcher matcher = pattern.matcher(name);
                if (matcher.find())
                    localCounts.compute(matcher.group(), (_, c) -> c == null ? 1L : c + 1L);
            }

            for (Map.Entry<String, Long> stringLongEntry : localCounts.entrySet()) {
                synchronized (finalCounts) {
                    final Long existingCount = finalCounts.get(stringLongEntry.getKey());
                    final var newCount = stringLongEntry.getValue();

                    if (existingCount == null) {
                        finalCounts.put(stringLongEntry.getKey(), newCount);
                    } else {
                        finalCounts.put(stringLongEntry.getKey(), existingCount + newCount);
                    }

                }
            }

            return null;
        }
    }
}
