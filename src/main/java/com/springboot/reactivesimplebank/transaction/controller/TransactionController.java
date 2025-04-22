package com.springboot.reactivesimplebank.transaction.controller;

import com.springboot.reactivesimplebank.dto.transactionDto.TotalAmount;
import com.springboot.reactivesimplebank.transaction.Service.TransactionService;
import com.springboot.reactivesimplebank.transaction.model.Transaction;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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

    @GetMapping("all/{bankAccountId}")
    public Flux<Transaction> getAllTransactionsByBankAccountId(@PathVariable final Long bankAccountId) {
        return transactionService.findAllByBankAccountId(bankAccountId);
    }

    @GetMapping("/{type}/{bankAccountId}")
    public Flux<Transaction> getAllTransactionsByTypeAndBankAccountId(@PathVariable final String type,
                                                                      @PathVariable final Long bankAccountId) {
        return transactionService.findAllByTypeAndBankAccount(type, bankAccountId);
    }

    @GetMapping(value = "/resume-by-type/{type}/{bankAccountId}")
    public Mono<TotalAmount> resumeByType(@PathVariable final String type,
                                          @PathVariable final Long bankAccountId) {

        return transactionService.getResumeByType(type, bankAccountId);
    }

    @PostMapping
    public Mono<Transaction> createTransaction(@RequestBody final Transaction transaction) {
        return transactionService.save(transaction);
    }

    @PutMapping
    public Mono<Transaction> updateTransaction(@RequestBody final Transaction transaction) {
        return transactionService.update(transaction);
    }

    @DeleteMapping("/{transactionId}")
    public Mono<String> deleteTransaction(@PathVariable final Long transactionId) {
        return transactionService.deleteById(transactionId);
    }

}
