package com.springboot.reactivesimplebank.bankAccount.service;

import com.springboot.reactivesimplebank.bankAccount.model.BankAccount;
import com.springboot.reactivesimplebank.bankAccount.repository.IBankAccountRepository;
import com.springboot.reactivesimplebank.costumer.respository.ICostumerRepository;
import com.springboot.reactivesimplebank.exception.customExceptions.EntityNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class BankAccountService {

    private static final String BANK_SERVICE_STRING = "[Bank Service]";
    final IBankAccountRepository bankAccountRepository;
    final ICostumerRepository costumerRepository;

    private static final String BANK_ACCOUNT_NOT_FOUND = "Bank account with id %s not found";


    public BankAccountService(final IBankAccountRepository bankAccountRepository,
                              final ICostumerRepository costumerRepository) {
        this.bankAccountRepository = bankAccountRepository;
        this.costumerRepository = costumerRepository;
    }

    public Mono<BankAccount> findById(final Long id) {
        return bankAccountRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(String.format(BANK_SERVICE_STRING
                        + BANK_ACCOUNT_NOT_FOUND, id))));
    }

    public Flux<BankAccount> findAllBankAccounts() {
        return bankAccountRepository.findAll();
    }

    public Mono<BankAccount> save(final BankAccount bankAccount) {
        return costumerRepository.findById(bankAccount.getCostumerId())
               .switchIfEmpty(Mono.error(new EntityNotFoundException(String.format(
                       BANK_SERVICE_STRING + " It's no possible create account, user with id: %s no found",
                       bankAccount.getCostumerId()
               ))))
                .flatMap(costumer ->  bankAccountRepository.save(bankAccount));
    }

//    public Mono<BankAccount> update(final BankAccount bankAccount) {
//        return bankAccountRepository.findById(bankAccount.getBankAccountId())
//                .switchIfEmpty(Mono.error(new EntityNotFoundException(String.format(
//                        BANK_ACCOUNT_NOT_FOUND, bankAccount.getBankAccountId()
//                )))).map(accountToUpdate -> {
//                    accountToUpdate.setBankAccountId(bankAccount.getBankAccountId());
//                    accountToUpdate.setCostumerId(bankAccount.getCostumerId());
//                    return accountToUpdate;
//                }).flatMap(bankAccountRepository::save);
//    }

    public Mono<String> deleteById(final Long id) {
        return bankAccountRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(String.format(BANK_ACCOUNT_NOT_FOUND, id))))
                .flatMap(bankAccount -> bankAccountRepository.deleteById(id))
                .then(Mono.just(BANK_SERVICE_STRING + "Bank account with id " + id + " deleted successfully"));
    }
}
