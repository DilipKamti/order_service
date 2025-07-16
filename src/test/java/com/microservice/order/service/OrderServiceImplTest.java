package com.microservice.order.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.microservice.order.service.dto.OrderRequest;
import com.microservice.order.service.dto.OrderRequestV2;
import com.microservice.order.service.dto.OrderResponse;
import com.microservice.order.service.dto.OrderStatus;
import com.microservice.order.service.model.Order;
import com.microservice.order.service.repository.OrderRepository;
import com.microservice.order.service.service.OrderServiceImpl;
import com.microservice.order.service.utility.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private OrderRepository orderRepository;

    private Order order;
    private OrderRequest request;
    private OrderRequestV2 requestv2;

    @BeforeEach
    void setUp() {
        request = new OrderRequest("Test Product", 2, new BigDecimal("50.00"), OrderStatus.CREATED);
        order = Order.builder()
                .id(1L)
                .productName("Test Product")
                .quantity(2)
                .totalPrice(new BigDecimal("50.00"))
                .status(OrderStatus.CREATED)
                .build();
    }

    @Test
    void shouldCreateOrderSuccessfully() {
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderResponse response = orderService.createOrder(requestv2);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.productName()).isEqualTo("Test Product");
    }

    @Test
    void shouldGetOrderById() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        OrderResponse response = orderService.getOrderById(1L);

        assertThat(response.productName()).isEqualTo("Test Product");
    }

    @Test
    void shouldThrowWhenOrderNotFound() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrderById(10L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Order not found");
    }

    @Test
    void shouldUpdateOrderSuccessfully() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderRequest updatedRequest = new OrderRequest("Updated", 3, new BigDecimal("75.00"), OrderStatus.SHIPPED);
        OrderResponse response = orderService.updateOrder(1L, updatedRequest);

        assertThat(response.quantity()).isEqualTo(3);
        assertThat(response.status()).isEqualTo(OrderStatus.SHIPPED);
    }

    @Test
    void shouldDeleteOrderSuccessfully() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        doNothing().when(orderRepository).delete(order);

        assertThatCode(() -> orderService.deleteOrder(1L)).doesNotThrowAnyException();
    }
}

