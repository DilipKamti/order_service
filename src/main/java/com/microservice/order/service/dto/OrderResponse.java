package com.microservice.order.service.dto;

import java.math.BigDecimal;

public record OrderResponse(
		Long id,
		String productName,
		int quantity,
		BigDecimal totalPrice,
		OrderStatus status
	) {}

