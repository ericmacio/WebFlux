package com.eric.webflux.sec09.repository;

import com.eric.webflux.sec09.entity.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ProductRepository extends ReactiveCrudRepository<Product, Integer> {

    Flux<Product> findByPriceBetween(Integer from, Integer to);
    Flux<Product> findBy(Pageable pageable);

}
