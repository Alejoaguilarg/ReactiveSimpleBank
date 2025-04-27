package com.springboot.reactivesimplebank.bankAccount.controller;

import com.springboot.reactivesimplebank.bankAccount.model.BankAccount;
import com.springboot.reactivesimplebank.bankAccount.service.BankAccountService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/accounts")
public class BankAccountController {

    private final BankAccountService bankAccountService;

    public BankAccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @GetMapping("/{bankAccountId}")
    public Mono<BankAccount> getAccount(@PathVariable final Long bankAccountId) {
        return bankAccountService.findById(bankAccountId);
    }

    @GetMapping("/all")
    public Flux<BankAccount> getAllAccounts() {
        return bankAccountService.findAllBankAccounts();
    }

    @PostMapping()
    public Mono<BankAccount> createBankAccount(@RequestBody final BankAccount bankAccount) {
        return bankAccountService.save(bankAccount);
    }

    @DeleteMapping("/{bankAccountId}")
    public Mono<String> deleteAccount(@PathVariable final Long bankAccountId) {
        return bankAccountService.deleteById(bankAccountId);
    }
}

