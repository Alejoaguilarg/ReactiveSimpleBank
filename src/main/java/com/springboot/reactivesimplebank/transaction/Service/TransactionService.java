package com.springboot.reactivesimplebank.transaction.Service;

import com.springboot.reactivesimplebank.bankAccount.service.BankAccountService;
import com.springboot.reactivesimplebank.dto.transactionDto.TotalAmount;
import com.springboot.reactivesimplebank.exception.customExceptions.DuplicateEntityException;
import com.springboot.reactivesimplebank.exception.customExceptions.EntityNotFoundException;
import com.springboot.reactivesimplebank.transaction.model.Transaction;
import com.springboot.reactivesimplebank.transaction.repository.ITransactionRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final ITransactionRepository transactionRepository;

    private static final String EXISTING_TRANSACTION = "[Transaction Service] The transaction with id: %s, already exists";
    private static final String TRANSACTION_NOT_FOUND = "[Transaction Service] Transaction with id: %s not found";
    private static final Set<String> VALID_TYPES = Set.of("withdrawal", "deposit", "transfer");

    public TransactionService(final ITransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Mono<Transaction> findById(final Long id) {
        return transactionRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(formatMessage(TRANSACTION_NOT_FOUND,
                        String.valueOf(id)))));
    }

    public Flux<Transaction> findAll() {
        return transactionRepository.findAll()
                .switchIfEmpty(Mono.error(new EntityNotFoundException("[Transaction Service] No transactions found")));
    }

    public Mono<Transaction> save(final Transaction transaction) {
        return transactionRepository.existsById(transaction.getTransactionId())
                .flatMap(exists -> exists
                                ? Mono.error(new DuplicateEntityException(
                                formatMessage(EXISTING_TRANSACTION,
                                        String.valueOf(transaction.getTransactionId()))
                        ))
                                : transactionRepository.save(transaction)
                );
    }

    public Mono<Transaction> update(final Transaction transaction) {
        return transactionRepository.findById(transaction.getTransactionId())
                .switchIfEmpty(Mono.error(new EntityNotFoundException(
                        formatMessage(TRANSACTION_NOT_FOUND, String.valueOf(transaction.getTransactionId()))))
                ).flatMap(transactionRepository::save);
    }

    public Mono<String> deleteById(final Long id) {
        return transactionRepository.findById(id)
        .switchIfEmpty(Mono.error(new EntityNotFoundException(
                        formatMessage(TRANSACTION_NOT_FOUND, String.valueOf(id))))
                )
                .flatMap(transactionRepository::delete)
                .then(Mono.just(
                        String.format("[Transaction Service] Transaction with id: %s successfully deleted", id)));
    }

    public Flux<Transaction> findAllByBankAccountId(final Long bankAccountId) {
        return transactionRepository.findAllByBankAccountId(bankAccountId)
                .switchIfEmpty(Flux.error(new EntityNotFoundException(
                        formatMessage(TRANSACTION_NOT_FOUND, String.valueOf(bankAccountId)
                ))));
    }

    public Flux<Transaction> findAllByTypeAndBankAccount(final String type, final Long bankAccountId) {
        return Mono.zip(validateType(type), validateBankAccountId(bankAccountId))
                .flatMapMany(tuple -> transactionRepository
                        .findAllByTypeAndBankAccountId(tuple.getT1(),tuple.getT2()));
    }

    public Mono<TotalAmount> getResumeByType(final String type, final Long bankAccountId) {

        return Mono.zip(validateType(type), validateBankAccountId(bankAccountId))
                .flatMap(tuple -> {
                    Flux<Transaction> filterTransactions = transactionRepository
                            .findAllByTypeAndBankAccountId(tuple.getT1(), tuple.getT2())
                            .take(100);

                    Mono<Double> total = calculateAmountByType(filterTransactions);
                    return total.zipWith(filterTransactions
                            .collectList())
                            .map(result -> new TotalAmount(result.getT1(), result.getT2()));
                });
    }

    public Mono<TotalAmount> getFullResume(Long bankAccountId) {
        return validateBankAccountId(bankAccountId)
                .flatMap(this::loadAndCompute);
    }

    private Mono<Long> validateBankAccountId(Long id) {
        if (id == null || id < 0) {
            return Mono.error(new IllegalArgumentException("Bank account id cannot be null"));
        }
        return Mono.just(id);
    }

    private Mono<TotalAmount> loadAndCompute(Long validId) {
        Flux<Transaction> txFlux = fetchTransactions(validId);

        Mono<List<Transaction>> listMono = txFlux.collectList();
        Mono<Double> totalMono = listMono
                .flatMapMany(Flux::fromIterable)
                .map(Transaction::getAmount)
                .reduce(0.0, Double::sum);

        return Mono.zip(totalMono, listMono)
                .map(tuple -> new TotalAmount(tuple.getT1(), tuple.getT2()));
    }

    private Flux<Transaction> fetchTransactions(Long bankAccountId) {
        return transactionRepository
                .findAllByBankAccountId(bankAccountId)
                .take(100)
                .cache();
    }


    private String formatMessage(final String message, final String argument) {
        return String.format(message, argument);
    }

    private Mono<String> validateType(final String type) {
        return Mono.justOrEmpty(type)
                .filter(Objects::nonNull)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Type cannot be null.")))
                .map(String::toLowerCase)
                .filter(VALID_TYPES::contains)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(
                        String.format(
                                "[Transaction service] Type: %s not valid, valid types: %s", type, VALID_TYPES
                        ))));
    }

    private Mono<Double> calculateAmountByType(final Flux<Transaction> transactions) {
        return transactions
                .map(Transaction::getAmount)
                .reduce(0.0, Double::sum);
    }
}
