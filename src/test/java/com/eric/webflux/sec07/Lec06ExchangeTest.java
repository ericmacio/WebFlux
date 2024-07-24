package com.eric.webflux.sec07;

import com.eric.webflux.sec07.dto.CalculatorResponse;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ProblemDetail;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class Lec06ExchangeTest extends AbstractWebClient {

    private static final Logger log = LoggerFactory.getLogger(Lec06ExchangeTest.class);
    private final WebClient client = createWebClient();

    @Test
    public void exchangeTest() throws InterruptedException {
        client.get()
                .uri("/lec05/calculator/{a}/{b}", 10, 20)
                .header("operation", "@")
                .exchangeToMono(this::decode)
                .doOnNext(print())
                .then()
                .as(StepVerifier::create)// instead of using Thread.sleep()
                .expectComplete()// test will exit when we get the complete signal
                .verify();
    }

    private Mono<CalculatorResponse> decode(ClientResponse clientResponse) {
        log.info("status code: {}", clientResponse.statusCode());
        if(clientResponse.statusCode().isError()) {
            return clientResponse.bodyToMono(ProblemDetail.class)
                    .doOnNext(pd -> log.info("{}", pd))
                    .then(Mono.empty());
        }
        return  clientResponse.bodyToMono(CalculatorResponse.class);
    }

}
