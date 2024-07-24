package com.eric.webflux.sec08.mapper;

import com.eric.webflux.sec08.dto.ProductDto;
import com.eric.webflux.sec08.entity.Product;

public class EntityDtoMapper {

    public static Product toEntity(ProductDto dto) {
        return new Product(dto.id(), dto.description(), dto.price());
    }

    public static ProductDto toDto(Product product) {
        return new ProductDto(product.getId(), product.getDescription(), product.getPrice());
    }
}
