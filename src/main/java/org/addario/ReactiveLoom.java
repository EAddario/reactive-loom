package org.addario;

import reactor.tools.agent.ReactorDebugAgent;
import reactor.blockhound.BlockHound;

import java.io.File;
import java.util.UUID;

public class ReactiveLoom {
    public static void main(String[] args) throws InterruptedException {
        var quantity = 2_000_000; // Generating 2 million payment records should take around 10 seconds and will use about 3.9 GB of memory
        var batchSize = 100_000; // quantity / batchSize = how many groups of payments to process in parallel
        var fileName = STR."\{UUID.randomUUID().toString()}.txt"; // Using Java 22 preview features so must be compiled/run with --enable-preview
        var file = new File(fileName); // A file with 2 million records will take around 3.3 GB
        System.setProperty("reactor.schedulers.defaultBoundedElasticOnVirtualThreads", "true");

        ReactorDebugAgent.init(); // Dev friendly reactor stack traces
        BlockHound.install(); // Detect and throw on blocking calls from non-blocking threads

        var paymentsList = TimedExecution.createPaymentsList(quantity);
        TimedExecution.createPaymentsFile(fileName, paymentsList, file);
        TimedExecution.baseCase(paymentsList);
        TimedExecution.thread(paymentsList, batchSize);
        TimedExecution.callable(paymentsList, batchSize);
        TimedExecution.completableFuture(paymentsList, batchSize);
        TimedExecution.reactive(paymentsList, batchSize);
        TimedExecution.loom(paymentsList, batchSize);
        TimedExecution.ioReactive(fileName, batchSize);
        TimedExecution.ioLoom(fileName, batchSize);

        if (file.exists())
            if (file.delete())
                System.exit(0); // Required to terminate Loom virtual threads
            else
                System.out.println(STR."Failed to delete file: \{fileName}");

        System.exit(1); // Something went wrong
    }
}
