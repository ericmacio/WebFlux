package com.eric.webflux.sec05.controller;

import com.eric.webflux.sec05.dto.CustomerDto;
import com.eric.webflux.sec05.exceptions.ApplicationExceptions;
import com.eric.webflux.sec05.filter.Category;
import com.eric.webflux.sec05.service.CustomerService;
import com.eric.webflux.sec05.validator.RequestValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private static final Logger log = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    CustomerService customerService;

    @GetMapping
    public Flux<CustomerDto> getAllCustomers(@RequestAttribute("category") Category category) {
        log.info("user category: {}", category);
        return customerService.getAllCustomers();
    }

    @GetMapping("/paginated")
    public Flux<CustomerDto> getPaginatedCustomers(@RequestParam(defaultValue = "0") Integer page,
                                                   @RequestParam(defaultValue = "3") Integer size) {
        return customerService.getPaginatedCustomers(page, size);
    }

    @GetMapping("/{id}")
    public Mono<CustomerDto> getCustomer(@PathVariable Integer id) {
        return customerService.getCustomerById(id)
                .switchIfEmpty(ApplicationExceptions.customerNotFound(id));
    }

    @PostMapping
    public Mono<CustomerDto> postCustomer(@RequestBody Mono<CustomerDto> customerDtoMono) {
//        Mono<CustomerDto> validatedMono = customerDtoMono.transform(RequestValidator.validate());
//        return customerService.saveCustomer(validatedMono);
        return customerDtoMono.transform(RequestValidator.validate())
                .as(customerService::saveCustomer);
    }

    @PutMapping("/{id}")
    public Mono<CustomerDto> putCustomer(@PathVariable Integer id, @RequestBody Mono<CustomerDto> customerDtoMono) {
        return customerDtoMono.transform(RequestValidator.validate())
                .as(validMono -> customerService.updateCustomer(id, validMono))
                .switchIfEmpty(ApplicationExceptions.customerNotFound(id));
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteCustomer(@PathVariable Integer id) {
        return customerService.deleteCustomerById(id)
                .filter(b -> b)
                .switchIfEmpty(ApplicationExceptions.customerNotFound(id))
                .then();
    }
}
