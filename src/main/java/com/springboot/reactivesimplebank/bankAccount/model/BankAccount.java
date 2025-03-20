package com.springboot.reactivesimplebank.bankAccount.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Table("bankAccount")
public class BankAccount {

    @Id
    @Column("bankAccountId")
    private Long bankAccountId;

    private final String number;

    @Column("costumerId")
    private Long costumerId;

    @Column("creationDate")
    private final LocalDateTime creationDate;

    public BankAccount() {
        this.number = UUID.randomUUID().toString();
        this.creationDate = LocalDateTime.now();
    }

    @PersistenceCreator
    public BankAccount(final Long bankAccountId,
                       final String number,
                       final Long costumerId,
                       final LocalDateTime creationDate) {
        this.bankAccountId = bankAccountId;
        this.number = number;
        this.costumerId = costumerId;
        this.creationDate = creationDate;

    }

    public BankAccount(final Long bankAccountId,
                                 final Long costumerId
                                 ) {
        this.bankAccountId = bankAccountId;
        this.number = UUID.randomUUID().toString();
        this.costumerId = costumerId;
        this.creationDate = LocalDateTime.now();
    }

    public Long getBankAccountId() {
        return bankAccountId;
    }

    public void setBankAccountId(final Long bankAccountId) {
        this.bankAccountId = bankAccountId;
    }

    public String getNumber() {
        return number;
    }

    public Long getCostumerId() {
        return costumerId;
    }
    public void setCostumerId(final Long costumerId) {
        this.costumerId = costumerId;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

}
