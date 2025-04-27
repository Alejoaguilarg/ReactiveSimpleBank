package com.springboot.reactivesimplebank;

import com.springboot.reactivesimplebank.bankAccount.model.BankAccount;
import com.springboot.reactivesimplebank.costumer.model.Costumer;
import com.springboot.reactivesimplebank.dto.bankAccountDto.AccountWithTransactions;
import com.springboot.reactivesimplebank.dto.bankAccountDto.CustomerAccountsResponse;
import com.springboot.reactivesimplebank.dto.transactionDto.TotalAmount;
import com.springboot.reactivesimplebank.dto.transactionDto.TransactionDto;
import com.springboot.reactivesimplebank.transaction.model.Transaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

public class TestUtils {

    public Costumer testCostumer() {
        final Costumer costumer = new Costumer();
        costumer.setName("Test");
        costumer.setEmailAddress("test@test.com");
        costumer.setPhoneNumber("123456789");

        return costumer;
    }

    public Costumer testCostumerWithId(final Long id) {
        return new Costumer(
                id,
                "Luisa",
                "123456789",
                "<EMAIL>"
        );
    }

    public Flux<BankAccount> getBancAccountTestFlux() {
        return Flux.just(
                new BankAccount(
                        1L,
                        "1234-5678-9012-3456",
                        1L,
                        LocalDateTime.now()
                ),
                new BankAccount(
                        2L,
                        "2345-6789-0123-4567",
                        1L,
                        LocalDateTime.now().minusDays(5)
                ),
                new BankAccount(
                        3L,
                        "3456-7890-1234-5678",
                        2L,
                        LocalDateTime.now().minusDays(10)
                ),
                new BankAccount(
                        4L,
                        "4567-8901-2345-6789",
                        3L,
                        LocalDateTime.now().minusMonths(1)
                ),
                new BankAccount(
                        5L,
                        "5678-9012-3456-7890",
                        4L,
                        LocalDateTime.now().minusMonths(2)
                )
        );
    }

    public Mono<TotalAmount> totalAmount() {
        return Mono.just(new TotalAmount(2000D, createSampleTransactions()));
    }

    public static class TransactionType {
        public static final String DEPOSIT = "deposit";
        public static final String WITHDRAWAL = "withdrawal";
    }

    public List<Transaction> createSampleTransactions() {
        return List.of(
                new Transaction(
                        1L,
                        TransactionType.DEPOSIT,
                        1L,
                        1000L,
                        LocalDateTime.now()
                ),
                new Transaction(
                        2L,
                        TransactionType.WITHDRAWAL,
                        1L,
                        500L,
                        LocalDateTime.now().minusHours(2)
                ),
                new Transaction(
                        3L,
                        TransactionType.DEPOSIT,
                        2L,
                        1500L,
                        LocalDateTime.now().minusHours(3)
                ),
                new Transaction(
                        4L,
                        TransactionType.DEPOSIT,
                        3L,
                        2000L,
                        LocalDateTime.now().minusDays(1)
                ),
                new Transaction(
                        5L,
                        TransactionType.WITHDRAWAL,
                        2L,
                        300L,
                        LocalDateTime.now().minusDays(2)
                )
        );
    }

    public Flux<Transaction> createFluxTransactions() {
        return Flux.just(
                new Transaction(
                        1L,
                        TransactionType.DEPOSIT,
                        1L,
                        1000L,
                        LocalDateTime.now()
                ),
                new Transaction(
                        2L,
                        TransactionType.WITHDRAWAL,
                        1L,
                        -500L,
                        LocalDateTime.now().minusHours(2)
                ),
                new Transaction(
                        3L,
                        TransactionType.DEPOSIT,
                        2L,
                        1500L,
                        LocalDateTime.now().minusHours(3)
                ),
                new Transaction(
                        4L,
                        TransactionType.DEPOSIT,
                        2L,
                        2000L,
                        LocalDateTime.now().minusDays(1)
                ),
                new Transaction(
                        5L,
                        TransactionType.WITHDRAWAL,
                        2L,
                        -300L,
                        LocalDateTime.now().minusDays(2)
                )
        );
    }

    public Flux<Transaction> createFluxTransactionsDeposit() {
        return Flux.just(
                new Transaction(
                        1L,
                        TransactionType.DEPOSIT,
                        1L,
                        1000L,
                        LocalDateTime.now()
                ),
                new Transaction(
                        2L,
                        TransactionType.DEPOSIT,
                        1L,
                        -500L,
                        LocalDateTime.now().minusHours(2)
                ),
                new Transaction(
                        3L,
                        TransactionType.DEPOSIT,
                        2L,
                        1500L,
                        LocalDateTime.now().minusHours(3)
                ),
                new Transaction(
                        4L,
                        TransactionType.DEPOSIT,
                        3L,
                        2000L,
                        LocalDateTime.now().minusDays(1)
                ),
                new Transaction(
                        5L,
                        TransactionType.DEPOSIT,
                        2L,
                        -300L,
                        LocalDateTime.now().minusDays(2)
                )
        );
    }

    public Mono<Transaction> createSampleTransaction() {
        return Mono.just(new Transaction(
                1L,
                TransactionType.DEPOSIT,
                1L,
                1000L,
                LocalDateTime.now()
        ));
    }

    public Transaction getTestTransaction() {
        return new Transaction(
                1L,
                TransactionType.DEPOSIT,
                1L,
                1000L,
                LocalDateTime.now()
        );
    }

    public Mono<BankAccount> getMonoTestBankAccount(final Long id) {
        return Mono.just(
                new BankAccount(
                        id,
                        "234325432",
                        2L,
                        LocalDateTime.now()
                )
        );
    }

    public BankAccount getTestBankAccount(final Long id) {
        return new BankAccount(
                id,
                "234325432",
                2L,
                LocalDateTime.now()
        );
    }

    public Flux<Costumer> getFluxTestCostumer() {
        return Flux.just(
                testCostumerWithId(1L),
                testCostumerWithId(2L),
                testCostumerWithId(3L)
        );
    }

    public CustomerAccountsResponse CustomerAccountsResponse() {

        List<TransactionDto> transactionDtos = List.of(
                new TransactionDto(TransactionType.DEPOSIT, 2300D),
                new TransactionDto(TransactionType.WITHDRAWAL, 100D),
                new TransactionDto(TransactionType.DEPOSIT, 1000D),
                new TransactionDto(TransactionType.WITHDRAWAL, 100D)
        );

        List<TransactionDto> transactionDtos1 = List.of(
                new TransactionDto(TransactionType.DEPOSIT, 200D),
                new TransactionDto(TransactionType.WITHDRAWAL, 100D),
                new TransactionDto(TransactionType.DEPOSIT, 100D),
                new TransactionDto(TransactionType.WITHDRAWAL, 100D)
        );

        List<AccountWithTransactions> accountWithTransactions = List.of(
                new AccountWithTransactions(
                        "23423423",
                        transactionDtos,
                        3500D
                ),
                new AccountWithTransactions(
                        "78932324",
                        transactionDtos1,
                        500D
                )
        );

        return new CustomerAccountsResponse(
                "Luisa", accountWithTransactions, 4500D
        );
    }
}
