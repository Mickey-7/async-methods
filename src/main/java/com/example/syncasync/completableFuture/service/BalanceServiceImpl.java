package com.example.syncasync.completableFuture.service;

import com.example.syncasync.completableFuture.model.BankAccount;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class BalanceServiceImpl implements BalanceService {
    @Override
    public CompletableFuture<Double> getAccountBalance(BankAccount account) {
        return CompletableFuture.supplyAsync(() -> Double.valueOf(7));
    }
}
