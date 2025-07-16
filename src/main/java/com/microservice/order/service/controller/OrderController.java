package com.microservice.order.service.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.microservice.order.service.dto.ApiResponse;
import com.microservice.order.service.dto.OrderRequest;
import com.microservice.order.service.dto.OrderRequestV2;
import com.microservice.order.service.dto.OrderResponse;
import com.microservice.order.service.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Order Controller", description = "APIs to manage orders")
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "Create a new order")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder( @RequestBody OrderRequestV2 request) {
    	System.out.println("Received: " + request);
        OrderResponse response = orderService.createOrder(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Order created successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an order by ID")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrder(@PathVariable Long id) {
        OrderResponse response = orderService.getOrderById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Order fetched successfully"));
    }

    @GetMapping("/all")
    @Operation(summary = "Get all orders (no pagination)")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getAllOrders() {
        List<OrderResponse> orders = orderService.getAllOrders();
        return ResponseEntity.ok(ApiResponse.success(orders, "All orders fetched successfully"));
    }

    @GetMapping
    @Operation(summary = "Get paginated orders")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getPaginatedOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,desc") String[] sort
    ) {
        Sort sortObj = Sort.by(
            Arrays.stream(sort)
                .filter(s -> s != null && !s.isBlank())
                .map(s -> {
                    String[] parts = s.split(",");
                    String field = parts[0].trim();
                    String direction = (parts.length > 1) ? parts[1].trim() : "asc";

                    Sort.Direction dir;
                    try {
                        dir = Sort.Direction.fromString(direction.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        dir = Sort.Direction.ASC; // fallback
                    }

                    return new Sort.Order(dir, field);
                })
                .toList()
        );

        Pageable pageable = PageRequest.of(page, size, sortObj);
        Page<OrderResponse> paginated = orderService.getAllOrders(pageable);
        return ResponseEntity.ok(ApiResponse.success(paginated, "Paginated orders fetched"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an order by ID")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrder(@PathVariable Long id, @Valid @RequestBody OrderRequest request) {
        OrderResponse response = orderService.updateOrder(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Order updated successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an order by ID")
    public ResponseEntity<ApiResponse<Object>> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Order deleted successfully"));
    }
    
    
}
