package org.addario;

import reactor.tools.agent.ReactorDebugAgent;

import java.io.File;
import java.util.UUID;

public class ReactiveLoom {
    public static void main(String[] args) throws InterruptedException {
        var quantity = 3_000_000; // Generating 3 million records should take around 20 seconds and will use about 2.6 GB of memory
        var batchSize = 100_000; // quantity / batchSize = how many groups of payments to process in parallel
        var fileName = STR."\{UUID.randomUUID().toString()}.txt"; // Using Java 22 preview features so must be compiled/run with --enable-preview
        var file = new File(fileName); // A file with 3 million records will take around 2.5 GB
        System.setProperty("reactor.schedulers.defaultBoundedElasticOnVirtualThreads", "true"); // Use virtual threads (project Loom) in bounded elastic scheduler

        ReactorDebugAgent.init(); // Dev friendly reactive stack traces
        //BlockHound.install(); // Detect and throw on blocking calls from non-blocking threads

        var start = System.currentTimeMillis();
        var paymentsList = TimedExecution.createRecordsList(quantity);
        TimedExecution.createRecordsFile(fileName, paymentsList, file);
        TimedExecution.baseCase(paymentsList);
        TimedExecution.thread(paymentsList, batchSize);
        TimedExecution.callable(paymentsList, batchSize);
        TimedExecution.completableFuture(paymentsList, batchSize);
        TimedExecution.reactive(paymentsList, batchSize);
        TimedExecution.loom(paymentsList, batchSize);
        TimedExecution.ioReactive(fileName, batchSize);
        TimedExecution.ioLoom(fileName, batchSize);
        var stop = System.currentTimeMillis();
        System.out.println(STR."\nFinished reactive-loom test with \{String.format("%,d", quantity)} elements in \{String.format("%,d", (stop - start))} ms");

        if (file.exists())
            if (file.delete())
                System.exit(0); // Required to terminate Loom virtual threads
            else
                System.out.println(STR."Failed to delete file: \{fileName}");

        System.exit(1); // Something went wrong
    }
}
