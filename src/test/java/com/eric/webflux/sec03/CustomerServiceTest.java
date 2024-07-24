package com.eric.webflux.sec03;

import com.eric.webflux.sec03.dto.CustomerDto;
import com.eric.webflux.sec03.service.CustomerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


public class CustomerServiceTest extends AbstractTest {

    private static final Logger log = LoggerFactory.getLogger(CustomerServiceTest.class);

    @Autowired
    private CustomerService customerService;

    @Test
    public void findAll() {
        customerService.getAllCustomers()
                .doOnNext(dto -> log.info("{}", dto))
                .as(StepVerifier::create)
                .expectNextCount(10)
                .expectComplete()
                .verify();
    }

    @Test
    public void findById() {
        customerService.getCustomerById(2)
                .doOnNext(dto -> log.info("{}", dto))
                .as(StepVerifier::create)
                .assertNext(dto -> Assertions.assertEquals("mike", dto.name()))
                .expectComplete()
                .verify();
    }

    @Test
    public void createCustomer() {
        // insert
        final String name = "eric";
        CustomerDto customerDto = new CustomerDto(null, name, "eric@gmail.com");
        customerService.saveCustomer(Mono.just(customerDto))
                .doOnNext(dto -> log.info("Save {}", dto))
                .as(StepVerifier::create)
                .assertNext(dto -> Assertions.assertNotNull(dto.id()))
                .expectComplete()
                .verify();
    }

    @Test
    public void UpdateCustomer() {
        customerService.getCustomerById(2)
                .doOnNext(dto -> log.info("{}", dto))
                .flatMap(dto -> {
                    CustomerDto customerDto = new CustomerDto(dto.id(), "mike1", "mike1@gmail.com");
                    return customerService.saveCustomer(Mono.just(customerDto));
                })
                .as(StepVerifier::create)
                .assertNext(newDto -> Assertions.assertEquals("mike1", newDto.name()))
                .expectComplete()
                .verify();
    }

}
