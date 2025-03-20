package com.springboot.reactivesimplebank.transaction.repository;

import com.springboot.reactivesimplebank.transaction.model.Transaction;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface ITransactionRepository extends ReactiveCrudRepository<Transaction, Long> {
    public Mono<Transaction> findByBankAccountId(final Long bankAccountId);
}
