package com.eric.webflux.sec05;

import com.eric.webflux.sec05.dto.CustomerDto;
import com.eric.webflux.sec05.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@AutoConfigureWebTestClient
@SpringBootTest(properties = {
        "sec=sec05",
        "logging.level.org.springframework.r2dbc=INFO"
})
public class CustomerIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(CustomerIntegrationTest.class);

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void noTokenTest() {
        webTestClient
                .get()
                .uri("/api/v1/customers")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void unAuthorizedTest() {
        webTestClient
                .get()
                .uri("/api/v1/customers")
                .header("X-auth-token", "badToken")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void standardTokenAuthorizedTest() {
        webTestClient
                .get()
                .uri("/api/v1/customers")
                .header("X-auth-token", "secret123")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectHeader().contentType((MediaType.APPLICATION_JSON))
                .expectBodyList(CustomerDto.class)
                .value(list -> log.info("{}", list))
                .hasSize(10);
    }

    @Test
    public void standardTokenForbiddenTest() {
        // create customer
        CustomerDto customerDto = new CustomerDto(null, "eric", "eric@gmail.com");
        webTestClient
                .post()
                .uri("/api/v1/customers")
                .header("X-auth-token", "secret123")
                .bodyValue(customerDto)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void primeTokenAuthorizedTest() {
        // create customer
        CustomerDto customerDto = new CustomerDto(null, "eric", "eric@gmail.com");
        webTestClient
                .post()
                .uri("/api/v1/customers")
                .bodyValue(customerDto)
                .header("X-auth-token", "secret456")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectHeader().contentType((MediaType.APPLICATION_JSON))
                .expectBody()
                .consumeWith(r -> log.info("{}", new String(r.getResponseBody())))
                .jsonPath("$.id").isEqualTo(11)
                .jsonPath("$.name").isEqualTo("eric")
                .jsonPath("$.email").isEqualTo("eric@gmail.com");
    }

    private void getAndCheckCustomer(CustomerDto customerDto) {
        webTestClient
                .get()
                .uri("/api/v1/customers/" + customerDto.id())
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectHeader().contentType((MediaType.APPLICATION_JSON))
                .expectBody()
                .consumeWith(r -> log.info("{}", new String(r.getResponseBody())))
                .jsonPath("$.id").isEqualTo(customerDto.id())
                .jsonPath("$.name").isEqualTo(customerDto.name())
                .jsonPath("$.email").isEqualTo(customerDto.email());
    }

}
