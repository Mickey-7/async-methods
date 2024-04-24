package com.example.syncasync;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
public class NestedAsyncService {
    @Async
    public CompletableFuture<String> asyncMethod1(){
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("AsyncMethod1: Start");
            try{
                TimeUnit.SECONDS.sleep(7);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("AsyncMethod1: Calling AsyncMethod2");
            return asyncMethod2().join();
        });
    }

    private CompletableFuture<String> asyncMethod2() {
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("AsyncMethod2: Start");
            try{
                TimeUnit.SECONDS.sleep(7);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("AsyncMethod2: End");
            return "AsyncMethod2 Result";
        });
    }
}
