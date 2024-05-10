package com.example.syncasync.completableFuture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


public class Main {


    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //CompletableFuture<String> completableFuture = new CompletableFuture<>();
        //System.out.println(completableFuture.get());
        // will never output anything at this stage and we'll wait indefinitely

        /*Manually complete a future*/
        CompletableFuture<String> completableFuture = new CompletableFuture<>();
        completableFuture.complete("Geekific");
        System.out.println(completableFuture.get()); // Geekific

        /*getNow -> returns a default value if the result is not ready
                 -> will not consider the future as completed
        */
        CompletableFuture<String> completableFuture1 = new CompletableFuture<>();
        System.out.println(completableFuture1.getNow("GeekificNow")); // GeekificNow
        //need to manually complete
        completableFuture1.complete("Geekific");
        System.out.println(completableFuture1.get()); // Geekific

        /*Executing Code Asynchronously*/
        /*
            runAsync -> runnable (with no return value)
            supplyAsync -> Supplier (with return value)
        */

        /* runAsync - without retuning anything from the task */
        System.out.println("Main Thread: "+Thread.currentThread().getName());
        CompletableFuture<Void> futureRunASync = CompletableFuture.runAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Future Thread: "+Thread.currentThread().getName());
        });
        futureRunASync.get();
        //output 1st
        // Main Thread: main
        //output after 5 seconds
        // Future Thread: ForkJoinPool.commonPool-worker-1

        /* supplyAsync - return some result from the  background task */
        System.out.println("Main Thread: "+Thread.currentThread().getName());
        CompletableFuture<String> futureSupplyAsync = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return "Future Thread: "+Thread.currentThread().getName();
        });
        System.out.println(futureSupplyAsync.get());
        //Output 1st
        // Main Thread: main
        //Output after 5 seconds
        // Future Thread: ForkJoinPool.commonPool-worker-1

        /* CompletableFuture.get() is blocking as it waits until
        the Future is completed and then returns the result

        we should be able to attach a callback to the CompletableFuture
        which should automatically get called when the Future completes

        made possible by thenApply (Function), thenAccept (Consumer) and thenRun (Runnable)
        */

        /* thenApply - takes in a Function, so it will accept and transforms the result
            of the completable future when it arrives and will produce another result
            which can be further chained to multiple thenApply methods
        */
        CompletableFuture<String> completableFuture2 = CompletableFuture
                .supplyAsync(() -> "Like and ")
                .thenApply(supplyResult -> supplyResult + "Subscribe to ")
                .thenApply(thenApplyResult -> thenApplyResult + "Geekific");
        System.out.println(completableFuture2.get());
        //Like and Subscribe to Geekific

        /* thenAccept (takes in a consumer - will accept the result of the previous future and will return void)
        */
        CompletableFuture.supplyAsync(() -> "Like and ")
                .thenApply(supplyResult -> supplyResult + "Subscribe")
                .thenAccept(thenApplyResult -> System.out.println(thenApplyResult+"!!!"))
                .get();
        //Like and Subscribe!!!

        /* thenRun (takes in a runnable and doesn't even have access to that previous future result)
            - it simply takes in a Runnable and will execute
        */
        CompletableFuture.supplyAsync(() -> "Like and ")
                .thenApply(supplyResult -> supplyResult + "Subscribe")
                .thenRun(() -> System.out.println("Geekific"))
                .get();
        //Geekific

        /* then...Async - to execute on different thread*/
        //execute on same thread
        CompletableFuture.supplyAsync(() -> Thread.currentThread().getName() + " | ")
                .thenApply(supplyResult ->
                        supplyResult + Thread.currentThread().getName() + " | ")
                .thenAccept(thenApplyResult ->
                        System.out.println(thenApplyResult + Thread.currentThread().getName()))
                .get();
        //ForkJoinPool.commonPool-worker-1 | main | main

        //execute on different thread
        CompletableFuture.supplyAsync(() -> Thread.currentThread().getName() + " | ")
                .thenApplyAsync(supplyResult ->
                        supplyResult + Thread.currentThread().getName() + " | ")
                .thenAcceptAsync(thenApplyResult ->
                        System.out.println(thenApplyResult + Thread.currentThread().getName()))
                .get();
        //ForkJoinPool.commonPool-worker-1 |
        //ForkJoinPool.commonPool-worker-1 |
        //ForkJoinPool.commonPool-worker-1
    }
}


