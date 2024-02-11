package org.addario;

import java.util.concurrent.ExecutionException;

public class ReactiveLoom {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        var quantity = 10_000_000;
        var batchSize = 100_000;
        var start = 0L;
        var stop = 0L;

        var baseCase = new BaseCase();
        start = System.currentTimeMillis();
        System.out.println("The most frequent baseCase name is " + baseCase.getName(quantity));
        stop = System.currentTimeMillis();
        System.out.println("Time: " + (stop - start) + "ms");
        System.out.println("-----------------------------------------------------------------------------------------");

        var threadExample = new ThreadExample();
        start = System.currentTimeMillis();
        System.out.println("The most frequent threadExample name is " + threadExample.getName(quantity, batchSize));
        stop = System.currentTimeMillis();
        System.out.println("Time: " + (stop - start) + "ms");
        System.out.println("-----------------------------------------------------------------------------------------");

        var callableExample = new CallableExample();
        start = System.currentTimeMillis();
        System.out.println("The most frequent callableExample name is " + callableExample.getName(quantity, batchSize));
        stop = System.currentTimeMillis();
        System.out.println("Time: " + (stop - start) + "ms");
        System.out.println("-----------------------------------------------------------------------------------------");

        var completableFutureExample = new CompletableFutureExample();
        start = System.currentTimeMillis();
        System.out.println("The most frequent completableFutureExample name is " + completableFutureExample.getName(quantity, batchSize));
        stop = System.currentTimeMillis();
        System.out.println("Time: " + (stop - start) + "ms");
        System.out.println("-----------------------------------------------------------------------------------------");

        var reactiveExample = new ReactiveExample();
        start = System.currentTimeMillis();
        System.out.println("The most frequent reactiveExample name is " + reactiveExample.getName(quantity, batchSize));
        stop = System.currentTimeMillis();
        System.out.println("Time: " + (stop - start) + "ms");
        System.out.println("-----------------------------------------------------------------------------------------");
    }
}
