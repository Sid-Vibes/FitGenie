package com.fitnessApplication.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class MLWebClientConfig {

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
    @Bean
    public WebClient mlWebClient(
            WebClient.Builder builder,
            @Value("${ml.service.base-url}")
            String baseUrl,
            @Value("${ml.service.api-key}")
            String apiKey)
    {

        return builder
                .baseUrl(baseUrl)
                .defaultHeader("X-Internal-API-Key", apiKey)
                .build();
    }
}