package com.eric.webflux.sec02.services;

import com.eric.webflux.sec02.entity.Product;
import com.eric.webflux.sec02.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class ProductServiceImpl implements ProductService{

    @Autowired
    ProductRepository productRepository;

}
