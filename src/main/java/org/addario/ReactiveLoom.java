package org.addario;

import reactor.tools.agent.ReactorDebugAgent;

import java.io.File;
import java.util.UUID;

public class ReactiveLoom {
    public static void main(String[] args) throws InterruptedException {
        var quantity = 3_000_000; // Generating 3 million records should take around 20 seconds and will use about 2.6 GB of memory
        var batchSize = 100_000; // quantity / batchSize = how many groups of payments to process in parallel
        var fileName = UUID.randomUUID() + ".txt";
        var file = new File(fileName); // A file with 3 million records will take around 2.5 GB
        System.setProperty("reactor.schedulers.defaultBoundedElasticOnVirtualThreads", "true"); // Use virtual threads (project Loom) in bounded elastic scheduler

        ReactorDebugAgent.init(); // Dev friendly reactive stack traces
        //BlockHound.install(); // Detect and throw on blocking calls from non-blocking threads

        var start = System.currentTimeMillis();
        var paymentsList = TimedExecution.createRecordsList(quantity);
        TimedExecution.createRecordsFile(fileName, paymentsList, file);
        var baseCase = TimedExecution.baseCase(paymentsList);
        var thread = TimedExecution.thread(paymentsList, batchSize);
        var callable = TimedExecution.callable(paymentsList, batchSize);
        var completableFuture = TimedExecution.completableFuture(paymentsList, batchSize);
        var reactive = TimedExecution.reactive(paymentsList, batchSize);
        var loom = TimedExecution.loom(paymentsList, batchSize);
        var ioReactive = TimedExecution.ioReactive(fileName, batchSize);
        var ioLoom = TimedExecution.ioLoom(fileName, batchSize);
        var stop = System.currentTimeMillis();

        if (
                baseCase.equals(thread) &&
                baseCase.equals(callable) &&
                baseCase.equals(completableFuture) &&
                baseCase.equals(reactive) &&
                baseCase.equals(loom) &&
                baseCase.equals(ioReactive) &&
                baseCase.equals(ioLoom)
        )
            System.out.println("\nFinished reactive-loom test with " + String.format("%,d", quantity) + " elements in " + String.format("%,d", (stop - start)) + " ms");
        else
            System.out.println("\nError! Tests returned different names");

        if (file.exists())
            if (file.delete())
                System.exit(0); // Required to terminate Loom virtual threads
            else
                System.out.println("Failed to delete file: " + fileName);

        System.exit(1); // Something went wrong
    }
}
