package com.springboot.reactivesimplebank.transaction;

import com.springboot.reactivesimplebank.TestUtils;
import com.springboot.reactivesimplebank.exception.customExceptions.EntityNotFoundException;
import com.springboot.reactivesimplebank.exception.handler.GlobalExceptionHandler;
import com.springboot.reactivesimplebank.transaction.Service.TransactionService;
import com.springboot.reactivesimplebank.transaction.controller.TransactionController;
import com.springboot.reactivesimplebank.transaction.model.Transaction;
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
import static org.springframework.http.MediaType.APPLICATION_JSON;

@WebFluxTest(TransactionController.class)
@Import(GlobalExceptionHandler.class)
class TransactionControllerTest {

    public static final String FULL_PAYLOAD = """
            {
                "amount": "1000",
                "type": "deposit",
                "bankAccountId": 1
            }
            """;
    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private TransactionService transactionService;

    private final TestUtils testUtils = new TestUtils();

    @Test
    void createTransactionTest() {
        when(transactionService.save(any(Transaction.class)))
                .thenReturn(testUtils.createSampleTransaction());

        webTestClient.post()
                .uri("/transactions")
                .contentType(APPLICATION_JSON)
                .bodyValue(FULL_PAYLOAD)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.transactionId").isEqualTo(1L)
                .jsonPath("$.amount").isEqualTo(1000.0)
                .jsonPath("$.type").isEqualTo("deposit")
                .jsonPath("$.bankAccountId").isEqualTo(1L);
    }

    @Test
    void createTransactionValidationErrorTest() {
        when(transactionService.save(any(Transaction.class)))
                .thenReturn(Mono.error(new EntityNotFoundException("Bad payload")));

        String badPayload = """
      {
        "amount": 100L,
        "type": "",
        "bankAccountId": null
      }
      """;

        webTestClient.post()
                .uri("/transactions")
                .contentType(APPLICATION_JSON)
                .bodyValue(badPayload)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").exists()
                .jsonPath("$.status").isEqualTo(404);
    }

    @Test
    void getTransactionByIdTest() {
        when(transactionService.findById(any(Long.class)))
                .thenReturn(testUtils.createSampleTransaction());

        webTestClient.get()
                .uri("/transactions/{id}", 1L)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.transactionId").isEqualTo(1L)
                .jsonPath("$.amount").isEqualTo(1000.0)
                .jsonPath("$.type").isEqualTo("deposit")
                .jsonPath("$.bankAccountId").isEqualTo(1L);
    }

    @Test
    void getTransactionByIdErrorTest() {
        when(transactionService.findById(any(Long.class)))
                .thenReturn(Mono.error(new EntityNotFoundException("Transaction with id " + 99L + " not found")));

        webTestClient.get()
                .uri("/transactions/{id}", 99L)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void getAllTransactions() {
        when(transactionService.findAll())
                .thenReturn(testUtils.createFluxTransactions());

        webTestClient.get()
                .uri("/transactions/all")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Transaction.class)
                .hasSize(5);
    }

    @Test
    void getAllTransactionsErrorTest() {
        when(transactionService.findAll())
                .thenReturn(Flux.error(new EntityNotFoundException("Transactions not found")));

        webTestClient.get()
                .uri("/transactions/all")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class)
                .value(err -> {
                    assert err.contains("Transactions not found");
                });
    }

    @Test
    void getAllTransactionsByBankAccountId() {
        when(transactionService.findAllByBankAccountId(any(Long.class)))
                .thenReturn(testUtils.createFluxTransactions());

        webTestClient.get()
                .uri("/transactions/all/{bankAccountId}", 1L)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Transaction.class)
                .hasSize(5);
    }

    @Test
    void getAllTransactionsByBankAccountIdErrorTest() {
        when(transactionService.findAllByBankAccountId(any(Long.class)))
                .thenReturn(Flux.error(new EntityNotFoundException("Transactions not found")));

        webTestClient.get()
                .uri("/transactions/all/{bankAccountId}", 1L)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class)
                .value(err -> {
                    assert err.contains("Transactions not found");
                });
    }

    @Test
    void getAllTransactionByTypeAndBanckAccountId() {
        when(transactionService.findAllByTypeAndBankAccount("deposit", 1L))
                .thenReturn(testUtils.createFluxTransactions());

        webTestClient.get()
                .uri("/transactions/{type}/{bankAccountId}", "deposit", 1L)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Transaction.class)
                .hasSize(5);
    }

    @Test
    void getAllTransactionByTypeAndBanckAccountIdErrorTest() {
        when(transactionService.findAllByTypeAndBankAccount("deposit", 1L))
                .thenReturn(Flux.error(new EntityNotFoundException("Transactions not found")));

        webTestClient.get()
                .uri("/transactions/{type}/{bankAccountId}", "deposit", 1L)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class)
                .value(err -> {
                    assert err.contains("Transactions not found");
                });
    }

    @Test
    void resumeByType() {
        when(transactionService.getResumeByType("deposit", 1L))
                .thenReturn(testUtils.totalAmount());

        webTestClient.get()
                .uri("/transactions/resume-by-type/{type}/{bankAccountId}", "deposit", 1L)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.totalAmount").isEqualTo(2000);
    }

    @Test
    void resumeByTypeErrorTest() {
        when(transactionService.getResumeByType("deposit", 1L))
                .thenReturn(Mono.error(new EntityNotFoundException("Transactions not found")));

        webTestClient.get()
                .uri("/transactions/resume-by-type/{type}/{bankAccountId}", "deposit", 1L)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class)
                .value(err -> {
                    assert err.contains("Transactions not found");
                });
    }

    @Test
    void updateTransactionSuccessTest() {
        when(transactionService.update(any(Transaction.class)))
                .thenReturn(testUtils.createSampleTransaction());

        webTestClient.put()
                .uri("/transactions")
                .contentType(APPLICATION_JSON)
                .bodyValue(FULL_PAYLOAD)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.transactionId").isEqualTo(1L)
                .jsonPath("$.amount").isEqualTo(1000.0)
                .jsonPath("$.type").isEqualTo("deposit")
                .jsonPath("$.bankAccountId").isEqualTo(1L);
    }

    @Test
    void updateTransactionNotFoundTest() {
        when(transactionService.update(any(Transaction.class)))
                .thenReturn(Mono.error(new EntityNotFoundException("Transaction not found: 99")));

        webTestClient.put()
                .uri("/transactions")
                .contentType(APPLICATION_JSON)
                .bodyValue(FULL_PAYLOAD)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class)
                .value(err -> {
                    assert err.contains("Transaction not found: 99");
                });
    }

    @Test
    void deleteTransactionTest() {
        when(transactionService.deleteById(anyLong()))
                .thenReturn(Mono.just("Transaction with id 1 deleted successfully"));

        webTestClient.delete()
                .uri("/transactions/{id}", 1L)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(err -> {
                    assert err.contains("Transaction with id 1 deleted successfully");
                });
    }

    @Test
    void deleteTransactionErrorTest() {
        when(transactionService.deleteById(anyLong()))
                .thenReturn(Mono.error(new EntityNotFoundException("Transaction with id " + 99L + " not found")));

        webTestClient.delete()
                .uri("/transactions/{id}", 99L)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class)
                .value(err -> {
                    assert err.contains("Transaction with id 99 not found");
                });
    }
}
