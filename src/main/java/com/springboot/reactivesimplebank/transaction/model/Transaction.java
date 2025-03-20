package com.springboot.reactivesimplebank.transaction.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table
public class Transaction {

    @Id
    private Long transactionId;

    private String type;

    private Long bankAccountId;

    @Column("value")
    private Long amount;

    private final LocalDateTime creationDate;

    public Transaction() {
        this.creationDate = LocalDateTime.now();
    }

    @PersistenceCreator
    public Transaction(
            final Long transactionId,
            final String type,
            final Long bankAccountId,
            final Long amount,
            final LocalDateTime creationDate) {
        this.transactionId = transactionId;
        this.type = type;
        this.bankAccountId = bankAccountId;
        this.amount = amount;
        this.creationDate = creationDate;
    }

    public Transaction(
            final Long transactionId,
            final String type,
            final Long bankAccountId,
            final Long amount) {
        this.transactionId = transactionId;
        this.type = type;
        this.bankAccountId = bankAccountId;
        this.amount = amount;
        this.creationDate = LocalDateTime.now();
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public String getType() {
        return type;
    }

    public Long getBankAccountId() {
        return bankAccountId;
    }

    public Long getAmount() {
        return amount;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setAmount(final Long amount) {
        this.amount=amount;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public void setBankAccountId(final Long bankAccountId) {
        this.bankAccountId = bankAccountId;
    }
}
