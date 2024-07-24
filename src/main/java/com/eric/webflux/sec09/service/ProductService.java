package com.eric.webflux.sec09.service;

import com.eric.webflux.sec09.dto.ProductDto;
import com.eric.webflux.sec09.mapper.EntityDtoMapper;
import com.eric.webflux.sec09.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private Sinks.Many<ProductDto> sink;

    public Mono<ProductDto> saveProduct(Mono<ProductDto> monoDto) {
        return monoDto
                .map(EntityDtoMapper::toEntity)
                .flatMap(productRepository::save)
                .map(EntityDtoMapper::toDto)
                .doOnNext(sink::tryEmitNext);
    }

    public Flux<ProductDto> productStream() {
        return sink.asFlux();
    }

}
