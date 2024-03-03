package org.addario;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ReactiveLoom {
    public static void main(String[] args) throws InterruptedException {
        var quantity = 5_000_000;
        var batchSize = 100_000;
        var fileName = STR."\{UUID.randomUUID().toString()}.txt";
        File file = new File(fileName);
        var start = 0L;
        var stop = 0L;
        Random r = new Random();

        var names = List.of("Ahmad", "Ankush", "Chiamaka", "Daniel", "Ed", "Faris", "Guilherme", "Ian",
                "Illia", "Jędrzej", "Jonathan", "Leo", "Mark", "Mic", "Nicola", "Sudi", "Tania");

        var namesList = IntStream.range(0, quantity).mapToObj(_ -> names.get(r.nextInt(names.size()))).collect(Collectors.toList());

        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(fileName, false));

            for (String name : namesList)
                bufferedWriter.write(name + System.lineSeparator());

            bufferedWriter.close();
        } catch (IOException e) {
            System.out.println(STR."Error writing to file: \{e.getMessage()}");
        }

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
        System.out.println("-----------------------------------------------------------------------------------------");

        var ioReactiveExample = new IoReactiveExample();
        start = System.currentTimeMillis();
        try {
            System.out.println(STR."The most frequent ioReactiveExample name is \{ioReactiveExample.getName(fileName, batchSize)}");
        } catch (IOException e) {
            System.out.println(STR."Error executing ioReactiveExample: \{e.getMessage()}");
        }
        stop = System.currentTimeMillis();
        System.out.println(STR."Time: \{stop - start}ms");
        System.out.println("-----------------------------------------------------------------------------------------");

        var ioLoomExample = new IoLoomExample();
        start = System.currentTimeMillis();
        System.out.println(STR."The most frequent ioLoomExample name is \{ioLoomExample.getName(fileName, batchSize)}");
        stop = System.currentTimeMillis();
        System.out.println(STR."Time: \{stop - start}ms");

        if (file.exists())
            file.delete();

        System.exit(0);
    }
}
