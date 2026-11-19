package com.tecsup.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.tecsup.dto.CategoriaDTO;
import com.tecsup.exception.ResourceNotFoundException;
import com.tecsup.exception.ServicioNoDisponibleException;

/**
 * Comunicacion con categoria-service. No hay host ni puerto: el nombre se resuelve en Eureka.
 */
@Component
public class CategoriaClient {

    private static final Logger log = LoggerFactory.getLogger(CategoriaClient.class);

    private final RestClient restClient;

    public CategoriaClient(@LoadBalanced RestClient.Builder builder) {
        this.restClient = builder.clone().baseUrl("http://categoria-service").build();
    }

    public CategoriaDTO obtener(Long id) {
        log.info("Llamando a categoria-service -> GET /api/categorias/{}", id);
        try {
            return restClient.get()
                    .uri("/api/categorias/{id}", id)
                    .retrieve()
                    .body(CategoriaDTO.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResourceNotFoundException("No existe la categoria con id " + id + " en categoria-service");
        } catch (RestClientException ex) {
            log.error("categoria-service no responde: {}", ex.getMessage());
            throw new ServicioNoDisponibleException("categoria-service no esta disponible");
        }
    }
}
