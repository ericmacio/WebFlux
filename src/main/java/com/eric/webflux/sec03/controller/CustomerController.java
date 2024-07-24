package com.eric.webflux.sec03.controller;

import com.eric.webflux.sec03.dto.CustomerDto;
import com.eric.webflux.sec03.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public Flux<CustomerDto> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping("/paginated")
    public Flux<CustomerDto> getPaginatedCustomers(@RequestParam(defaultValue = "0") Integer page,
                                                   @RequestParam(defaultValue = "3") Integer size) {
        log.info("page: {}, size: {}", page, size);
        return customerService.getPaginatedCustomers(page, size);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<CustomerDto>> getCustomer(@PathVariable Integer id) {
        return customerService.getCustomerById(id)
//                .map(ResponseEntity::ok)
                .map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<CustomerDto> postCustomer(@RequestBody Mono<CustomerDto> customerDto) {
        return customerService.saveCustomer(customerDto);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<CustomerDto>> putCustomer(@PathVariable Integer id, @RequestBody Mono<CustomerDto> customerDto) {
        return customerService.updateCustomer(id, customerDto)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteCustomer(@PathVariable Integer id) {
        return customerService.deleteCustomerById(id)
                .filter(b -> b)
                .map(b -> ResponseEntity.ok().<Void>build())
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
