package com.springboot.reactivesimplebank.exception.handler;

import com.springboot.reactivesimplebank.exception.customExceptions.EntityNotFoundException;
import com.springboot.reactivesimplebank.exception.customExceptions.DuplicateEntityException;
import com.springboot.reactivesimplebank.exception.model.ApiErrorDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(DuplicateEntityException.class)
    public Mono<ResponseEntity<ApiErrorDto>> handleDuplicateCostumerException(final DuplicateEntityException e,
                                                                           final ServerWebExchange exchange) {
        log.error(e.getMessage());
        return Mono.just(
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(
                                new ApiErrorDto(
                                        e.getMessage(),
                                        HttpStatus.NOT_FOUND.value(),
                                        LocalDateTime.now(),
                                        exchange.getRequest().getPath().toString()
                                )
                        )
        );
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public Mono<ResponseEntity<ApiErrorDto>> handleEntityNotFoundException(final EntityNotFoundException e,
                                                                              final ServerWebExchange exchange
    ) {
        log.error("[User Service] User not found with id: {}", e.getMessage());
        return Mono.just(
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(
                                new ApiErrorDto(
                                        e.getMessage(),
                                        HttpStatus.NOT_FOUND.value(),
                                        LocalDateTime.now(),
                                        exchange.getRequest().getPath().toString()
                                )
                        )
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<ApiErrorDto>> handleIllegalArgumentException(final IllegalArgumentException e,
                                                                             final ServerWebExchange exchange) {
        log.error("[User Service] Illegal argument: {}", e.getMessage());
        return Mono.just(
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(
                                new ApiErrorDto(
                                        e.getMessage(),
                                        HttpStatus.NOT_FOUND.value(),
                                        LocalDateTime.now(),
                                        exchange.getRequest().getPath().toString()
                                )
                        )
        );
    }

    @ExceptionHandler(Throwable.class)
    public Mono<ResponseEntity<ApiErrorDto>> handleUnexpectedException(final Exception e,
                                                                        final ServerWebExchange exchange) {
        log.error("[User Service] Unexpected exception: {}", e.getMessage());
        return Mono.just(
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(
                                new ApiErrorDto(
                                        e.getMessage(),
                                        HttpStatus.NOT_FOUND.value(),
                                        LocalDateTime.now(),
                                        exchange.getRequest().getPath().toString()
                                )
                        )
        );
    }
}
