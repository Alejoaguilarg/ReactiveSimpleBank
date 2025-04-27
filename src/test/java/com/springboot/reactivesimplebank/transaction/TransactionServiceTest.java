package com.springboot.reactivesimplebank.transaction;

import com.springboot.reactivesimplebank.TestUtils;
import com.springboot.reactivesimplebank.bankAccount.service.BankAccountService;
import com.springboot.reactivesimplebank.exception.customExceptions.DuplicateEntityException;
import com.springboot.reactivesimplebank.exception.customExceptions.EntityNotFoundException;
import com.springboot.reactivesimplebank.transaction.Service.TransactionService;
import com.springboot.reactivesimplebank.transaction.model.Transaction;
import com.springboot.reactivesimplebank.transaction.repository.ITransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private ITransactionRepository transactionRepository;

    @Mock
    private BankAccountService bankAccountService;

    @InjectMocks
    private TransactionService transactionService;

    private final Long TRANSACTION_ID = 1L;

    private final TestUtils testUtils = new TestUtils();

    public final String DEPOSIT_TYPE = "deposit";
    public final String WITHDRAWAL_TYPE = "withdrawal";
    public final String wrongType = "WITHDRAWAL_TYPE";

    @Test
    void findByIdTest() {

        Mono<Transaction> sampleTransaction = testUtils.createSampleTransaction();

        when(transactionRepository.findById(TRANSACTION_ID))
                .thenReturn(sampleTransaction);

        StepVerifier.create(transactionService.findById(TRANSACTION_ID))
                .expectNextMatches(transaction -> transaction.getAmount() == 1000L &&
                        transaction.getTransactionId().equals(TRANSACTION_ID))
                .verifyComplete();

        verify(transactionRepository).findById(TRANSACTION_ID);
    }

    @Test
    void findByIdNonExistingTest() {
        when(transactionRepository.findById(TRANSACTION_ID))
                .thenReturn(Mono.empty());

        StepVerifier.create(transactionService.findById(TRANSACTION_ID))
                .expectError(EntityNotFoundException.class)
                .verify();

        verify(transactionRepository).findById(TRANSACTION_ID);
    }

    @Test
    void findAllTest() {
        when(transactionRepository.findAll())
                .thenReturn(testUtils.createFluxTransactions());

        StepVerifier.create(transactionService.findAll())
                .expectNextCount(5)
                .verifyComplete();

        verify(transactionRepository).findAll();
    }

    @Test
    void findAllEmptyTest() {
        when(transactionRepository.findAll())
                .thenReturn(Flux.empty());

        StepVerifier.create(transactionService.findAll())
                .expectError(EntityNotFoundException.class)
                .verify();

        verify(transactionRepository).findAll();
    }

    @Test
    void saveTest() {
        final Transaction sampleTransaction = testUtils.getTestTransaction();

        when(transactionRepository.existsById(sampleTransaction.getTransactionId()))
                .thenReturn(Mono.just(false));

        when(transactionRepository.save(sampleTransaction))
                .thenReturn(Mono.just(sampleTransaction));

        StepVerifier.create(transactionService.save(sampleTransaction))
                .expectNextMatches(transaction -> transaction.getType().equals(DEPOSIT_TYPE) &&
                        Objects.equals(transaction.getTransactionId(), TRANSACTION_ID) &&
                        Objects.equals(transaction.getAmount(), 1000L
                        )
                )
                .verifyComplete();

        verify(transactionRepository).existsById(sampleTransaction.getTransactionId());
        verify(transactionRepository).save(sampleTransaction);
    }

    @Test
    void saveEmptyTransactionTest() {
        when(transactionRepository.existsById(TRANSACTION_ID))
                .thenReturn(Mono.just(true));

        StepVerifier.create(transactionService.save(testUtils.getTestTransaction()))
                .expectError(DuplicateEntityException.class)
                .verify();

        verify(transactionRepository).existsById(TRANSACTION_ID);
    }

    @Test
    void updateTest() {
        final Transaction sampleTransaction = testUtils.getTestTransaction();
        final Transaction existingTransaction = new Transaction(2L, DEPOSIT_TYPE, 1000L, 1L);

        when(transactionRepository.findById(TRANSACTION_ID))
                .thenReturn(Mono.just(existingTransaction));

        when(transactionRepository.save(any()))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(transactionService.update(sampleTransaction))
                .assertNext(updated -> {
                    assertEquals(updated.getTransactionId(), updated.getTransactionId());
                    assertEquals(updated.getType(), updated.getType());
                    assertEquals(updated.getAmount(), updated.getAmount());
                    assertEquals(updated.getBankAccountId(), updated.getBankAccountId());
                })
                .verifyComplete();

        verify(transactionRepository).findById(TRANSACTION_ID);
        verify(transactionRepository).save(any());
    }

    @Test
    void updateEmptyTest() {
        final Transaction sampleTransaction = testUtils.getTestTransaction();

        when(transactionRepository.findById(TRANSACTION_ID))
                .thenReturn(Mono.empty());


        StepVerifier.create(transactionService.update(sampleTransaction))
                .expectError(EntityNotFoundException.class)
                .verify();

        verify(transactionRepository, never()).save(any());
    }

    @Test
    void deleteByIdTest() {
        final Transaction sampleTransaction = testUtils.getTestTransaction();

        when(transactionRepository.findById(TRANSACTION_ID))
                .thenReturn(Mono.just(sampleTransaction));

        when(transactionRepository.delete(sampleTransaction))
                .thenReturn(Mono.empty());

        StepVerifier.create(transactionService.deleteById(TRANSACTION_ID))
                .expectNext("[Transaction Service] Transaction with id: " + TRANSACTION_ID + " successfully deleted")
                .verifyComplete();

        verify(transactionRepository).findById(TRANSACTION_ID);
        verify(transactionRepository).delete(sampleTransaction);
        verifyNoMoreInteractions(transactionRepository);
    }

    @Test
    void deleteByIdEmptyTest() {

        when(transactionRepository.findById(TRANSACTION_ID))
                .thenReturn(Mono.empty());

        StepVerifier.create(transactionService.deleteById(TRANSACTION_ID))
                .expectError(EntityNotFoundException.class)
                .verify();

        verify(transactionRepository).findById(TRANSACTION_ID);
        verify(transactionRepository, never()).delete(any());
        verifyNoMoreInteractions(transactionRepository);
    }

    @Test
    void findAllByBankAccountIdTest() {
        when(transactionRepository.findAllByBankAccountId(anyLong()))
                .thenReturn(testUtils.createFluxTransactions());

        StepVerifier.create(transactionService.findAllByBankAccountId(anyLong()))
                .expectNextCount(5)
                .verifyComplete();

        verify(transactionRepository).findAllByBankAccountId(anyLong());
        verifyNoMoreInteractions(transactionRepository);
    }

    @Test
    void findAllByBankAccountIdEmptyTest() {
        when(transactionRepository.findAllByBankAccountId(anyLong()))
                .thenReturn(Flux.empty());

        StepVerifier.create(transactionService.findAllByBankAccountId(anyLong()))
                .expectError(EntityNotFoundException.class)
                .verify();

        verify(transactionRepository).findAllByBankAccountId(anyLong());
        verifyNoMoreInteractions(transactionRepository);
    }

    @Test
    void findAllByTypeAndBanckAccountTest() {
        when(transactionRepository.findAllByTypeAndBankAccountId(anyString(), anyLong()))
                .thenReturn(testUtils.createFluxTransactionsDeposit());

        StepVerifier.create(transactionService.findAllByTypeAndBankAccount(DEPOSIT_TYPE, 2L))
                .expectNextCount(5)
                .verifyComplete();

        verify(transactionRepository).findAllByTypeAndBankAccountId(DEPOSIT_TYPE, 2L);
        verifyNoMoreInteractions(transactionRepository);
    }

    @Test
    void findAllByTypeAndBanckAccountWithdrawalTest() {
        when(transactionRepository.findAllByTypeAndBankAccountId(anyString(), anyLong()))
                .thenReturn(testUtils.createFluxTransactionsDeposit());

        StepVerifier.create(transactionService.findAllByTypeAndBankAccount(WITHDRAWAL_TYPE, 2L))
                .expectNextCount(5)
                .verifyComplete();

        verify(transactionRepository).findAllByTypeAndBankAccountId(WITHDRAWAL_TYPE, 2L);
        verifyNoMoreInteractions(transactionRepository);
    }

    @Test
    void findAllByTypeAndBanckAccountWrongTypeTest() {

        StepVerifier.create(transactionService.findAllByTypeAndBankAccount(wrongType, 2L))
                .expectError(IllegalArgumentException.class)
                .verify();

        verifyNoMoreInteractions(transactionRepository);
    }

    @Test
    void findAllByTypeAndBanckAccountNullTypeTest() {

        StepVerifier.create(transactionService.findAllByTypeAndBankAccount(null, 2L))
                .expectError(IllegalArgumentException.class)
                .verify();

        verifyNoMoreInteractions(transactionRepository);
    }

    @Test
    void findAllByTypeAndNullBanckAccountTest() {

        StepVerifier.create(transactionService.findAllByTypeAndBankAccount(WITHDRAWAL_TYPE, null))
                .expectError(IllegalArgumentException.class)
                .verify();

        verifyNoMoreInteractions(transactionRepository);
    }

    @Test
    void findAllByTypeAndWrongBanckAccountTest() {

        StepVerifier.create(transactionService.findAllByTypeAndBankAccount(WITHDRAWAL_TYPE, -1L))
                .expectError(IllegalArgumentException.class)
                .verify();

        verifyNoMoreInteractions(transactionRepository);
    }

    @Test
    void getResumeByType() {
        when(transactionRepository.findAllByTypeAndBankAccountId(anyString(), anyLong()))
                .thenReturn(testUtils.createFluxTransactionsDeposit());

        StepVerifier.create(transactionService.getResumeByType(DEPOSIT_TYPE, 2L))
                .expectNextMatches(totalAmount -> Objects.equals(totalAmount.getTotalAmount(), 3700D) &&
                        Objects.equals(totalAmount.getTransactions().size(), 5))
                .verifyComplete();

        verify(transactionRepository).findAllByTypeAndBankAccountId(DEPOSIT_TYPE, 2L);
        verifyNoMoreInteractions(transactionRepository);
    }

    @Test
    void getResumeByTypeNullType() {
        StepVerifier.create(transactionService.getResumeByType(null, 2L))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void getResumeByTypeWrongType() {
        StepVerifier.create(transactionService.getResumeByType(wrongType, 2L))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void getResumeByTypeNullAccountId() {
        StepVerifier.create(transactionService.getResumeByType(WITHDRAWAL_TYPE, null))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void getResumeByTypeWrongAccountId() {
        StepVerifier.create(transactionService.getResumeByType(WITHDRAWAL_TYPE, -1L))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void getFullResume() {
        when(transactionRepository.findAllByBankAccountId(2L))
                .thenReturn(testUtils.createFluxTransactions());

        StepVerifier.create(transactionService.getFullResume(2L))
                .expectNextMatches(totalAmount -> Objects.equals(totalAmount.getTotalAmount(), 3700D) &&
                        Objects.equals(totalAmount.getTransactions().size(), 5))
                .verifyComplete();
    }

    @Test
    void getFullResumeNullAccountId() {
        StepVerifier.create(transactionService.getFullResume(null))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void getFullResumeWrongAccountId() {
        StepVerifier.create(transactionService.getFullResume(-2L))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

}
