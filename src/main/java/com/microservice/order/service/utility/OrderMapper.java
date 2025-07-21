package com.microservice.order.service.utility;

import com.microservice.order.service.dto.OrderRequest;
import com.microservice.order.service.dto.OrderResponse;
import com.microservice.order.service.dto.OrderStatus;
import com.microservice.order.service.model.Order;

public class OrderMapper {

    public static OrderResponse toResponse(Order order) {
    return new OrderResponse(
            order.getId(),
            order.getCustomerOrderId(),
            order.getProductName(),
            order.getProductId(),
            order.getQuantity(),
            order.getTotalPrice(),
            order.getStatus()
    );
}


    public static Order toEntity(OrderRequest request) {
        return Order.builder()
                .productName(request.productName())
                .productId(request.productId())
                .quantity(request.quantity())
                .totalPrice(request.totalPrice())
                .status(request.status() != null ? request.status() : OrderStatus.CREATED)
                .build();
    }

    public static void updateEntity(Order order, OrderRequest request) {
        order.setProductName(request.productName());
        order.setProductId(request.productId());
        order.setQuantity(request.quantity());
        order.setTotalPrice(request.totalPrice());
        if (request.status() != null) {
            order.setStatus(request.status());
        }
    }
}
