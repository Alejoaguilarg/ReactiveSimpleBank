package com.springboot.reactivesimplebank.transaction.controller;

import com.springboot.reactivesimplebank.transaction.Service.TransactionService;
import com.springboot.reactivesimplebank.transaction.model.Transaction;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(final TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/{bankAccountId}")
    public Mono<Transaction> getTransaction(@PathVariable final Long bankAccountId) {
        return transactionService.findById(bankAccountId);
    }

    @GetMapping("/all")
    public Flux<Transaction> getAllTransactions() {
        return transactionService.findAll();
    }

    @PostMapping
    public Mono<Transaction> createTransaction(@RequestBody final Transaction transaction) {
        return transactionService.save(transaction);
    }

    @PutMapping
    public Mono<Transaction> updateTransaction(@RequestBody final Transaction transaction) {
        return transactionService.save(transaction);
    }

    @DeleteMapping("/{transactionId}")
    public Mono<String> deleteTransaction(@PathVariable final Long transactionId) {
        return transactionService.deleteById(transactionId);
    }
}
