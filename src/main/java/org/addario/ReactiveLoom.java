package org.addario;

import java.util.concurrent.ExecutionException;

public class ReactiveLoom {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        var quantity = 1_000_000;
        var batchSize = 10_000;

        var baseCase = new BaseCase();
        System.out.println("The most frequent baseCase name is " + baseCase.getName(quantity));
        System.out.println("-----------------------------------------------------------------------------------------");

        var threadExample = new ThreadExample();
        System.out.println("The most frequent threadExample name is " + threadExample.getName(quantity, batchSize));
        System.out.println("-----------------------------------------------------------------------------------------");

        var callableExample = new CallableExample();
        System.out.println("The most frequent callableExample name is " + callableExample.getName(quantity, batchSize));
        System.out.println("-----------------------------------------------------------------------------------------");

        var completableFutureExample = new CompletableFutureExample();
        System.out.println("The most frequent completableFutureExample name is " + completableFutureExample.getName(quantity, batchSize));
        System.out.println("-----------------------------------------------------------------------------------------");
    }
}
