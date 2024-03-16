package org.addario;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BaseCase {
    private final static Pattern pattern = Pattern.compile("(?<=first_name=).*?(?=,)");

    public String getName(List<String> list) {
        // Aggregate counts
        System.out.println(STR."\{LocalDateTime.now()}: \{Thread.currentThread().getName()} [virtual=\{Thread.currentThread().isVirtual()}] Processing batch...");
        Map<String, Long> counts = new HashMap<>();
        for (String name : list) {
            Matcher matcher = pattern.matcher(name);
            if (matcher.find())
                counts.compute(matcher.group(), (_, c) -> c == null ? 1L : c + 1);
        }

        // Find the max count
        return counts.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .get()
                .getKey();
    }
}
