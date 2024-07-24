package com.eric.webflux.sec07;

import com.eric.webflux.sec07.dto.Product;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

public class Lec08BearerAuthTest extends AbstractWebClient {

    private static final Logger log = LoggerFactory.getLogger(Lec08BearerAuthTest.class);
    private final WebClient client = createWebClient(b -> b.defaultHeaders(h -> h.setBearerAuth("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9")));

    @Test
    public void basicAuthTest() throws InterruptedException {
        client.get()
                .uri("/lec08/product/{id}", 1)
                .retrieve()
                .bodyToMono(Product.class)
                .doOnNext(print())
                .then()
                .as(StepVerifier::create)// instead of using Thread.sleep()
                .expectComplete()// test will exit when we get the complete signal
                .verify();
    }

}
