package com.example.syncasync.completableFuture.service;

import com.example.syncasync.completableFuture.model.BankAccount;

import java.util.concurrent.CompletableFuture;

public interface BalanceService {
     CompletableFuture<Double> getAccountBalance(BankAccount account);
}
