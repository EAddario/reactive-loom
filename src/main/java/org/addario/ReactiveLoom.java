package org.addario;

import java.io.File;
import java.util.UUID;

public class ReactiveLoom {
    public static void main(String[] args) throws InterruptedException {
        var quantity = 2_000_000; // 2 million payment records should take around 10 seconds to generate and will use about 3 GB of memory
        var batchSize = 100_000; // quantity / batchSize = how many groups of payments to process in parallel
        var fileName = STR."\{UUID.randomUUID().toString()}.txt"; // Using Java 21 preview features so must be compiled/run with --enable-preview
        File file = new File(fileName); // 2 million records will generate a file of around 3.3 GB

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
            file.delete();

        System.exit(0); // Required to terminate Loom virtual threads
    }
}
