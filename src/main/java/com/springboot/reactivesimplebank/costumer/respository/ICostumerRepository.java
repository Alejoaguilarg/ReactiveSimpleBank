package com.springboot.reactivesimplebank.costumer.respository;

import com.springboot.reactivesimplebank.costumer.model.Costumer;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface ICostumerRepository extends ReactiveCrudRepository<Costumer, Long> {
    public Mono<Costumer> findByEmailAddress(String emailAddress);
}
