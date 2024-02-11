package org.addario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CallableExample {
    public String getName(int quantity, int batchSize) throws InterruptedException {
        var namesList = new Util().getNames(quantity);
        int parallelism = (int) Math.ceil(namesList.size() / (double) batchSize);
        System.out.println("Parallelism is " + parallelism);

        ExecutorService executorService = Executors.newFixedThreadPool(
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

        for (int i = 0; i < namesList.size(); i += batchSize) {
            int batchEnd = Math.min((i + batchSize), namesList.size());
            final List<String> batch = namesList.subList(i, batchEnd);
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

    private static class CountTask implements Callable<Void> {
        private final List<String> batch;
        private final Map<String, Long> finalCounts;

        private CountTask(List<String> batch, Map<String, Long> finalCounts) {
            this.batch = batch;
            this.finalCounts = finalCounts;
        }

        @Override
        public Void call() {
            Map<String, Long> localCounts = new HashMap<>();
            System.out.printf("[%s] Processing batch... \n", Thread.currentThread().getName());

            for (String name : batch) {
                localCounts.compute(name, (n, c) -> c == null ? 1L : c + 1);
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
