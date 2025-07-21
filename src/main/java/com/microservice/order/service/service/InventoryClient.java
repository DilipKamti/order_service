package com.microservice.order.service.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.microservice.order.service.dto.ApiResponse;
import com.microservice.order.service.dto.InventoryRequest;
import com.microservice.order.service.dto.InventoryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryClient {
    private final WebClient.Builder webClientBuilder;

    @Value("${inventory.service.base-url:http://inventory-service:8084}")
    private String inventoryServiceBaseUrl;

    public List<InventoryResponse> checkProductInTheInventory(List<InventoryRequest> requests) {
    log.info("Sending request to Inventory Service for batch check...");

    ParameterizedTypeReference<ApiResponse<List<InventoryResponse>>> responseType =
        new ParameterizedTypeReference<>() {};

    ApiResponse<List<InventoryResponse>> apiResponse = webClientBuilder.build()
            .post()
            .uri(inventoryServiceBaseUrl + "/api/v1/inventory/batch/check")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requests)
            .retrieve()
            .bodyToMono(responseType)
            .block();

    return apiResponse != null ? apiResponse.data() : List.of(); // Or throw an exception
}

}
