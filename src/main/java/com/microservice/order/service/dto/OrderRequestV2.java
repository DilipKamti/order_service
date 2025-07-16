package com.microservice.order.service.dto;

import java.math.BigDecimal;

public class OrderRequestV2 {
	String productName;
	int quantity;
	BigDecimal totalPrice;
	OrderStatus status;
	
	
	public OrderRequestV2(String productName, int quantity, BigDecimal totalPrice, OrderStatus status) {
		super();
		this.productName = productName;
		this.quantity = quantity;
		this.totalPrice = totalPrice;
		this.status = status;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public BigDecimal getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}
	public OrderStatus getStatus() {
		return status;
	}
	public void setStatus(OrderStatus status) {
		this.status = status;
	}
	
	
}
