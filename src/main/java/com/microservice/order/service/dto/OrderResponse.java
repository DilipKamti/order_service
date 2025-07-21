package com.microservice.order.service.dto;

import java.math.BigDecimal;

public record OrderResponse(
        Long id,
        String customerOrderId,
        String productName,
        String productId,
        int quantity,
        BigDecimal totalPrice,
        OrderStatus status
) {}

