package org.addario;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseCase {
    public String getName(List<String> namesList) {
        // Aggregate counts
        Map<String, Long> counts = new HashMap<>();
        for (String name : namesList) {
            counts.compute(name, (n, c) -> c == null ? 1L : c + 1);
        }

        // Find the max count
        return counts.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .get()
                .getKey();
    }
}
