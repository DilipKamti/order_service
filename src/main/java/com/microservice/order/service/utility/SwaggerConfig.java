package com.microservice.order.service.utility;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@Configuration
@OpenAPIDefinition(
	info = @Info(title = "Order Service API", version = "1.0", description = "Handles order operations")
)
public class SwaggerConfig {}

