package com.eric.webflux.sec02.entity;

import org.springframework.data.annotation.Id;

import java.time.Instant;
import java.util.UUID;

public class CustomerOrder {

    @Id
    private UUID orderId;
    private Integer customerId;
    private Integer productId;
    private Integer amount;
    private Instant orderDate;
}
