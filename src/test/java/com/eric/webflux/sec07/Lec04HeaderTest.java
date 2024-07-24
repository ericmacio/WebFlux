package com.eric.webflux.sec07;

import com.eric.webflux.sec07.dto.Product;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Map;

public class Lec04HeaderTest extends AbstractWebClient {

    private final WebClient client = createWebClient(b -> b.defaultHeader("caller-id", "order-service"));

    @Test
    public void defaultHeaderTest() throws InterruptedException {
        client.get()
                .uri("/lec04/product/{id}", 1)
                .retrieve()
                .bodyToMono(Product.class)
                .doOnNext(print())
                .then()
                .as(StepVerifier::create)// instead of using Thread.sleep()
                .expectComplete()// test will exit when we get the complete signal
                .verify();
    }

    @Test
    public void overrideHeaderTest() throws InterruptedException {
        client.get()
                .uri("/lec04/product/{id}", 1)
                .header("caller-id", "new-value")
                .retrieve()
                .bodyToMono(Product.class)
                .doOnNext(print())
                .then()
                .as(StepVerifier::create)// instead of using Thread.sleep()
                .expectComplete()// test will exit when we get the complete signal
                .verify();
    }

    @Test
    public void headersWithMapTest() throws InterruptedException {
        Map<String, String> map = Map.of(
                "caller-id", "new-value",
                "some-key", "some-value"
        );
        client.get()
                .uri("/lec04/product/{id}", 1)
                .headers(h -> h.setAll(map))
                .retrieve()
                .bodyToMono(Product.class)
                .doOnNext(print())
                .then()
                .as(StepVerifier::create)// instead of using Thread.sleep()
                .expectComplete()// test will exit when we get the complete signal
                .verify();
    }

}
