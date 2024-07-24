package com.eric.webflux.sec03.service;

import com.eric.webflux.sec03.dto.CustomerDto;
import com.eric.webflux.sec03.mapper.EntityDtoMapper;
import com.eric.webflux.sec03.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public Flux<CustomerDto> getAllCustomers() {
        return customerRepository
                .findAll()
                .map(EntityDtoMapper::toDto);
    }

    public Flux<CustomerDto> getPaginatedCustomers(Integer page, Integer size) {
        return customerRepository
                .findBy(PageRequest.of(page, size))
                .map(EntityDtoMapper::toDto);
    }

    public Mono<CustomerDto> getCustomerById(Integer id) {
        return customerRepository
                .findById(id)
                .map(EntityDtoMapper::toDto); // convert Mono entity to Mono dto
    }

    public Mono<CustomerDto> saveCustomer(Mono<CustomerDto> monoCustomerDto) {
        return monoCustomerDto
                .map(EntityDtoMapper::toEntity)
                .flatMap(customerRepository::save)
                .map(EntityDtoMapper::toDto);
    }

    public Mono<CustomerDto> updateCustomer(Integer id, Mono<CustomerDto> monoCustomerDto) {
        return customerRepository
                .findById(id)
                .flatMap(entity -> monoCustomerDto) // customer exists. switch from entity to mono dto
                .map(EntityDtoMapper::toEntity) // convert Mono dto to Mono entity
                .doOnNext(c -> c.setId(id)) // this is safe ...
                .flatMap(customerRepository::save)
                .map(EntityDtoMapper::toDto);
    }

    public Mono<Boolean> deleteCustomerById(Integer id) {
        return customerRepository.deleteCustomerById(id);
    }

}
