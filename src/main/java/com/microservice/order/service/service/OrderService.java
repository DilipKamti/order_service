package com.microservice.order.service.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.microservice.order.service.dto.OrderRequest;
import com.microservice.order.service.dto.OrderResponse;

public interface OrderService {

    List<OrderResponse> createOrder(List<OrderRequest> request);

    OrderResponse getOrderById(Long id);

    List<OrderResponse> getAllOrders();

    Page<OrderResponse> getAllOrders(Pageable pageable); // NEW

    OrderResponse updateOrder(Long id, OrderRequest request); // NEW

    void deleteOrder(Long id); // NEW
}

