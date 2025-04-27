package com.springboot.reactivesimplebank.costumer.service;

import com.springboot.reactivesimplebank.bankAccount.service.BankAccountService;
import com.springboot.reactivesimplebank.dto.bankAccountDto.AccountWithTransactions;
import com.springboot.reactivesimplebank.dto.bankAccountDto.CustomerAccountsResponse;
import com.springboot.reactivesimplebank.costumer.model.Costumer;
import com.springboot.reactivesimplebank.costumer.respository.ICostumerRepository;
import com.springboot.reactivesimplebank.exception.customExceptions.EntityNotFoundException;
import com.springboot.reactivesimplebank.exception.customExceptions.DuplicateEntityException;
import com.springboot.reactivesimplebank.transaction.Service.TransactionService;
import com.springboot.reactivesimplebank.transaction.model.Transaction;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class CostumerService {
    private final ICostumerRepository costumerRepository;
    private final BankAccountService bankAccountService;

    public static final String NOT_FOUND_WITH_ID = " not found with id: ";
    private static final String USER_SERVICE = "[User Service] User";
    private final TransactionService transactionService;

    public CostumerService(final ICostumerRepository costumerRepository,
                           final BankAccountService bankAccountService, TransactionService transactionService) {
        this.costumerRepository = costumerRepository;
        this.bankAccountService = bankAccountService;
        this.transactionService = transactionService;
    }

    public Mono<Costumer> findById(final Long id) {
        return costumerRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(USER_SERVICE + NOT_FOUND_WITH_ID + id)));
    }

    public Flux<Costumer> findAll() {
        return costumerRepository.findAll();
    }

    public Mono<Costumer> save(final Costumer customer) {
        return costumerRepository.findByEmailAddress(customer.getEmailAddress())
                .flatMap(existingCustomer -> Mono.error(
                        new DuplicateEntityException(USER_SERVICE + " already existing.")))
                .switchIfEmpty(costumerRepository.save(customer))
                .cast(Costumer.class);
    }

    public Mono<Costumer> update(final Costumer customer) {
        return costumerRepository.findById(customer.getCostumerId())
                .switchIfEmpty(Mono.error(new EntityNotFoundException(USER_SERVICE + NOT_FOUND_WITH_ID
                        + customer.getCostumerId())))
                .map(existingCustomer -> {
                    existingCustomer.setName(customer.getName());
                    existingCustomer.setEmailAddress(customer.getEmailAddress());
                    existingCustomer.setPhoneNumber(customer.getPhoneNumber());
                    return customer;
                }).flatMap(costumerRepository::save);
    }

    public Mono<String> delete(final Long id) {
        return costumerRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(USER_SERVICE + NOT_FOUND_WITH_ID + id)))
                .flatMap(customerExisting -> costumerRepository.deleteById(customerExisting.getCostumerId()))
                .then(Mono.just("Customer with id " + id + " deleted successfully"));
    }

    public Mono<CustomerAccountsResponse> getBankAccountResumeUserId(final Long costumerId) {
        return costumerRepository.findById(costumerId)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(USER_SERVICE + NOT_FOUND_WITH_ID + costumerId)))
                .flatMap(customer -> bankAccountService
                        .findAllBankAccountsByCustomerId(customer.getCostumerId())
                        .flatMap(bankAccount -> transactionService
                                .getFullResume(bankAccount.getBankAccountId())
                                .map(totalAmount -> new AccountWithTransactions(
                                        bankAccount.getNumber(),
                                        totalAmount.getTransactions().stream().map(Transaction::from).toList(),
                                        totalAmount.getTotalAmount()
                                ))
                        )
                        .collectList()
                        .map( accounts -> {
                            double totalAmount = getSumTotal(accounts);
                            return new CustomerAccountsResponse(
                                    customer.getName(),
                                    accounts,
                                    totalAmount
                            );
                        })
                );


    }

    private static double getSumTotal(final List<AccountWithTransactions> accounts) {
        return accounts.stream()
                .mapToDouble(AccountWithTransactions::amount)
                .sum();
    }
}
