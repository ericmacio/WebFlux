package com.eric.webflux.sec07;

import com.eric.webflux.sec07.dto.Product;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.support.PropertyProvider;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

public class Lec03PostTest extends AbstractWebClient {

    private final WebClient client = createWebClient();

    @Test
    public void postBodyValueTest() throws InterruptedException {
        Product product = new Product(null, "iphone", 1000);
        client.post()
                .uri("/lec03/product")
                .bodyValue(product)
                .retrieve()
                .bodyToMono(Product.class)
                .take(Duration.ofSeconds(3))
                .doOnNext(print())
                .then()
                .as(StepVerifier::create)// instead of using Thread.sleep()
                .expectComplete()// test will exit when we get the complete signal
                .verify();
    }

    @Test
    public void postBodyTest() throws InterruptedException {
        Mono<Product> monoProduct = Mono.fromSupplier(() -> new Product(null, "iphone", 1000))
                        .delayElement(Duration.ofSeconds(1));
        client.post()
                .uri("/lec03/product")
                .body(monoProduct, Product.class)
                .retrieve()
                .bodyToMono(Product.class)
                .take(Duration.ofSeconds(3))
                .doOnNext(print())
                .then()
                .as(StepVerifier::create)// instead of using Thread.sleep()
                .expectComplete()// test will exit when we get the complete signal
                .verify();
    }

}
