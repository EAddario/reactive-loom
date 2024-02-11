package org.addario;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Util {
    public List<String> getNames(int quantity) {
        // Generate names
        Random r = new Random();
        var names = List.of("Aran", "Colin", "Ed", "Eddie", "Jimmy", "Joe", "Kel", "Mike", "Peter");

        return IntStream.range(0, quantity)
                .mapToObj(__ -> names.get(r.nextInt(names.size())))
                .collect(Collectors.toList());
    }
}
