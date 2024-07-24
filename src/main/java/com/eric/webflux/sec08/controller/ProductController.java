package com.eric.webflux.sec08.controller;

import com.eric.webflux.sec08.dto.ProductDto;
import com.eric.webflux.sec08.dto.UploadResponse;
import com.eric.webflux.sec08.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.awt.*;
import java.util.UUID;

@RestController
@RequestMapping("/products")
public class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    ProductService productService;

    @PostMapping(value = "/upload", consumes = MediaType.APPLICATION_NDJSON_VALUE)
    public Mono<UploadResponse> uploadProducts(@RequestBody Flux<ProductDto> flux) {
        log.info("POST upload invoked");
        return productService.saveProducts(flux.doOnNext(dto -> log.info("received dto: {}", dto)))
                .then(productService.getProductsCount())
                .map(count -> new UploadResponse(UUID.randomUUID(), count));
    }

    @PostMapping(value = "/uploadBi", consumes = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<ProductDto> uploadProductsBi(@RequestBody Flux<ProductDto> flux) {
        log.info("POST upload invoked");
        return productService.saveProducts(flux.doOnNext(dto -> log.info("POST received dto: {}", dto)));
    }

    @GetMapping(value = "/download", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<ProductDto> downloadProducts() {
        return productService.getAllProducts();
    }
}
