package com.microservice.order.service.utility;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {


	/**
	 * This bean is used for load-balanced WebClient instances.
	 * It allows the use of service discovery with Spring Cloud LoadBalancer.
	 */
	@Bean
	@LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}
