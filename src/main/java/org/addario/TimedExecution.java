package org.addario;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TimedExecution {
    private static long stop;
    private static long start;

    static List<String> createRecordsList(int quantity) {
        System.out.println("------------------------------------------------------------------------------------------- Create records array");
        System.out.println(LocalDateTime.now() + ": Executing createRecordsList on " + Thread.currentThread().getName() + " thread [virtual=" + Thread.currentThread().isVirtual() + "]");
        start = System.currentTimeMillis();
        var paymentsList = new ArrayList<String>(quantity); // Better performance than IntStream.range(0, quantity).mapToObj(_ -> new RecordEx().toString()).toList()

        for (int i = 0; i < quantity; i++)
            paymentsList.add(new RecordEx().toString());

        stop = System.currentTimeMillis();
        System.out.println("Records list with " + String.format("%,d", quantity) + " elements created in " + String.format("%,d", (stop - start)) + " ms");

        return paymentsList;
    }

    static void createRecordsFile(String fileName, List<String> paymentsList, File file) {
        System.out.println("------------------------------------------------------------------------------------------- Create records file");
        System.out.println(LocalDateTime.now() + ": Executing createRecordsFile on " + Thread.currentThread().getName() + " thread [virtual=" + Thread.currentThread().isVirtual() + "]");
        BufferedWriter bufferedWriter;
        start = System.currentTimeMillis();

        try {
            bufferedWriter = new BufferedWriter(new FileWriter(fileName, false));

            for (String payment : paymentsList)
                bufferedWriter.write(payment + System.lineSeparator());

            bufferedWriter.close();
        } catch (IOException e) {
            System.out.println("Error writing to file " + fileName + ": " + e.getMessage());

            if (file.exists())
                if (file.delete())
                    System.out.println("File " + fileName + " deleted successfully");
                else
                    System.out.println("Failed to delete file " + fileName);

            System.exit(1);
        }

        stop = System.currentTimeMillis();
        System.out.println("Records file size " + String.format("%,d", (file.length() / 1024^2)) + " MB created in " + String.format("%,d", (stop - start)) + " ms");
    }

    static String baseCase(List<String> paymentsList) {
        System.out.println("------------------------------------------------------------------------------------------- Base Case");
        System.out.println(LocalDateTime.now() + ": Executing baseCase.getName on " + Thread.currentThread().getName() + " thread [virtual=" + Thread.currentThread().isVirtual() + "]");
        var baseCase = new BaseCase();
        start = System.currentTimeMillis();
        var name = baseCase.getName(paymentsList);
        System.out.print("The most frequent base case name is " + name);
        stop = System.currentTimeMillis();
        System.out.println(", calculated in " + String.format("%,d", (stop - start)) + " ms");

        return name;
    }

    static String thread(List<String> paymentsList, int batchSize) throws InterruptedException {
        System.out.println("------------------------------------------------------------------------------------------- Thread Example");
        System.out.println(LocalDateTime.now() + ": Executing threadEx.getName on " + Thread.currentThread().getName() + " thread [virtual=" + Thread.currentThread().isVirtual() + "]");
        var threadExample = new ThreadEx();
        start = System.currentTimeMillis();
        var name = threadExample.getName(paymentsList, batchSize);
        System.out.print("The most frequent Thread example name is " + name);
        stop = System.currentTimeMillis();
        System.out.println(", calculated in " + String.format("%,d", (stop - start)) + " ms");

        return name;
    }

    static String callable(List<String> paymentsList, int batchSize) throws InterruptedException {
        System.out.println("------------------------------------------------------------------------------------------- Callable Example");
        System.out.println(LocalDateTime.now() + ": Executing callableEx.getName on " + Thread.currentThread().getName() + " thread [virtual=" + Thread.currentThread().isVirtual() + "]");
        var callableExample = new CallableEx();
        start = System.currentTimeMillis();
        var name = callableExample.getName(paymentsList, batchSize);
        System.out.print("The most frequent Callable example name is " + name);
        stop = System.currentTimeMillis();
        System.out.println(", calculated in " + String.format("%,d", (stop - start)) + " ms");

        return name;
    }

    static String completableFuture(List<String> paymentsList, int batchSize) {
        System.out.println("------------------------------------------------------------------------------------------- CompletableFuture Example");
        System.out.println(LocalDateTime.now() + ": Executing completableFutureEx.getName on " + Thread.currentThread().getName() + " thread [virtual=" + Thread.currentThread().isVirtual() + "]");
        var completableFutureExample = new CompletableFutureEx();
        start = System.currentTimeMillis();
        var name = completableFutureExample.getName(paymentsList, batchSize);
        System.out.print("The most frequent CompletableFuture example name is " + name);
        stop = System.currentTimeMillis();
        System.out.println(", calculated in " + String.format("%,d", (stop - start)) + " ms");

        return name;
    }

    static String reactive(List<String> paymentsList, int batchSize) throws InterruptedException {
        System.out.println("------------------------------------------------------------------------------------------- Reactive Example");
        System.out.println(LocalDateTime.now() + ": Executing reactiveEx.getName on " + Thread.currentThread().getName() + " thread [virtual=" + Thread.currentThread().isVirtual() + "]");
        var reactiveExample = new ReactiveEx();
        start = System.currentTimeMillis();
        var name = reactiveExample.getName(paymentsList, batchSize);
        System.out.print("The most frequent Reactive example name is " + name);
        stop = System.currentTimeMillis();
        System.out.println(", calculated in " + String.format("%,d", (stop - start)) + " ms");

        return name;
    }

    static String loom(List<String> paymentsList, int batchSize) {
        System.out.println("------------------------------------------------------------------------------------------- Project Loom Example");
        System.out.println(LocalDateTime.now() + ": Executing loomEx.getName on " + Thread.currentThread().getName() + " thread [virtual=" + Thread.currentThread().isVirtual() + "]");
        var loomExample = new LoomEx();
        start = System.currentTimeMillis();
        var name = loomExample.getName(paymentsList, batchSize);
        System.out.print("The most frequent Virtual Threads example name is " + name);
        stop = System.currentTimeMillis();
        System.out.println(", calculated in " + String.format("%,d", (stop - start)) + " ms");

        return name;
    }

    static String ioReactive(String fileName, int batchSize) {
        System.out.println("------------------------------------------------------------------------------------------- IO Reactive Example");
        System.out.println(LocalDateTime.now() + ": Executing ioReactiveEx.getName on " + Thread.currentThread().getName() + " thread [virtual=" + Thread.currentThread().isVirtual() + "]");
        var ioReactiveExample = new IOReactiveEx();
        start = System.currentTimeMillis();
        String name;

        try {
            name = ioReactiveExample.getName(fileName, batchSize);
            System.out.print("The most frequent IO Reactive example name is " + name);
        } catch (IOException e) {
            name = "Error executing ioReactiveEx";
            System.out.println("Error executing ioReactiveEx: " + e.getMessage());
        }

        stop = System.currentTimeMillis();
        System.out.println(", calculated in " + String.format("%,d", (stop - start)) + " ms");

        return name;
    }

    static String ioLoom(String fileName, int batchSize) {
        System.out.println("------------------------------------------------------------------------------------------- IO Project Loom Example");
        System.out.println(LocalDateTime.now() + ": Executing ioLoomEx.getName on " + Thread.currentThread().getName() + " thread [virtual=" + Thread.currentThread().isVirtual() + "]");
        var ioLoomExample = new IOLoomEx();
        start = System.currentTimeMillis();
        var name = ioLoomExample.getName(fileName, batchSize);
        System.out.print("The most frequent IO Virtual Threads example name is " + name);
        stop = System.currentTimeMillis();
        System.out.println(", calculated in " + String.format("%,d", (stop - start)) + " ms");

        return name;
    }
}
