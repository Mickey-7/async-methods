package com.example.syncasync;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
public class AsyncController {

    @Autowired
    public MyService myService;

    @Autowired
    public NestedAsyncService nestedAsyncService;

    @Autowired
    public TwoAsyncInAsyncService twoAsyncInAsyncService;

    @GetMapping("/sync")
    public ResponseEntity<String> syncApi(){
        return new ResponseEntity<>("Synchronous API response", HttpStatus.OK);
    }

    @GetMapping("/asyncTry")
    public ResponseEntity<String> asyncApiTry(){
        return myService.asyncMethod()
                .thenApply(result -> ResponseEntity.ok("Asynchronous API response: "+result))
                .join();
    }

    @GetMapping("/async")
    public CompletableFuture<ResponseEntity<String>> asyncApi(){
        return myService.asyncMethod()
                .thenApply(result -> ResponseEntity.ok("Asynchronous API response: "+result));
    }

    @GetMapping("/nested-async")
    public CompletableFuture<ResponseEntity<String>> nestedAsyncApi(){
        return nestedAsyncService.asyncMethod1()
                .thenApply(result -> ResponseEntity.ok("Async API Response: " + result));
    }

    @GetMapping("/two-async-in-async")
    public CompletableFuture<ResponseEntity<String>> TwoAsyncInAsyncApi(){
        return twoAsyncInAsyncService.asyncMethod1()
                .thenApply(result -> ResponseEntity.ok("Async API Response: " + result));
    }

}
