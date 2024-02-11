package org.addario;

import java.util.concurrent.ExecutionException;

public class ReactiveLoom {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        var baseCase = new BaseCase().getName(1_000_000);
        System.out.println("The most frequent baseCase name is " + baseCase);

        var threadExample = new ThreadExample().getName(1_000_000, 10_000);
        System.out.println("The most frequent threadExample name is " + threadExample);

        var callableExample = new CallableExample().getName(1_000_000, 10_000);
        System.out.println("The most frequent callableExample name is " + callableExample);
    }
}
