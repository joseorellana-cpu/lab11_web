package com.tecsup.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestClient;

/**
 * @LoadBalanced: las URL usan el NOMBRE del servicio registrado en Eureka
 * (http://categoria-service) en lugar de host y puerto fijos.
 * Spring Cloud LoadBalancer consulta a Eureka y elige una instancia disponible.
 */
@Configuration
public class RestClientConfig {

    @Bean
    @LoadBalanced
    public RestClient.Builder loadBalancedRestClientBuilder() {
        return RestClient.builder();
    }

    // Builder normal para el propio cliente de Eureka (habla con http://localhost:8761).
    // Si solo existiera el balanceado, Eureka buscaria "localhost" como nombre de servicio.
    @Bean
    @Primary
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }
}
