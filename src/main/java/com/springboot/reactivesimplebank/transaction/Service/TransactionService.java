package com.springboot.reactivesimplebank.transaction.Service;

import com.springboot.reactivesimplebank.bankAccount.service.BankAccountService;
import com.springboot.reactivesimplebank.exception.customExceptions.EntityNotFoundException;
import com.springboot.reactivesimplebank.transaction.model.Transaction;
import com.springboot.reactivesimplebank.transaction.repository.ITransactionRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TransactionService {

    private static final String EXISTING_BANK_ACCOUNT = "[Transaction Service] There's no exits bank account with id: %s";
    private final ITransactionRepository transactionRepository;
    private final BankAccountService bankAccountService;

    private static final String TRANSACTION_NOT_FOUND = "[Transaction Service] Transaction with id: %s not found";

    public TransactionService(final ITransactionRepository transactionRepository,
                              final BankAccountService bankAccountService) {
        this.transactionRepository = transactionRepository;
        this.bankAccountService = bankAccountService;
    }

    public Mono<Transaction> findById(final Long id) {
        return transactionRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(formatMessage(TRANSACTION_NOT_FOUND, id))));
    }

    public Flux<Transaction> findAll() {
        return transactionRepository.findAll()
                .switchIfEmpty(Mono.error(new EntityNotFoundException("[Transaction Service] No transactions found")));
    }

    public Mono<Transaction> save(final Transaction transaction) {
        return bankAccountService.findById(transaction.getBankAccountId())
                .switchIfEmpty(Mono.error(new EntityNotFoundException(
                        formatMessage(EXISTING_BANK_ACCOUNT,
                        transaction.getBankAccountId()
                        )
                )))
                .flatMap(t -> transactionRepository.save(transaction));
    }

    public Mono<Transaction> update(final Transaction transaction) {
        return transactionRepository.findById(transaction.getTransactionId())
                .switchIfEmpty(Mono.error(new EntityNotFoundException(
                        formatMessage(TRANSACTION_NOT_FOUND, transaction.getTransactionId())))
                ).flatMap(transactionRepository::save);
    }

    public Mono<String> deleteById(final Long id) {
        return transactionRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(
                        formatMessage(TRANSACTION_NOT_FOUND, id)))
                )
                .flatMap(transactionRepository::delete)
                .then(Mono.just("[Transaction Service] Transaction with id: %s successfully deleted"));
    }

    private String formatMessage(final String message, Long id) {
        return String.format(message, id);
    }
}
