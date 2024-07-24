package com.eric.webflux.sec02.repository;

import com.eric.webflux.sec02.entity.Customer;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface CustomerRepository extends ReactiveCrudRepository<Customer, Integer> {

    Flux<Customer> findByName(String name);
    Flux<Customer> findByEmail(String email);
    Flux<Customer> findByEmailEndingWith(String email);
    Mono<Void> deleteByName(String name);
}
