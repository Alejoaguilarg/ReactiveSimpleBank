package com.springboot.reactivesimplebank.customer;

import com.springboot.reactivesimplebank.TestUtils;
import com.springboot.reactivesimplebank.costumer.model.Costumer;
import com.springboot.reactivesimplebank.costumer.respository.ICostumerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataR2dbcTest
@ActiveProfiles("test")
class CostumerRepositoryTest {

    @Autowired
    private ICostumerRepository costumerRepository;

    @BeforeEach
    void clean() { costumerRepository.deleteAll().block(); }

    private final TestUtils testUtils = new TestUtils();

    @Test
    void saveAndFindById() {
        final Costumer costumer = testUtils.testCostumer();

        StepVerifier.create(costumerRepository.save(costumer))
                .assertNext(saved -> {
                    assertNotNull(saved.getCostumerId());
                    assertEquals("Test", saved.getName());
                })
                .verifyComplete();

        Long id = costumerRepository.findAll().blockFirst().getCostumerId();

        StepVerifier.create(costumerRepository.findById(id))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void deleteById() {
        final Costumer costumer = testUtils.testCostumer();

        Long id = costumerRepository.save(costumer)
                .map(Costumer::getCostumerId)
                .block();

        StepVerifier.create(costumerRepository.deleteById(id)
                .then(costumerRepository.findById(id)))
                .expectNextCount(0)
                .verifyComplete();
    }
}
