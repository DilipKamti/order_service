package com.microservice.order.service.utility;

import com.microservice.order.service.dto.OrderRequest;
import com.microservice.order.service.dto.OrderRequestV2;
import com.microservice.order.service.dto.OrderResponse;
import com.microservice.order.service.dto.OrderStatus;
import com.microservice.order.service.model.Order;

public class OrderMapper {

    public static OrderResponse toResponse(Order order) {
        return new OrderResponse(
            order.getId(),
            order.getProductName(),
            order.getQuantity(),
            order.getTotalPrice(),
            order.getStatus()
        );
    }

    public static Order toEntity(OrderRequestV2 request) {
        return Order.builder()
            .productName(request.getProductName())
            .quantity(request.getQuantity())
            .totalPrice(request.getTotalPrice())
            .status(request.getStatus() != null ? request.getStatus() : OrderStatus.CREATED)
            .build();
    }

    public static void updateEntity(Order order, OrderRequest request) {
        order.setProductName(request.productName());
        order.setQuantity(request.quantity());
        order.setTotalPrice(request.totalPrice());
        if (request.status() != null) {
            order.setStatus(request.status());
        }
    }
}

