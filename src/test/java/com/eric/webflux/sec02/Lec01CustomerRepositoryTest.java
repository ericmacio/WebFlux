package com.eric.webflux.sec02;

import com.eric.webflux.sec02.entity.Customer;
import com.eric.webflux.sec02.repository.CustomerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;


public class Lec01CustomerRepositoryTest extends AbstractTest {

    private static final Logger log = LoggerFactory.getLogger(Lec01CustomerRepositoryTest.class);

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    public void findAll() {
        customerRepository.findAll()
                .doOnNext(c -> log.info("{}", c))
                .as(StepVerifier::create)
                .expectNextCount(10)
                .expectComplete()
                .verify();
    }

    @Test
    public void findById() {
        customerRepository.findById(2)
                .doOnNext(c -> log.info("{}", c))
                .as(StepVerifier::create)
                .assertNext(c -> Assertions.assertEquals("mike", c.getName()))
                .expectComplete()
                .verify();
    }

    @Test
    public void findByName() {
        customerRepository.findByName("sam")
                .doOnNext(c -> log.info("{}", c))
                .as(StepVerifier::create)
                .assertNext(c -> Assertions.assertEquals("sam@gmail.com", c.getEmail()))
                .expectComplete()
                .verify();
    }

    @Test
    public void findByEmail() {
        customerRepository.findByEmail("liam@example.com")
                .doOnNext(c -> log.info("{}", c))
                .as(StepVerifier::create)
                .assertNext(c -> Assertions.assertEquals("liam", c.getName()))
                .expectComplete()
                .verify();
    }

    @Test
    public void findByEmailEndingWith() {
        customerRepository.findByEmailEndingWith("ke@gmail.com")
                .doOnNext(c -> log.info("{}", c))
                .as(StepVerifier::create)
                .assertNext(c -> Assertions.assertEquals("mike@gmail.com", c.getEmail()))
                .assertNext(c -> Assertions.assertEquals("jake@gmail.com", c.getEmail()))
                .expectComplete()
                .verify();
    }

    @Test
    public void insertAndDeleteCustomer() {
        // insert
        final String name = "eric";
        Customer customer = new Customer(name, "eric@gmail.com");
        customerRepository.save(customer)
                .doOnNext(c -> log.info("Save {}", c))
                .as(StepVerifier::create)
                .assertNext(c -> Assertions.assertNotNull(c.getId()))
                .expectComplete()
                .verify();

        // check count after insert
        customerRepository.count()
                .as(StepVerifier::create)
                .expectNext(11L)
                .expectComplete()
                .verify();

        // delete user then check count after delete
        customerRepository.deleteByName(name)
                .then(customerRepository.count())
                .as(StepVerifier::create)
                .expectNext(10L)
                .expectComplete()
                .verify();
    }

    @Test
    @DisplayName("Update customer")
    public void UpdateCustomer() {
        customerRepository.findByName("ethan")
                .doOnNext(c -> log.info("{}", c))
                .flatMap(c -> {
                    c.setName("ethan1");
                    c.setEmail("ethan1@gmail.com");
                    return customerRepository.save(c);
                })
                .as(StepVerifier::create)
                .assertNext(newCustomer -> Assertions.assertEquals("ethan1", newCustomer.getName()))
                .expectComplete()
                .verify();
    }

}
