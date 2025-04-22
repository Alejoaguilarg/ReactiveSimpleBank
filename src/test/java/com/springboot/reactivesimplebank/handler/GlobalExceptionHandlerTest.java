package com.springboot.reactivesimplebank.handler;

import com.springboot.reactivesimplebank.exception.customExceptions.DuplicateEntityException;
import com.springboot.reactivesimplebank.exception.customExceptions.EntityNotFoundException;
import com.springboot.reactivesimplebank.exception.handler.GlobalExceptionHandler;
import com.springboot.reactivesimplebank.exception.model.ApiErrorDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private ServerWebExchange exchange;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();

        MockServerHttpRequest request = MockServerHttpRequest
                .get("api/test/foo")
                .build();

        exchange = MockServerWebExchange.from(request);

    }


    @Test
    void handleDuplicateEntityExceptionReturns409() {
        DuplicateEntityException ex =
                new DuplicateEntityException("Dup error");

        StepVerifier.create(handler.handleDuplicateCostumerException(ex, exchange))
                .assertNext(resp -> {
                    assertEquals(404, resp.getStatusCodeValue());
                    ApiErrorDto err = resp.getBody();
                    assertNotNull(err);
                    assertEquals("Dup error", err.getMessage());
                    assertEquals(404, err.getStatus());
                    assertTrue(err.getPath().endsWith("api/test/foo"));
                    assertNotNull(err.getTimestamp());
                })
                .verifyComplete();
    }

    @Test
    void handleEntityNotFoundException_returns404() {
        EntityNotFoundException ex =
                new EntityNotFoundException("Not found");

        StepVerifier.create(handler.handleEntityNotFoundException(ex, exchange))
                .assertNext(resp -> {
                    assertEquals(404, resp.getStatusCodeValue());
                    ApiErrorDto err = resp.getBody();
                    assertNotNull(err);
                    assertEquals("Not found", err.getMessage());
                })
                .verifyComplete();
    }

    @Test
    void handleIllegalArgumentException_returns404() {
        IllegalArgumentException ex =
                new IllegalArgumentException("Bad arg");

        StepVerifier.create(handler.handleIllegalArgumentException(ex, exchange))
                .assertNext(resp -> {
                    assertEquals(404, resp.getStatusCodeValue());
                    assertEquals("Bad arg", resp.getBody().getMessage());
                })
                .verifyComplete();
    }

    @Test
    void handleUnexpectedException_returns404() {
        RuntimeException ex = new RuntimeException("Oops");

        StepVerifier.create(handler.handleUnexpectedException(ex, exchange))
                .assertNext(resp -> {
                    assertEquals(404, resp.getStatusCodeValue());
                    assertEquals("Oops", resp.getBody().getMessage());
                })
                .verifyComplete();
    }
}
