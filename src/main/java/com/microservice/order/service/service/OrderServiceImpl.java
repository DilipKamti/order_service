package com.microservice.order.service.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.microservice.order.service.dto.InventoryRequest;
import com.microservice.order.service.dto.InventoryResponse;
import com.microservice.order.service.dto.OrderRequest;
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
    private final InventoryClient inventoryClient;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final AtomicInteger COUNTER = new AtomicInteger(1);

    @Override
    public List<OrderResponse> createOrder(List<OrderRequest> requests) {
        if (!isProductAvailable(requests)) {
            throw new IllegalStateException("One or more products are not available in sufficient quantity.");
        }

        List<Order>listOfOrders = requests.stream().map(request->{
            Order order = OrderMapper.toEntity(request);
            order.setCustomerOrderId(generateCustomOrderId());
            return order;
        }).toList();


        List<Order> saved = orderRepository.saveAll(listOfOrders);
        return saved.stream().map(OrderMapper::toResponse).toList();
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

    public boolean isProductAvailable(List<OrderRequest> request) {
        List<InventoryRequest> inventoryRequests = request.stream()
                .map(req -> new InventoryRequest(req.productId(), req.quantity()))
                .toList();

        List<InventoryResponse> inventoryResponses = inventoryClient.checkProductInTheInventory(inventoryRequests);

        log.info("Inventory responses received: {}", inventoryResponses);

        Set<String> requestedCodes = inventoryRequests.stream()
                .map(InventoryRequest::productCode)
                .collect(Collectors.toSet());

        Set<String> responseCodes = inventoryResponses.stream()
                .map(InventoryResponse::productCode)
                .collect(Collectors.toSet());

        // Check if any product was not returned in the response
        requestedCodes.removeAll(responseCodes);
        if (!requestedCodes.isEmpty()) {
            throw new IllegalStateException("No inventory data found for products: " + requestedCodes);
        }

        // Check if any product is out of stock
        inventoryResponses.stream()
                .filter(response -> !response.inStock())
                .findFirst()
                .ifPresent(response -> {
                    throw new IllegalStateException("Product " + response.productCode() + 
                        " is not available in sufficient quantity. Available: " + response.availableQuantity());
                });

        return true;
    }


    private String generateCustomOrderId() {
        String date = LocalDate.now().format(FORMATTER);
        int count = COUNTER.getAndIncrement(); // For simplicity. For persistence, read from DB
        return "ORD-" + date + "-" + String.format("%05d", count);
    }

}
