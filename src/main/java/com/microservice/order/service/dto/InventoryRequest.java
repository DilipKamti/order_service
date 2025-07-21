package com.microservice.order.service.dto;

public record InventoryRequest(
	    String productCode,
	    int quantity
	) {}