package com.microservice.order.service.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.microservice.order.service.dto.OrderRequest;
import com.microservice.order.service.dto.OrderRequestV2;
import com.microservice.order.service.dto.OrderResponse;
import com.microservice.order.service.dto.OrderStatus;
import com.microservice.order.service.model.Order;
import com.microservice.order.service.repository.OrderRepository;
import com.microservice.order.service.utility.OrderMapper;
import com.microservice.order.service.utility.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClient;


    @Override
    public OrderResponse createOrder(OrderRequestV2 request) {
    	if (!isProductAvailable(request.getProductName(), request.getQuantity())) {
			throw new IllegalStateException("Product not available in sufficient quantity");
		}
        Order order = OrderMapper.toEntity(request);
        Order saved = orderRepository.save(order);
        log.info("Order created: {}", saved.getId());
        return OrderMapper.toResponse(saved);
    }

    @Override
    public OrderResponse getOrderById(Long id) {
        Order order = findOrderById(id);
        return OrderMapper.toResponse(order);
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(OrderMapper::toResponse)
                .toList();
    }

    @Override
    public Page<OrderResponse> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(OrderMapper::toResponse);
    }

    @Override
    public OrderResponse updateOrder(Long id, OrderRequest request) {
        Order order = findOrderById(id);

        // Optional: Prevent invalid transitions
        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Cannot update delivered or cancelled orders.");
        }

        OrderMapper.updateEntity(order, request);
        Order updated = orderRepository.save(order);
        log.info("Order updated: {}", id);
        return OrderMapper.toResponse(updated);
    }

    @Override
    public void deleteOrder(Long id) {
        Order order = findOrderById(id);

        if (order.getStatus() == OrderStatus.SHIPPED || order.getStatus() == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Cannot delete shipped or delivered orders.");
        }

        orderRepository.delete(order);
        log.info("Order deleted: {}", id);
    }

    private Order findOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + id));
    }
    
    public boolean isProductAvailable(String productCode, int quantity) {
    	var data=((WebClient) webClient).get()
			.uri("http://localhost:8082/api/v1/inventory/{productCode}", productCode)
			.retrieve()
			.bodyToMono(Boolean.class)
			.block(); 
		return data ;
	}


}
