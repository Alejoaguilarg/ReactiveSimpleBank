package com.springboot.reactivesimplebank.banckAccount;

import com.springboot.reactivesimplebank.TestUtils;
import com.springboot.reactivesimplebank.bankAccount.controller.BankAccountController;
import com.springboot.reactivesimplebank.bankAccount.model.BankAccount;
import com.springboot.reactivesimplebank.bankAccount.service.BankAccountService;
import com.springboot.reactivesimplebank.exception.customExceptions.EntityNotFoundException;
import com.springboot.reactivesimplebank.exception.handler.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@WebFluxTest(BankAccountController.class)
@Import(GlobalExceptionHandler.class)
class BankAccountControllerTest {

    public static final String FULL_PAYLOAD = """
      {
        "costumerId": 1
      }
      """;
    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private BankAccountService bankAccountService;

    private final TestUtils testUtils = new TestUtils();

    @Test
    void getBankAccountByIdTest() {
        when(bankAccountService.findById(1L))
                .thenReturn(testUtils.getMonoTestBankAccount(1L));

        webTestClient.get()
                .uri("/accounts/{id}", 1L)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.bankAccountId").isEqualTo(1L)
                .jsonPath("$.costumerId").isEqualTo(2L);
    }

    @Test
    void getBankAccountByIdErrorTest() {
        when(bankAccountService.findById(anyLong()))
                .thenReturn(Mono.error(new EntityNotFoundException("Bank account with id " + 99L + " not found")));

        webTestClient.get()
                .uri("accounts/{id}", 99L)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404);
    }

    @Test
    void getAllAccountsTest() {
        when(bankAccountService.findAllBankAccounts())
                .thenReturn(testUtils.getBancAccountTestFlux());

        webTestClient.get()
                .uri("/accounts/all")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json")
                .expectBodyList(BankAccount.class)
                .hasSize(5);
    }

    @Test
    void getAllAccountsErrorTest() {
        when(bankAccountService.findAllBankAccounts())
                .thenReturn(Flux.error(new EntityNotFoundException("Bank accounts not found")));

        webTestClient.get()
                .uri("/accounts/all")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class)
                .value(err -> {
                    assert err.contains("Bank accounts not found");
                });
    }

    @Test
    void createBankAccountTest() {
        when(bankAccountService.save(any(BankAccount.class)))
                .thenReturn(testUtils.getMonoTestBankAccount(1L));

        webTestClient.post()
                .uri("/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(FULL_PAYLOAD)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.bankAccountId").isEqualTo(1)
                .jsonPath("$.costumerId").isEqualTo(2);
    }

    @Test
    void createBankAccountErrorTest() {
        when(bankAccountService.save(any(BankAccount.class)))
                .thenReturn(Mono.error(new EntityNotFoundException("Bad payload")));

        webTestClient.post()
                .uri("/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(FULL_PAYLOAD)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").exists()
                .jsonPath("$.status").isEqualTo(404);
    }

    @Test
    void deleteBankAccountByIdTest() {
        when(bankAccountService.deleteById(anyLong()))
                .thenReturn(Mono.just("Bank account with id 1 deleted successfully"));

        webTestClient.delete()
                .uri("/accounts/{id}", 1L)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(err -> {
                    assert err.contains("Bank account with id 1 deleted successfully");
                });
    }

    @Test
    void deleteBankAccountByIdErrorTest() {
        when(bankAccountService.deleteById(anyLong()))
                .thenReturn(Mono.error(new EntityNotFoundException("Bank account with id " + 99L + " not found")));

        webTestClient.delete()
                .uri("/accounts/{id}", 99L)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class)
                .value(err -> {
                    assert err.contains("Bank account with id 99 not found");
                });
    }
}
