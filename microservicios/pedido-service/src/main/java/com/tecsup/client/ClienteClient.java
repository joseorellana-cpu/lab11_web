package com.tecsup.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.tecsup.dto.ClienteDTO;
import com.tecsup.exception.ResourceNotFoundException;
import com.tecsup.exception.ServicioNoDisponibleException;

/**
 * Comunicacion con cliente-service. No hay host ni puerto: el nombre se resuelve en Eureka.
 */
@Component
public class ClienteClient {

    private static final Logger log = LoggerFactory.getLogger(ClienteClient.class);

    private final RestClient restClient;

    public ClienteClient(@LoadBalanced RestClient.Builder builder) {
        this.restClient = builder.clone().baseUrl("http://cliente-service").build();
    }

    public ClienteDTO obtener(Long id) {
        log.info("Llamando a cliente-service -> GET /api/clientes/{}", id);
        try {
            return restClient.get()
                    .uri("/api/clientes/{id}", id)
                    .retrieve()
                    .body(ClienteDTO.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResourceNotFoundException("No existe el cliente con id " + id + " en cliente-service");
        } catch (RestClientException ex) {
            log.error("cliente-service no responde: {}", ex.getMessage());
            throw new ServicioNoDisponibleException("cliente-service no esta disponible");
        }
    }
}
