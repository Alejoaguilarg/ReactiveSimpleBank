package com.springboot.reactivesimplebank.costumer.service;

import com.springboot.reactivesimplebank.costumer.model.Costumer;
import com.springboot.reactivesimplebank.costumer.respository.ICostumerRepository;
import com.springboot.reactivesimplebank.exception.customExceptions.CostumerNotFoundException;
import com.springboot.reactivesimplebank.exception.customExceptions.DuplicateUserException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CostumerService {
    public static final String NOT_FOUND_WITH_ID = " not found with id: ";
    private final ICostumerRepository costumerRepository;
    private static final String USER_SERVICE = "[User Service] User";

    public CostumerService(final ICostumerRepository costumerRepository) {
        this.costumerRepository = costumerRepository;
    }

    public Mono<Costumer> findById(final Long id) {
        return costumerRepository.findById(id)
                .switchIfEmpty(Mono.error(new CostumerNotFoundException(USER_SERVICE + NOT_FOUND_WITH_ID + id)));
    }

    public Flux<Costumer> findAll() {
        return costumerRepository.findAll();
    }

    public Mono<Costumer> save(final Costumer costumer) {
        return costumerRepository.findByEmailAddress(costumer.getEmailAddress())
                .flatMap(existingCostumer -> Mono.error(
                        new DuplicateUserException(USER_SERVICE + " already existing.")))
                .switchIfEmpty(costumerRepository.save(costumer))
                .cast(Costumer.class);
    }

    public Mono<Costumer> update(final Costumer costumer) {
        return costumerRepository.findById(costumer.getCostumerId())
                .switchIfEmpty(Mono.error(new CostumerNotFoundException(USER_SERVICE + NOT_FOUND_WITH_ID
                        + costumer.getCostumerId())))
                .map(existingCostumer -> {
                    existingCostumer.setName(costumer.getName());
                    existingCostumer.setEmailAddress(costumer.getEmailAddress());
                    existingCostumer.setPhoneNumber(costumer.getPhoneNumber());
                    return costumer;
                }).flatMap(costumerRepository::save);
    }

    public Mono<String> delete(final Long id) {
        return costumerRepository.findById(id)
                .switchIfEmpty(Mono.error(new CostumerNotFoundException(USER_SERVICE + NOT_FOUND_WITH_ID + id)))
                .flatMap(costumerExisting -> costumerRepository.deleteById(costumerExisting.getCostumerId()))
                .then(Mono.just("Costumer with id " + id + " deleted successfully"));
    }
}
