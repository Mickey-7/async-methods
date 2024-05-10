package com.example.syncasync.completableFuture.controller;

import com.example.syncasync.completableFuture.service.AccountService;
import com.example.syncasync.completableFuture.service.BalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@RestController
public class Controller {

    //fetch a bank account object from a remote service
    //and once the account is available, we fetch the balance
    // of that account from another service

    @Autowired
    BalanceService balanceService;


    /* thenApply - nested completable future */
    @GetMapping("/then-apply")
    public void thenApply() throws ExecutionException, InterruptedException {
        //getAccountBalance has return type of CompletableFuture<Double>
        //getBankAccount has a return type of CompletableFuture<BankAccount>
        CompletableFuture<CompletableFuture<Double>> result =
                accountService.getBankAccount("123")
                        .thenApply(bankAccount -> balanceService.getAccountBalance(bankAccount));
        System.out.println(result.get().get());//7.0
    }

    @Autowired
    AccountService accountService;

    /* thenCompose - top level future - used to combine two future
        where one future is dependent on the other
    */
    @GetMapping("/then-compose")
    public void thenCompose() throws ExecutionException, InterruptedException {
        CompletableFuture<Double> result =
                accountService.getBankAccount("123")
                        .thenCompose(bankAccount -> balanceService.getAccountBalance(bankAccount));
        System.out.println(result.get());//7.0
    }

    /* thenCombine - used when you want two futures to run independently
        and do something after both are complete
    */
    @GetMapping("/then-combine")
    public void thenCombine() throws ExecutionException, InterruptedException {
        CompletableFuture<Double> posFut = CompletableFuture.supplyAsync(() -> {
            System.out.println("retrieving positive number...");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return 123.0;
        });

        CompletableFuture<Double> negFut = CompletableFuture.supplyAsync(() -> {
            System.out.println("retrieving negative number...");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return -99.0;
        });


        CompletableFuture<Double> combinedFuture = posFut.thenCombine(
                negFut, (posVal, negVal) -> posVal - negVal
        );
        System.out.println("pos -neg " + combinedFuture.get());
        //retrieving positive number...
        //retrieving negative number...
        //pos -neg 222.0
    }

    /* thenAcceptBoth - two future results but don't need to pass any resulting value down a future chain */
    @GetMapping("/then-accept-both")
    public void thenAcceptBoth() throws ExecutionException, InterruptedException {
        CompletableFuture<Double> posFut = CompletableFuture.supplyAsync(() -> {
            System.out.println("retrieving positive number...");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return 123.0;
        });

        CompletableFuture<Double> negFut = CompletableFuture.supplyAsync(() -> {
            System.out.println("retrieving negative number...");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return -99.0;
        });

        posFut.thenAcceptBoth(
                negFut, (posVal, negVal) -> System.out.println(posVal - negVal)

        ).get();
        //retrieving positive number...
        //retrieving negative number...
        //222.0
    }


    /* CompletableFuture.allOf - executes multiple Futures in parallel, waits for all of
        them to finish and then processes their combined results
    */
    @GetMapping("/all-of")
    public void allOf() throws ExecutionException, InterruptedException {
        List<CompletableFuture<String>> futures = List.of(
                CompletableFuture.supplyAsync(() -> "Like"),
                CompletableFuture.supplyAsync(() -> "Subscribe"),
                CompletableFuture.supplyAsync(() -> "Geekific")
        );

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[3]))
                .thenAccept(v ->
                        futures.stream()
                                .map(CompletableFuture::join)
                                .forEach(System.out::println))
                .get();
        //Like
        //Subscribe
        //Geekific
    }


    /* CompletableFuture.anyOf - returns a new completed completable future
        when any of the given futures completes
    */
    @GetMapping("/any-of")
    public void anyOf() throws ExecutionException, InterruptedException {
        List<CompletableFuture<String>> futures = List.of(
                CompletableFuture.supplyAsync(() -> {
                    try {
                        TimeUnit.SECONDS.sleep(3);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return "Like";
                }),
                CompletableFuture.supplyAsync(() -> {
                    try {
                        TimeUnit.SECONDS.sleep(2);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return "Subscribe";
                }),
                CompletableFuture.supplyAsync(() -> {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return "Geekific";
                })
        );

        System.out.println(
                CompletableFuture.anyOf(futures.toArray(new CompletableFuture[3]))
                        .get()
        );
        //Geekific
    }

    /* Exception Handling */

    /* exceptionally */
    @GetMapping("/exceptionally")
    public void exceptionally() throws ExecutionException, InterruptedException {
        List<String> myList =  new ArrayList<>();
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            if (myList.isEmpty()){
                throw new IllegalArgumentException("List provided cannot be empty");
            }
            //DO something with the list
            return "List processed Successfully";
        }).exceptionally(exception -> {
            System.out.println("Exception: "+exception.getMessage());
            //Exception: java.lang.IllegalArgumentException: List provided cannot be empty
            return "Exception Occurred";
        });
        System.out.println("Result: "+future.get());
        //Result: Exception Occurred
    }

    /* handle - used to recover from an exception */
    @GetMapping("/handle")
    public void handle() throws ExecutionException, InterruptedException {
        List<String> myList =  new ArrayList<>();
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            if (myList.isEmpty()){
                throw new IllegalArgumentException("List provided cannot be empty");
            }
            //DO something with the list
            return "List processed Successfully";
        }).handle((result, exception) -> {
            if (exception == null) return result;
            System.out.println("Exception: "+exception.getMessage());
            //Exception: java.lang.IllegalArgumentException: List provided cannot be empty
            return "Exception Occurred";
        });
        System.out.println("Result: "+future.get());
        //Result: Exception Occurred
    }

}
