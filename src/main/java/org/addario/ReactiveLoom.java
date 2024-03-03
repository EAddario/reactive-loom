package org.addario;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;
import java.util.stream.IntStream;

public class ReactiveLoom {
    public static void main(String[] args) throws InterruptedException {
        var quantity = 100_000;
        var batchSize = 10_000;
        var fileName = STR."\{UUID.randomUUID().toString()}.txt";
        File file = new File(fileName);
        var start = 0L;
        var stop = 0L;
        var msg = "";

        System.out.println("---------------------------------------------------------------------- paymentsList array");
        start = System.currentTimeMillis();
        var paymentsList = IntStream.range(0, quantity).mapToObj(_ -> new Payment().toString()).toList();
        stop = System.currentTimeMillis();
        msg = STR."paymentsList array with \{String.format("%,d", quantity)} elements created in \{String.format("%,d", (stop - start))} ms";
        System.out.println(msg);

        System.out.println("----------------------------------------------------------------------- paymentsList file");
        BufferedWriter bufferedWriter = null;
        start = System.currentTimeMillis();
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(fileName, false));

            for (String payment : paymentsList)
                bufferedWriter.write(payment + System.lineSeparator());

            bufferedWriter.close();
        } catch (IOException e) {
            System.out.println(STR."Error writing to file \{fileName}: \{e.getMessage()}");
            if (file.exists())
                file.delete();
            System.exit(1);
        }
        stop = System.currentTimeMillis();
        msg = STR."paymentsList file size \{String.format("%,d", (file.length() / 1024^2))} MB created in \{String.format("%,d", (stop - start))} ms";
        System.out.println(msg);

        System.out.println("-------------------------------------------------------------------------------- baseCase");
        var baseCase = new BaseCase();
        start = System.currentTimeMillis();
        System.out.print(STR."The most frequent baseCase name is \{baseCase.getName(paymentsList)}");
        stop = System.currentTimeMillis();
        System.out.println(STR.", calculated in \{String.format("%,d", (stop - start))} ms");

        System.out.println("--------------------------------------------------------------------------- threadExample");
        var threadExample = new ThreadExample();
        start = System.currentTimeMillis();
        System.out.print(STR."The most frequent threadExample name is \{threadExample.getName(paymentsList, batchSize)}");
        stop = System.currentTimeMillis();
        System.out.println(STR.", calculated in \{String.format("%,d", (stop - start))} ms");

        System.out.println("------------------------------------------------------------------------- callableExample");
        var callableExample = new CallableExample();
        start = System.currentTimeMillis();
        System.out.print(STR."The most frequent callableExample name is \{callableExample.getName(paymentsList, batchSize)}");
        stop = System.currentTimeMillis();
        System.out.println(STR.", calculated in \{String.format("%,d", (stop - start))} ms");

        System.out.println("---------------------------------------------------------------- completableFutureExample");
        var completableFutureExample = new CompletableFutureExample();
        start = System.currentTimeMillis();
        System.out.print(STR."The most frequent completableFutureExample name is \{completableFutureExample.getName(paymentsList, batchSize)}");
        stop = System.currentTimeMillis();
        System.out.println(STR.", calculated in \{String.format("%,d", (stop - start))} ms");

        System.out.println("------------------------------------------------------------------------- reactiveExample");
        var reactiveExample = new ReactiveExample();
        start = System.currentTimeMillis();
        System.out.print(STR."The most frequent reactiveExample name is \{reactiveExample.getName(paymentsList, batchSize)}");
        stop = System.currentTimeMillis();
        System.out.println(STR.", calculated in \{String.format("%,d", (stop - start))} ms");

        System.out.println("----------------------------------------------------------------------------- loomExample");
        var loomExample = new LoomExample();
        start = System.currentTimeMillis();
        System.out.print(STR."The most frequent loomExample name is \{loomExample.getName(paymentsList, batchSize)}");
        stop = System.currentTimeMillis();
        System.out.println(STR.", calculated in \{String.format("%,d", (stop - start))} ms");

        System.out.println("----------------------------------------------------------------------- ioReactiveExample");
        var ioReactiveExample = new IoReactiveExample();
        start = System.currentTimeMillis();
        try {
            System.out.print(STR."The most frequent ioReactiveExample name is \{ioReactiveExample.getName(fileName, batchSize)}");
        } catch (IOException e) {
            System.out.println(STR."Error executing ioReactiveExample: \{e.getMessage()}");
        }
        stop = System.currentTimeMillis();
        System.out.println(STR.", calculated in \{String.format("%,d", (stop - start))} ms");

        System.out.println("--------------------------------------------------------------------------- ioLoomExample");
        var ioLoomExample = new IoLoomExample();
        start = System.currentTimeMillis();
        System.out.print(STR."The most frequent ioLoomExample name is \{ioLoomExample.getName(fileName, batchSize)}");
        stop = System.currentTimeMillis();
        System.out.println(STR.", calculated in \{String.format("%,d", (stop - start))} ms");

        if (file.exists())
            file.delete();

        System.exit(0);
    }
}
