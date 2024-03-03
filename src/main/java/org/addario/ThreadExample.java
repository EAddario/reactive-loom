package org.addario;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ThreadExample {
    public String getName(List<String> list, int batchSize) throws InterruptedException {
        // Aggregate counts
        ArrayList<CountTask> tasks = new ArrayList<>();
        Map<String, Long> finalCounts = new Hashtable<>();

        for (int i = 0; i < list.size(); i += batchSize) {
            int batchEnd = Math.min((i + batchSize), list.size());
            final List<String> batch = list.subList(i, batchEnd);

            // Split into batches
            System.out.println(STR."\{LocalDateTime.now()}: \{Thread.currentThread().getName()} [virtual=\{Thread.currentThread().isVirtual()}] Preparing batch...");
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
        private final Pattern pattern = Pattern.compile("(?<=first_name=).*?(?=,)");
        private final List<String> batch;
        private final Map<String, Long> finalCounts;

        private CountTask(List<String> batch, Map<String, Long> finalCounts) {
            this.batch = batch;
            this.finalCounts = finalCounts;
        }

        @Override
        public void run() {
            Map<String, Long> localCounts = new Hashtable<>();
            System.out.println(STR."\{LocalDateTime.now()}: \{Thread.currentThread().getName()} [virtual=\{Thread.currentThread().isVirtual()}] Processing batch...");

            for (String name : batch) {
                Matcher matcher = pattern.matcher(name);
                if (matcher.find())
                    localCounts.compute(matcher.group(), (n, c) -> c == null ? 1L : c + 1);
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
