package com.springboot.reactivesimplebank.customerTest;

import com.springboot.reactivesimplebank.TestUtils;
import com.springboot.reactivesimplebank.bankAccount.service.BankAccountService;
import com.springboot.reactivesimplebank.costumer.model.Costumer;
import com.springboot.reactivesimplebank.costumer.respository.ICostumerRepository;
import com.springboot.reactivesimplebank.costumer.service.CostumerService;
import com.springboot.reactivesimplebank.dto.transactionDto.TotalAmount;
import com.springboot.reactivesimplebank.exception.customExceptions.DuplicateEntityException;
import com.springboot.reactivesimplebank.exception.customExceptions.EntityNotFoundException;
import com.springboot.reactivesimplebank.transaction.Service.TransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class CostumerServiceTest {

    @Mock
    private ICostumerRepository costumerRepository;

    @Mock
    private BankAccountService bankAccountService;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private CostumerService costumerService;

    private final TestUtils testUtils = new TestUtils();
    private final Long id = 5L;


    @Test
    void findByIdTest() {
        final Costumer costumer = testUtils.testCostumerWithId(1L);

        when(costumerRepository.findById(costumer.getCostumerId()))
                .thenReturn(Mono.just(costumer));

        StepVerifier.create(costumerService.findById(costumer.getCostumerId()))
                .expectNext(costumer)
                .verifyComplete();
    }

    @Test
    void findAllTest() {
        final Costumer costumer = testUtils.testCostumerWithId(1L);
        final Costumer costumer2 = testUtils.testCostumerWithId(2L);

        when(costumerRepository.findAll())
                .thenReturn(Flux.just(costumer, costumer2));

        StepVerifier.create(costumerService.findAll())
                .expectNext(costumer, costumer2)
                .verifyComplete();
    }

    @Test
    void saveTest() {
        final Costumer costumer = testUtils.testCostumerWithId(3L);

        when(costumerRepository.findByEmailAddress(costumer.getEmailAddress()))
                .thenReturn(Mono.empty());

        when(costumerRepository.save(costumer))
                .thenReturn(Mono.just(costumer));

        StepVerifier.create(costumerService.save(costumer))
                .expectNext(costumer)
                .verifyComplete();
    }

    @Test
    void saveDuplicateEmailTest() {
        final Costumer costumer = testUtils.testCostumerWithId(3L);

        when(costumerRepository.findByEmailAddress(costumer.getEmailAddress()))
                .thenReturn(Mono.just(costumer));

        when(costumerRepository.save(costumer))
                .thenReturn(Mono.just(costumer));

        StepVerifier.create(costumerService.save(costumer))
                .expectError(DuplicateEntityException.class)
                .verify();
    }

    @Test
    void updateTest() {
        Costumer input = new Costumer(id, "Pepita", "3001234567", "pepita@mail.com");
        Costumer existing = new Costumer(id, "Luisa", "3201112222", "luisa@mail.com");

        when(costumerRepository.findById(id))
                .thenReturn(Mono.just(existing));
        when(costumerRepository.save(any()))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(costumerService.update(input))
                .assertNext(updated -> {
                    assertEquals(id,             updated.getCostumerId());
                    assertEquals("Pepita",       updated.getName());
                    assertEquals("pepita@mail.com", updated.getEmailAddress());
                    assertEquals("3001234567",   updated.getPhoneNumber());
                })
                .verifyComplete();
    }

    @Test
    void updateNonExistingTest() {
        final Costumer costumer = testUtils.testCostumerWithId(3L);

        when(costumerRepository.findById(costumer.getCostumerId()))
                .thenReturn(Mono.empty());

        StepVerifier.create(costumerService.update(costumer))
                .expectError(EntityNotFoundException.class)
                .verify();
    }

    @Test
    void deleteTest() {
        final Costumer costumer = testUtils.testCostumerWithId(3L);

        when(costumerRepository.findById(costumer.getCostumerId()))
                .thenReturn(Mono.just(costumer));

        when(costumerRepository.deleteById(costumer.getCostumerId()))
                .thenReturn(Mono.empty());

        StepVerifier.create(costumerService.delete(costumer.getCostumerId()))
                .expectNext("Customer with id " + costumer.getCostumerId() + " deleted successfully")
                .verifyComplete();

        verify(costumerRepository, times(1)).findById(costumer.getCostumerId());
        verify(costumerRepository, times(1)).deleteById(costumer.getCostumerId());
        verifyNoMoreInteractions(costumerRepository);
    }

    @Test
    void deleteNonExistingTest() {
        when(costumerRepository.findById(id))
                .thenReturn(Mono.empty());

        StepVerifier.create(costumerService.delete(id))
                .expectError(EntityNotFoundException.class)
                .verify();

        verify(costumerRepository, times(1)).findById(id);
        verifyNoMoreInteractions(costumerRepository);
    }

    @Test
    void getBankAccountResumeUserIdTest() {
        final Costumer costumer = testUtils.testCostumerWithId(3L);

        when(costumerRepository.findById(costumer.getCostumerId()))
                .thenReturn(Mono.just(costumer));

        when(bankAccountService.findAllBankAccountsByCustomerId(costumer.getCostumerId()))
                .thenReturn(testUtils.getBancAccountTestFlux());

        when(transactionService.getFullResume(anyLong()))
                .thenReturn(Mono.just(new TotalAmount(
                        1000L,
                        testUtils.createSampleTransactions()
                )));

        StepVerifier.create(costumerService.getBankAccountResumeUserId(costumer.getCostumerId()))
                .assertNext(
                        response -> {
                            assertEquals(response.customerName(), costumer.getName());
                            assertEquals(5000L, response.totalAmount());
                        }
                )
                .verifyComplete();

    }

    @Test
    void getBankAccountResumeEmptyUserTest() {

        when(costumerRepository.findById(id))
                .thenReturn(Mono.empty());

        StepVerifier.create(costumerService.getBankAccountResumeUserId(id))
                .expectError(EntityNotFoundException.class)
                .verify();
    }
}
