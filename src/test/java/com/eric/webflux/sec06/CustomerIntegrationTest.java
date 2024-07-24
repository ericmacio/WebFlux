package com.eric.webflux.sec06;

import com.eric.webflux.sec06.dto.CustomerDto;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@AutoConfigureWebTestClient
@SpringBootTest(properties = {
        "sec=sec06",
        "logging.level.org.springframework.r2dbc=INFO"
})
public class CustomerIntegrationTest  {

    private static final Logger log = LoggerFactory.getLogger(CustomerIntegrationTest.class);

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void getAllCustomersTest() {
        webTestClient
                .get()
                .uri("/api/v1/customers")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectHeader().contentType((MediaType.APPLICATION_JSON))
                .expectBodyList(CustomerDto.class)
                .value(list -> log.info("{}", list))
                .hasSize(10);
    }

    @Test
    public void getPaginatedCustomersTest() {
        webTestClient
                .get()
                .uri("/api/v1/customers/paginated?page=2&size=2")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectHeader().contentType((MediaType.APPLICATION_JSON))
                .expectBody()
                .consumeWith(r -> log.info("{}", new String(r.getResponseBody())))
                .jsonPath("$.length()").isEqualTo(2)
                .jsonPath("$[0].id").isEqualTo(5)
                .jsonPath("$[1].id").isEqualTo(6);

    }

    @Test
    public void getCustomerByIdTest() {
        getAndCheckCustomer(new CustomerDto(1, "sam", "sam@gmail.com"));
    }

    @Test
    public void createAndDeleteCustomerTest() {
        // create customer
        CustomerDto customerDto = new CustomerDto(null, "eric", "eric@gmail.com");
        webTestClient
                .post()
                .uri("/api/v1/customers")
                .bodyValue(customerDto)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectHeader().contentType((MediaType.APPLICATION_JSON))
                .expectBody()
                .consumeWith(r -> log.info("{}", new String(r.getResponseBody())))
                .jsonPath("$.id").isEqualTo(11)
                .jsonPath("$.name").isEqualTo("eric")
                .jsonPath("$.email").isEqualTo("eric@gmail.com");

        // delete customer
        webTestClient
                .delete()
                .uri("/api/v1/customers/11")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody().isEmpty();

        // check remaining customers
        getAllCustomersTest();
    }

    @Test
    public void updateCustomerTest() {
        // create customer
        CustomerDto customerDto = new CustomerDto(null, "eric", "eric@gmail.com");
        webTestClient
                .post()
                .uri("/api/v1/customers")
                .bodyValue(customerDto)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectHeader().contentType((MediaType.APPLICATION_JSON))
                .expectBody()
                .consumeWith(r -> log.info("{}", new String(r.getResponseBody())))
                .jsonPath("$.id").isEqualTo(11)
                .jsonPath("$.name").isEqualTo("eric")
                .jsonPath("$.email").isEqualTo("eric@gmail.com");

        // update customer
        Integer customerId = 11;
        CustomerDto newCustomerDto = new CustomerDto(null, "eric1", "eric1@gmail.com");
        webTestClient
                .put()
                .uri("/api/v1/customers/11")
                .bodyValue(newCustomerDto)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectHeader().contentType((MediaType.APPLICATION_JSON))
                .expectBody()
                .consumeWith(r -> log.info("{}", new String(r.getResponseBody())))
                .jsonPath("$.id").isEqualTo(customerId)
                .jsonPath("$.name").isEqualTo(newCustomerDto.name())
                .jsonPath("$.email").isEqualTo(newCustomerDto.email());

        getAndCheckCustomer(new CustomerDto(customerId, newCustomerDto.name(), newCustomerDto.email()));

    }

    @Test
    public void customerNotFoundTest() {
        // get
        webTestClient
                .get()
                .uri("/api/v1/customers/11")
                .exchange()
                .expectStatus().isEqualTo(404)
                .expectBody()
                .jsonPath("$.detail").isEqualTo("Customer [id=11] is not found");

        // put
        CustomerDto customerDto = new CustomerDto(null, "eric", "eric@gmail.com");
        webTestClient
                .put()
                .uri("/api/v1/customers/11")
                .bodyValue(customerDto)
                .exchange()
                .expectStatus().isEqualTo(404)
                .expectBody()
                .jsonPath("$.detail").isEqualTo("Customer [id=11] is not found");

        // delete
        webTestClient
                .delete()
                .uri("/api/v1/customers/11")
                .exchange()
                .expectStatus().isEqualTo(404)
                .expectBody()
                .jsonPath("$.detail").isEqualTo("Customer [id=11] is not found");
    }

    @Test
    public void invalidInputTest() {
        // null name
        CustomerDto customerDto1 = new CustomerDto(null, null, "eric@gmail.com");
        webTestClient
                .post()
                .uri("/api/v1/customers")
                .bodyValue(customerDto1)
                .exchange()
                .expectStatus().isEqualTo(400)
                .expectBody()
                .jsonPath("$.title").isEqualTo("Invalid input")
                .jsonPath("$.detail").isEqualTo("Name is required");

        // null email
        CustomerDto customerDto2 = new CustomerDto(null, "eric", null);
        webTestClient
                .post()
                .uri("/api/v1/customers")
                .bodyValue(customerDto2)
                .exchange()
                .expectStatus().isEqualTo(400)
                .expectBody()
                .jsonPath("$.title").isEqualTo("Invalid input")
                .jsonPath("$.detail").isEqualTo("Valid email is required");;

        // invalid email when post
        CustomerDto customerDto3 = new CustomerDto(null, "eric", "ericgmail.com");
        webTestClient
                .post()
                .uri("/api/v1/customers")
                .bodyValue(customerDto3)
                .exchange()
                .expectStatus().isEqualTo(400)
                .expectBody()
                .jsonPath("$.title").isEqualTo("Invalid input")
                .jsonPath("$.detail").isEqualTo("Valid email is required");

        // invalid email when put
        CustomerDto customerDto4 = new CustomerDto(null, "eric", "ericgmail.com");
        webTestClient
                .put()
                .uri("/api/v1/customers/10")
                .bodyValue(customerDto4)
                .exchange()
                .expectStatus().isEqualTo(400)
                .expectBody()
                .jsonPath("$.title").isEqualTo("Invalid input")
                .jsonPath("$.detail").isEqualTo("Valid email is required");;
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
