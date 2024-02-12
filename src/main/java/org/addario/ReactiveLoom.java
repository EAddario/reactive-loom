package org.addario;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ReactiveLoom {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        var quantity = 100_000_000;
        var batchSize = 100_000;
        var start = 0L;
        var stop = 0L;
        Random r = new Random();
        var names = List.of("Ahmad", "Ankush", "Chiamaka", "Daniel", "Ed", "Faris", "Guilherme", "Ian",
                "Illia", "Jędrzej", "Jonathan", "Leo", "Mark", "Mic", "Nicola", "Sudi", "Tania");

        var namesList = IntStream.range(0, quantity).mapToObj(__ -> names.get(r.nextInt(names.size())))
                .collect(Collectors.toList());

        var baseCase = new BaseCase();
        start = System.currentTimeMillis();
        System.out.println(STR."The most frequent baseCase name is \{baseCase.getName(namesList)}");
        stop = System.currentTimeMillis();
        System.out.println(STR."Time: \{stop - start}ms");
        System.out.println("-----------------------------------------------------------------------------------------");

        var threadExample = new ThreadExample();
        start = System.currentTimeMillis();
        System.out.println(STR."The most frequent threadExample name is \{threadExample.getName(namesList, batchSize)}");
        stop = System.currentTimeMillis();
        System.out.println(STR."Time: \{stop - start}ms");
        System.out.println("-----------------------------------------------------------------------------------------");

        var callableExample = new CallableExample();
        start = System.currentTimeMillis();
        System.out.println(STR."The most frequent callableExample name is \{callableExample.getName(namesList, batchSize)}");
        stop = System.currentTimeMillis();
        System.out.println(STR."Time: \{stop - start}ms");
        System.out.println("-----------------------------------------------------------------------------------------");

        var completableFutureExample = new CompletableFutureExample();
        start = System.currentTimeMillis();
        System.out.println(STR."The most frequent completableFutureExample name is \{completableFutureExample.getName(namesList, batchSize)}");
        stop = System.currentTimeMillis();
        System.out.println(STR."Time: \{stop - start}ms");
        System.out.println("-----------------------------------------------------------------------------------------");

        var reactiveExample = new ReactiveExample();
        start = System.currentTimeMillis();
        System.out.println(STR."The most frequent reactiveExample name is \{reactiveExample.getName(namesList, batchSize)}");
        stop = System.currentTimeMillis();
        System.out.println(STR."Time: \{stop - start}ms");
        System.out.println("-----------------------------------------------------------------------------------------");

        var loomExample = new LoomExample();
        start = System.currentTimeMillis();
        System.out.println(STR."The most frequent loomExample name is \{loomExample.getName(namesList, batchSize)}");
        stop = System.currentTimeMillis();
        System.out.println(STR."Time: \{stop - start}ms");
    }
}
