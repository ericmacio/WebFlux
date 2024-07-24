package com.eric.webflux.sec07;

import com.eric.webflux.sec07.dto.CalculatorResponse;
import com.eric.webflux.sec07.dto.Product;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ProblemDetail;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Map;

public class Lec07QueryParamTest extends AbstractWebClient {

    private static final Logger log = LoggerFactory.getLogger(Lec07QueryParamTest.class);
    private final WebClient client = createWebClient();

    @Test
    public void uriBuilderTest() throws InterruptedException {
        String path = "/lec06/calculator";
        String query = "first={first}&second={second}&operation={operation}";
        client.get()
                .uri(builder -> builder.path(path).query(query).build(10, 20, "+"))
                .retrieve()
                .bodyToMono(CalculatorResponse.class)
                .doOnNext(print())
                .then()
                .as(StepVerifier::create)// instead of using Thread.sleep()
                .expectComplete()// test will exit when we get the complete signal
                .verify();
    }

    @Test
    public void uriBuilderMapTest() throws InterruptedException {
        String path = "/lec06/calculator";
        String query = "first={first}&second={second}&operation={operation}";
        var map = Map.of(
                "first", 10,
                "second", 20,
                "operation", "+"
        );
        client.get()
                .uri(builder -> builder.path(path).query(query).build(map))
                .retrieve()
                .bodyToMono(CalculatorResponse.class)
                .doOnNext(print())
                .then()
                .as(StepVerifier::create)// instead of using Thread.sleep()
                .expectComplete()// test will exit when we get the complete signal
                .verify();
    }
}
