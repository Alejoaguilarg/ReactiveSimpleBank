package com.springboot.reactivesimplebank.transaction;

import com.springboot.reactivesimplebank.TestUtils;
import com.springboot.reactivesimplebank.exception.handler.GlobalExceptionHandler;
import com.springboot.reactivesimplebank.transaction.Service.TransactionService;
import com.springboot.reactivesimplebank.transaction.controller.TransactionController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@WebFluxTest(TransactionController.class)
@Import(GlobalExceptionHandler.class)
public class TransactionControllerTest {

    @Autowired
    private TransactionController transactionController;

    @MockitoBean
    private TransactionService transactionService;

    private final TestUtils testUtils = new TestUtils();

    @Test
    void createTransactionTest() {

    }

    @Test
    void getTransactionByIdTest() {}
}
