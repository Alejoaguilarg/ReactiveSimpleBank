package com.springboot.reactivesimplebank.dto.bankAccountDto;

import java.util.List;

public record CustomerAccountsResponse(
        String customerName,
        List<AccountWithTransactions> accounts,
        Double totalAmount
) {}
