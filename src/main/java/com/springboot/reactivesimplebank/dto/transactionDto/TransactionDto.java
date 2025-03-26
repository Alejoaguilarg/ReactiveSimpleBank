package com.springboot.reactivesimplebank.dto.transactionDto;

import com.springboot.reactivesimplebank.transaction.model.Transaction;

public record TransactionDto(String type, double amount) {}
