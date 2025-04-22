package com.springboot.reactivesimplebank.customer;

import com.springboot.reactivesimplebank.TestUtils;
import com.springboot.reactivesimplebank.costumer.controller.CostumerController;
import com.springboot.reactivesimplebank.costumer.model.Costumer;
import com.springboot.reactivesimplebank.costumer.service.CostumerService;
import com.springboot.reactivesimplebank.dto.bankAccountDto.CustomerAccountsResponse;
import com.springboot.reactivesimplebank.exception.customExceptions.EntityNotFoundException;
import com.springboot.reactivesimplebank.exception.handler.GlobalExceptionHandler;
import com.springboot.reactivesimplebank.exception.model.ApiErrorDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(CostumerController.class)
@Import(GlobalExceptionHandler.class)
class CostumerControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private CostumerService costumerService;

    private final TestUtils testUtils = new TestUtils();

    @Test
    void createCostumerTest() {
        when(costumerService.save(any(Costumer.class)))
                .thenReturn(Mono.just(testUtils.testCostumerWithId(1L)));

        String payload = """
                {
                    "name": "Luisa",
                    "phoneNumber": "123456789",
                    "emailAddress": "<EMAIL>"
                }
                """;

        webTestClient.post()
                .uri("/costumer")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.costumerId").isEqualTo(1)
                .jsonPath("$.name").isEqualTo("Luisa")
                .jsonPath("$.phoneNumber").isEqualTo("123456789")
                .jsonPath("$.emailAddress").isEqualTo("<EMAIL>");
    }

    @Test
    void getCostumerByIdTest() {
        when(costumerService.findById(any(Long.class)))
                .thenReturn(Mono.just(testUtils.testCostumerWithId(1L)));

        webTestClient.get()
                .uri("/costumer/{id}", 1L)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.costumerId").isEqualTo(1)
                .jsonPath("$.name").isEqualTo("Luisa")
                .jsonPath("$.phoneNumber").isEqualTo("123456789")
                .jsonPath("$.emailAddress").isEqualTo("<EMAIL>");
    }

    @Test
    void getCostumerByIdErrorTest() {
        when(costumerService.findById(99L))
                .thenReturn(Mono.error(new EntityNotFoundException("Customer with id " + 99L + " not found")));

        webTestClient.get()
                .uri("/costumer/{id}", 99L)
                .exchange()
                .expectStatus().isNotFound();

    }

    @Test
    void getAllCostumersTest() {
        when(costumerService.findAll())
                .thenReturn(testUtils.getFluxTestCostumer());

        webTestClient.get()
                .uri("/costumer/all")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$[0].costumerId").isEqualTo(1)
                .jsonPath("$[0].name").isEqualTo("Luisa")
                .jsonPath("$[0].phoneNumber").isEqualTo("123456789")
                .jsonPath("$[0].emailAddress").isEqualTo("<EMAIL>")
                .jsonPath("$[1].costumerId").isEqualTo(2)
                .jsonPath("$[1].phoneNumber").isEqualTo("123456789")
                .jsonPath("$[1].emailAddress").isEqualTo("<EMAIL>");
    }

    @Test
    void getAllCostumersEmptyTest() {
        when(costumerService.findAll()).thenReturn(Flux.empty());

        webTestClient.get()
                .uri("/costumer/all")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Costumer.class)
                .hasSize(0);
    }


    @Test
    void updateCostumerSuccessTest() {
        Costumer updated = new Costumer(1L, "Nuevo Nombre", "3217654321", "nuevo@correo.com");

        when(costumerService.update(any(Costumer.class)))
                .thenReturn(Mono.just(updated));

        String payload = """
                {
                  "name": "Nuevo Nombre",
                  "phoneNumber": "3217654321",
                  "emailAddress": "nuevo@correo.com"
                }
                """;

        webTestClient.put()
                .uri("/costumer/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_JSON);
    }

    @Test
    void updateCostumerNotFoundTest() {
        when(costumerService.update(any(Costumer.class)))
                .thenReturn(Mono.error(new EntityNotFoundException("Costumer not found: 99")));

        String payload = """
                {
                  "name": "Nombre",
                  "phoneNumber": "3000000000",
                  "emailAddress": "x@x.com"
                }
                """;

        webTestClient.put()
                .uri("/costumer")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ApiErrorDto.class)
                .value(err -> {
                    assertThat(err.getMessage()).contains("Costumer not found: 99");
                    assertThat(err.getStatus()).isEqualTo(404);
                });
    }

    @Test
    void deleteCostumerSuccessTest() {
        when(costumerService.delete(any(Long.class)))
                .thenReturn(Mono.just("Customer with id 1 deleted successfully"));

        webTestClient.delete()
                .uri("/costumer/{id}", 1L)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(err -> assertThat(err).isEqualTo("Customer with id 1 deleted successfully"));
    }

    @Test
    void deleteCostumerNotFoundTest() {
        when(costumerService.delete(any(Long.class)))
                .thenReturn(Mono.error(new EntityNotFoundException("Costumer not found: 99")));

        webTestClient.delete()
                .uri("/costumer/{id}", 99L)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ApiErrorDto.class)
                .value(err -> {
                    assertThat(err.getMessage()).contains("Costumer not found: 99");
                    assertThat(err.getStatus()).isEqualTo(404);
                });
    }

    @Test
    void accountDetailsTest() {
        CustomerAccountsResponse expected = testUtils.CustomerAccountsResponse();

        when(costumerService.getBankAccountResumeUserId(1L))
                .thenReturn(Mono.just(expected));

        webTestClient.get()
                .uri("/costumer/account-details/{id}", 1L)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.customerName").isEqualTo("Luisa")
                .jsonPath("$.totalAmount").isEqualTo(4500.0)
                .jsonPath("$.accounts.length()").isEqualTo(2)
                .jsonPath("$.accounts[0].accountNumber").isEqualTo("23423423")
                .jsonPath("$.accounts[0].amount").isEqualTo(3500.0)
                .jsonPath("$.accounts[0].transactions.length()").isEqualTo(4)
                .jsonPath("$.accounts[0].transactions[0].type")
                .isEqualTo("deposit")
                .jsonPath("$.accounts[0].transactions[0].amount").isEqualTo(2300.0)
                .jsonPath("$.accounts[1].accountNumber").isEqualTo("78932324")
                .jsonPath("$.accounts[1].amount").isEqualTo(500.0);
    }

    @Test
    void accountDetailsNotFoundTest() {
        when(costumerService.getBankAccountResumeUserId(99L))
                .thenReturn(Mono.error(new EntityNotFoundException("Customer with id " + 99L + " not found")));

        webTestClient.get()
                .uri("/costumer/account-details/{id}", 99L)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ApiErrorDto.class)
                .value(err -> {
                    assertThat(err.getMessage()).contains("Customer with id " + 99L + " not found");
                    assertThat(err.getStatus()).isEqualTo(404);
                });
    }
}
