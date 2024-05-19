package com.epam.community.middlesvcasync;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * This class contains tests for CompletableFuture behaviours.
 */
class Test_CompletableFutureBehaviours {

    /**
     * This test checks the behaviour of thenApplyAsync method of CompletableFuture.
     * It prints the thread name for each step and asserts that the final result is not null.
     *
     * @throws ExecutionException   if the computation threw an exception
     * @throws InterruptedException if the current thread was interrupted while waiting
     */
    @Test
    void thenApplyAsync_count_threads() throws ExecutionException, InterruptedException {
        System.out.println("T0 Executing in thread: " + Thread.currentThread().getName());
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> 10);

        CompletableFuture<String> resultFuture = future.thenApplyAsync(num -> {
            System.out.println("T1 Executing in thread: " + Thread.currentThread().getName());
            return "Result: " + (num * 2);
        });
        CompletableFuture<String> additionalFeature0 = resultFuture.thenApplyAsync(str -> {
            System.out.println("T2 Executing in thread: " + Thread.currentThread().getName());
            return str + " <-- additional 0";
        });
        CompletableFuture<String> additionalFeature1 = additionalFeature0.thenApply(str -> {
            System.out.println("T3 Executing in thread: " + Thread.currentThread().getName());
            return str + " <-- additional 1";
        });

        String result = additionalFeature1.get();
        System.out.println(result);
        Assertions.assertNotNull(result);
    }

    /**
     * This test checks the behaviour of thenApply method of CompletableFuture.
     * It prints the thread name for each step and asserts that the final result is not null.
     * @throws ExecutionException if the computation threw an exception
     * @throws InterruptedException if the current thread was interrupted while waiting
     */
    @Test
    void thenApply_count_threads() throws ExecutionException, InterruptedException {
        System.out.println("R0 Executing in thread: " + Thread.currentThread().getName());
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> 10);

        CompletableFuture<String> resultFuture = future.thenApply(num -> {
            System.out.println("R1 Executing in thread: " + Thread.currentThread().getName());
            return "Result: " + (num * 2);
        });
        CompletableFuture<String> additionalFeature0 = resultFuture.thenApply(str -> {
            System.out.println("R2 Executing in thread: " + Thread.currentThread().getName());
            return str + " <-- additional 0";
        });
        CompletableFuture<String> additionalFeature1 = additionalFeature0.thenApplyAsync(str -> {
            System.out.println("R3 Executing in thread: " + Thread.currentThread().getName());
            return str + " <-- additional 1";
        });

        String result = additionalFeature1.get();
        System.out.println(result);
        Assertions.assertNotNull(result);
    }
}
