package com.springboot.reactivesimplebank.dto.transactionDto;

import com.springboot.reactivesimplebank.transaction.model.Transaction;

import java.util.List;

public class TotalAmount {
    private double totalAmount;
    private List<Transaction> transactions;

    public TotalAmount() {}

    public TotalAmount(final double amount, final List<Transaction> transactions) {
        this.totalAmount = amount;
        this.transactions = transactions;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTotalAmount(final double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setTransactions(final List<Transaction> transactions) {
        this.transactions = transactions;
    }
}
