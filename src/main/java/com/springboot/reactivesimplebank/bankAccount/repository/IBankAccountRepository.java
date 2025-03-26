package com.springboot.reactivesimplebank.bankAccount.repository;

import com.springboot.reactivesimplebank.bankAccount.model.BankAccount;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface IBankAccountRepository extends ReactiveCrudRepository<BankAccount, Long> {
    public Flux<BankAccount> findAllByCostumerId(final Long costumerId);
}
