package com.tecsup.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.tecsup.dto.ProductoDTO;
import com.tecsup.exception.ResourceNotFoundException;
import com.tecsup.exception.ServicioNoDisponibleException;

/**
 * Comunicacion con producto-service. No hay host ni puerto: el nombre se resuelve en Eureka.
 */
@Component
public class ProductoClient {

    private static final Logger log = LoggerFactory.getLogger(ProductoClient.class);

    private final RestClient restClient;

    public ProductoClient(@LoadBalanced RestClient.Builder builder) {
        this.restClient = builder.clone().baseUrl("http://producto-service").build();
    }

    public ProductoDTO obtener(Long id) {
        log.info("Llamando a producto-service -> GET /api/productos/{}", id);
        try {
            return restClient.get()
                    .uri("/api/productos/{id}", id)
                    .retrieve()
                    .body(ProductoDTO.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResourceNotFoundException("No existe el producto con id " + id + " en producto-service");
        } catch (RestClientException ex) {
            log.error("producto-service no responde: {}", ex.getMessage());
            throw new ServicioNoDisponibleException("producto-service no esta disponible");
        }
    }
}
