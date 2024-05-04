package com.epam.community.middlesvcasync;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

class Test_CompletableFutureBehaviours {

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
