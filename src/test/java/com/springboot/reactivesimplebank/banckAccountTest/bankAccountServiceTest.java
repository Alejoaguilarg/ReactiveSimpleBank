package com.springboot.reactivesimplebank.banckAccountTest;

import com.springboot.reactivesimplebank.TestUtils;
import com.springboot.reactivesimplebank.bankAccount.repository.IBankAccountRepository;
import com.springboot.reactivesimplebank.bankAccount.service.BankAccountService;
import com.springboot.reactivesimplebank.costumer.respository.ICostumerRepository;
import com.springboot.reactivesimplebank.exception.customExceptions.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class bankAccountServiceTest {

    @Mock
    private IBankAccountRepository bankAccountRepository;

    @Mock
    private ICostumerRepository costumerRepository;

    @InjectMocks
    private BankAccountService bankAccountService;

    private final TestUtils testUtils = new TestUtils();

    private final Long id = 1L;

    private final String BANC_ACCOUNT_NUMBER = "234325432";


    @Test
    void findByIdTest() {

        when(bankAccountRepository.findById(id))
                .thenReturn(testUtils.getMonoTestBankAccount(1L));

        StepVerifier.create(bankAccountService.findById(id))
                .assertNext(bankAccount ->
                        assertAll("Banco asserts",
                                () -> assertEquals(id, bankAccount.getBankAccountId()),
                                () -> assertEquals(BANC_ACCOUNT_NUMBER, bankAccount.getNumber())
                        ))
                .verifyComplete();

        verify(bankAccountRepository).findById(id);
        verifyNoMoreInteractions(bankAccountRepository);
    }

    @Test
    void findByIdEmptyTest() {
        when(bankAccountRepository.findById(id))
                .thenReturn(Mono.empty());

        StepVerifier.create(bankAccountService.findById(id))
                .expectError(EntityNotFoundException.class)
                .verify();

        verify(bankAccountRepository).findById(id);
        verifyNoMoreInteractions(bankAccountRepository);
    }

    @Test
    void findAllAccountsTest() {
        when(bankAccountRepository.findAll())
                .thenReturn(testUtils.getBancAccountTestFlux());

        StepVerifier.create(bankAccountService.findAllBankAccounts())
                .expectNextCount(5)
                .verifyComplete();

        verify(bankAccountRepository).findAll();
        verifyNoMoreInteractions(bankAccountRepository);
    }

    @Test
    void findAllBankAccountsByCustomerIdTest() {
        when(bankAccountRepository.findAllByCostumerId(id))
                .thenReturn(testUtils.getBancAccountTestFlux());

        StepVerifier.create(bankAccountService.findAllBankAccountsByCustomerId(id))
                .expectNextCount(5)
                .verifyComplete();

        verify(bankAccountRepository).findAllByCostumerId(id);
        verifyNoMoreInteractions(bankAccountRepository);
    }

    @Test
    void saveTest() {
        when(costumerRepository.existsById(anyLong()))
                .thenReturn(Mono.just(true));

        when(bankAccountRepository.save(any()))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(bankAccountService.save(testUtils.getTestBankAccount(1L)))
                .assertNext(
                        bankAccount -> assertAll("Banco save asserts",
                                () -> assertEquals(2L, bankAccount.getCostumerId()),
                                () -> assertEquals(BANC_ACCOUNT_NUMBER, bankAccount.getNumber())
                        )
                )
                .verifyComplete();

        verify(costumerRepository).existsById(anyLong());
        verify(bankAccountRepository).save(any());
        verifyNoMoreInteractions(costumerRepository, bankAccountRepository);
    }

    @Test
    void saveNonExistingCostumerTest() {
        when(costumerRepository.existsById(anyLong()))
                .thenReturn(Mono.just(false));

        StepVerifier.create(bankAccountService.save(testUtils.getTestBankAccount(1L)))
                .expectError(EntityNotFoundException.class)
                .verify();

        verify(costumerRepository).existsById(anyLong());
        verifyNoMoreInteractions(costumerRepository, bankAccountRepository);
    }

    @Test
    void deleteByIdTest() {
        when(bankAccountRepository.findById(id))
                .thenReturn(testUtils.getMonoTestBankAccount(id));

        when(bankAccountRepository.deleteById(id))
                .thenReturn(Mono.empty());

        StepVerifier.create(bankAccountService.deleteById(id))
                .expectNext("[Bank Service] Bank account with id " + id + " deleted successfully")
                .verifyComplete();

        verify(bankAccountRepository).findById(id);
        verify(bankAccountRepository).deleteById(id);
        verifyNoMoreInteractions(bankAccountRepository);
    }

    @Test
    void deleteByIdNonExistingTest() {
        when(bankAccountRepository.findById(id))
                .thenReturn(Mono.empty());

        StepVerifier.create(bankAccountService.deleteById(id))
                .expectError(EntityNotFoundException.class)
                .verify();

        verify(bankAccountRepository).findById(id);
        verifyNoMoreInteractions(bankAccountRepository);
    }
}

