package com.eric.webflux.sec07;

import com.eric.webflux.sec07.dto.CalculatorResponse;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxProperties;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.http.ProblemDetail;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.test.StepVerifier;

public class Lec05ErrorHandlingTest extends AbstractWebClient {

    private static final Logger log = LoggerFactory.getLogger(Lec05ErrorHandlingTest.class);
    private final WebClient client = createWebClient();

    @Test
    public void errorHandlingTest1() throws InterruptedException {
        client.get()
                .uri("/lec05/calculator/{a}/{b}", 10, 20)
                .header("operation", "@")
                .retrieve()
                .bodyToMono(CalculatorResponse.class)
                .onErrorReturn(new CalculatorResponse(0, 0, null, 0))
                .doOnNext(print())
                .then()
                .as(StepVerifier::create)// instead of using Thread.sleep()
                .expectComplete()// test will exit when we get the complete signal
                .verify();
    }

    @Test
    public void errorHandlingTest2() throws InterruptedException {
        client.get()
                .uri("/lec05/calculator/{a}/{b}", 10, 20)
                .header("operation", "@")
                .retrieve()
                .bodyToMono(CalculatorResponse.class)
                .onErrorReturn(WebClientResponseException.BadRequest.class, new CalculatorResponse(0, 0, null, -1))
                .doOnNext(print())
                .then()
                .as(StepVerifier::create)// instead of using Thread.sleep()
                .expectComplete()// test will exit when we get the complete signal
                .verify();
    }
    @Test
    public void errorHandlingTest3() throws InterruptedException {
        client.get()
                .uri("/lec05/calculator/{a}/{b}", 10, 20)
                .header("operation", "@")
                .retrieve()
                .bodyToMono(CalculatorResponse.class)
                .doOnError(WebClientResponseException.class, ex -> log.info("{}", ex.getResponseBodyAs(ProblemDetail.class)))
                .onErrorReturn(WebClientResponseException.BadRequest.class, new CalculatorResponse(0, 0, null, -1))
                .onErrorReturn(WebClientResponseException.InternalServerError.class, new CalculatorResponse(0, 0, null, -2))
                .doOnNext(print())
                .then()
                .as(StepVerifier::create)// instead of using Thread.sleep()
                .expectComplete()// test will exit when we get the complete signal
                .verify();
    }


}
