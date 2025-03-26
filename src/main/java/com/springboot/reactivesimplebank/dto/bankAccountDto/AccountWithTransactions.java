package com.springboot.reactivesimplebank.dto.bankAccountDto;

import com.springboot.reactivesimplebank.dto.transactionDto.TransactionDto;

import java.util.List;

public record AccountWithTransactions(
        String accountNumber,
        List<TransactionDto> transactions,
        Double amount) {}
