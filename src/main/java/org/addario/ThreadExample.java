package org.addario;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class ThreadExample {
    public String getName(int quantity, int batchSize) throws InterruptedException {
        var namesList = new Util().getNames(quantity);

        // Aggregate counts
        ArrayList<CountTask> tasks = new ArrayList<>();
        Map<String, Long> finalCounts = new Hashtable<>();

        for (int i = 0; i < namesList.size(); i += batchSize) {
            int batchEnd = Math.min((i + batchSize), namesList.size());
            final List<String> batch = namesList.subList(i, batchEnd);

            // Split into batches
            final CountTask task = new CountTask(batch, finalCounts);
            tasks.add(task);
            task.setDaemon(true);
            task.start();
        }

        // Wait until the threads finished
        for (Thread thread : tasks) {
            thread.join();
        }

        // Find the max count
        return finalCounts.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .get()
                .getKey();
    }

    private static class CountTask extends Thread {
        private final List<String> batch;
        private final Map<String, Long> finalCounts;

        private CountTask(List<String> batch, Map<String, Long> finalCounts) {
            this.batch = batch;
            this.finalCounts = finalCounts;
        }

        @Override
        public void run() {
            Map<String, Long> localCounts = new Hashtable<>();
            System.out.printf("[%s] Processing batch...\n", Thread.currentThread().getName());

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
        }
    }
}
