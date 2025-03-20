package com.springboot.reactivesimplebank.bankAccount.repository;

import com.springboot.reactivesimplebank.bankAccount.model.BankAccount;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface IBankAccountRepository extends ReactiveCrudRepository<BankAccount, Long> {}
