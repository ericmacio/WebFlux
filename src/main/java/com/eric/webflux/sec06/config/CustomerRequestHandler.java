package com.eric.webflux.sec06.config;

import com.eric.webflux.sec06.dto.CustomerDto;
import com.eric.webflux.sec06.service.CustomerService;
import com.eric.webflux.sec06.exceptions.ApplicationExceptions;
import com.eric.webflux.sec06.validator.RequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Service
public class CustomerRequestHandler {

    @Autowired
    CustomerService customerService;

    public Mono<ServerResponse> getAllCustomers(ServerRequest request) {
        return customerService.getAllCustomers()
                .as(flux -> ServerResponse.ok().body(flux, CustomerDto.class));
    }

    public Mono<ServerResponse> getPaginatedCustomers(ServerRequest request) {
        Integer page = request.queryParam("page").map(Integer::parseInt).orElse(0);
        Integer size = request.queryParam("size").map(Integer::parseInt).orElse(3);
        return customerService.getPaginatedCustomers(page, size)
                .as(flux -> ServerResponse.ok().body(flux, CustomerDto.class));
    }

    public Mono<ServerResponse> getCustomer(ServerRequest request) {
        Integer id = Integer.parseInt(request.pathVariable("id"));
        return customerService.getCustomerById(id)
                .switchIfEmpty(ApplicationExceptions.customerNotFound(id))
                .flatMap(ServerResponse.ok()::bodyValue);
    }

    public Mono<ServerResponse> postCustomer(ServerRequest request) {
        return request.bodyToMono(CustomerDto.class)
                .transform(RequestValidator.validate())
                .as(customerService::saveCustomer)
                .flatMap(ServerResponse.ok()::bodyValue);

    }

    public Mono<ServerResponse> putCustomer(ServerRequest request) {
        Integer id = Integer.parseInt(request.pathVariable("id"));
        return request.bodyToMono(CustomerDto.class)
                .transform(RequestValidator.validate())
                .as(validatedBody -> customerService.updateCustomer(id, validatedBody))
                .switchIfEmpty(ApplicationExceptions.customerNotFound(id))
                .flatMap(ServerResponse.ok()::bodyValue);

    }

    public Mono<ServerResponse> deleteCustomer(ServerRequest request) {
        Integer id = Integer.parseInt(request.pathVariable("id"));
        return customerService.deleteCustomerById(id)
                .filter(b -> b)
                .switchIfEmpty(ApplicationExceptions.customerNotFound(id))
                .then(ServerResponse.ok().build());
    }

}
