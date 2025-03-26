package com.springboot.reactivesimplebank.costumer.controller;

import com.springboot.reactivesimplebank.dto.bankAccountDto.CustomerAccountsResponse;
import com.springboot.reactivesimplebank.costumer.model.Costumer;
import com.springboot.reactivesimplebank.costumer.service.CostumerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/costumer")
public class CostumerController {

    private final CostumerService costumerService;

    public CostumerController(CostumerService costumerService) {
        this.costumerService = costumerService;
    }

    @GetMapping("/{costumerId}")
    public Mono<Costumer> getCostumer(@PathVariable final Long costumerId) {
        return costumerService.findById(costumerId);
    }

    @GetMapping("/all")
    public Flux<Costumer> getAllCostumer() {
        return costumerService.findAll();
    }

    @PostMapping()
    public Mono<Costumer> createCostumer(@RequestBody final Costumer costumer) {
        return costumerService.save(costumer);
    }

    @PutMapping()
    public Mono<Costumer> updateCostumer(@RequestBody final Costumer costumer) {
        return costumerService.update(costumer);
    }

    @DeleteMapping("/{costumerId}")
    public Mono<String> deleteCostumer(@PathVariable final Long costumerId) {
        return costumerService.delete(costumerId);
    }

    @GetMapping("/account-details/{costumerId}")
    public Mono<CustomerAccountsResponse> accountsDetails(@PathVariable final Long costumerId) {
        return costumerService.getBankAccountResumeUserId(costumerId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NO_CONTENT)));
    }
}
