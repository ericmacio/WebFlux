package com.eric.webflux.sec08;

import com.eric.webflux.sec08.dto.ProductDto;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.nio.file.Path;
import java.time.Duration;

public class ProductsUploadDownloadTest {

    private static final Logger log = LoggerFactory.getLogger(ProductsUploadDownloadTest.class);
    private final ProductClient productClient = new ProductClient();

    @Test
    public void upload() {
        Flux<ProductDto> flux = Flux.range(1, 500)
                .map(i -> new ProductDto(null, "product-" + i, i));
               // .delayElements(Duration.ofSeconds(2));
        productClient.uploadProducts(flux)
                .doOnNext(uploadResponse -> log.info("received {}", uploadResponse))
                .then()
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    @Test
    public void uploadBi() {
        Flux<ProductDto> flux = Flux.range(1, 500)
                .map(i -> new ProductDto(null, "product-" + i, i));
        // .delayElements(Duration.ofSeconds(2));
        productClient.uploadProductsBi(flux)
                .doOnNext(dto -> log.info("TEST received {}", dto))
                .then()
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    @Test
    public void download() {
        productClient.downloadProducts()
                .map(ProductDto::toString)
                .as(flux -> FileWriter.create(flux, Path.of("product.txt")))
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }
}
