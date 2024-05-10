package com.example.syncasync.completableFuture.service;

import com.example.syncasync.completableFuture.model.BankAccount;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class AccountServiceImpl implements AccountService{
    @Override
    public CompletableFuture<BankAccount> getBankAccount(String accId) {
        return CompletableFuture.supplyAsync(BankAccount::new);
    }
}
