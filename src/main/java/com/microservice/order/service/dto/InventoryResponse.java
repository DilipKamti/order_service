package com.microservice.order.service.dto;

public record InventoryResponse(String productCode, boolean inStock, int availableQuantity) {}