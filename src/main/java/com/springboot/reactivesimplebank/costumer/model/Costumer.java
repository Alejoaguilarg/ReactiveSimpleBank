package com.springboot.reactivesimplebank.costumer.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table
public class Costumer {

    @Id
    @Column("costumerId")
    private Long costumerId;
    private String name;
    @Column("phoneNumber")
    private String phoneNumber;
    @Column("emailAddress")
    private String emailAddress;

    public Costumer() {}

    public Costumer(
            final Long CostumerId,
            final String name,
            final String phoneNumber,
            final String emailAddress) {
        this.costumerId = CostumerId;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
    }

    public Long getCostumerId() {
        return costumerId;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setPhoneNumber(final String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setEmailAddress(final String emailAddress) {
        this.emailAddress = emailAddress;
    }

}
