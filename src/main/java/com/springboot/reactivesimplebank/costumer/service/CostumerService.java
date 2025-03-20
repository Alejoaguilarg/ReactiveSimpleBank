package com.springboot.reactivesimplebank.costumer.service;

import com.springboot.reactivesimplebank.costumer.model.Costumer;
import com.springboot.reactivesimplebank.costumer.respository.ICostumerRepository;
import com.springboot.reactivesimplebank.exception.customExceptions.EntityNotFoundException;
import com.springboot.reactivesimplebank.exception.customExceptions.DuplicateEntityException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CostumerService {
    private final ICostumerRepository costumerRepository;

    public static final String NOT_FOUND_WITH_ID = " not found with id: ";
    private static final String USER_SERVICE = "[User Service] User";

    public CostumerService(final ICostumerRepository costumerRepository) {
        this.costumerRepository = costumerRepository;
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
}
