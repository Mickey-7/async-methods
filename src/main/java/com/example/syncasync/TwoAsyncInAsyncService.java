package com.example.syncasync;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
public class TwoAsyncInAsyncService {

    @Async
    public CompletableFuture<String> asyncMethod1(){
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("AsyncMethod1: Start");
            try {
                // Simulate a delay
                TimeUnit.SECONDS.sleep(7);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("AsyncMethod1: Calling AsyncMethod2 and AsyncMethod3");
            CompletableFuture<String> async2Result = asyncMethod2();
            CompletableFuture<String> async3Result = asyncMethod3();
            // Wait for both asyncMethod2 and asyncMethod3 to complete
            return async2Result.join() + ", " + async3Result.join();
        });
    }

    @Async
    private CompletableFuture<String> asyncMethod2() {
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("AsyncMethod2: Start");
            try {
                // Simulate a delay
                TimeUnit.SECONDS.sleep(7);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("AsyncMethod2: End");
            return "AsyncMethod2 Result";
        });
    }

    @Async
    private CompletableFuture<String> asyncMethod3() {
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("AsyncMethod3: Start");
            // Simulate a delay
            try {
                TimeUnit.SECONDS.sleep(4);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("AsyncMethod3: End");
            return "AsyncMethod3 Result";
        });
    }


}
