package com.springboot.reactivesimplebank.transaction.repository;

import com.springboot.reactivesimplebank.transaction.model.Transaction;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface ITransactionRepository extends ReactiveCrudRepository<Transaction, Long> {
    public Flux<Transaction> findAllByBankAccountId(final Long bankAccountId);
    Flux<Transaction> findAllByTypeAndBankAccountId(String type, Long bankAccountId);
}
