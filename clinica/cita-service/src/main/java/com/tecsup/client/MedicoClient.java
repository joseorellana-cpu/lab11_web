package com.tecsup.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.tecsup.dto.MedicoDTO;
import com.tecsup.exception.ResourceNotFoundException;
import com.tecsup.exception.ServicioNoDisponibleException;

/**
 * Comunicacion con medico-service. No hay host ni puerto: el nombre se resuelve en Eureka.
 */
@Component
public class MedicoClient {

    private static final Logger log = LoggerFactory.getLogger(MedicoClient.class);

    private final RestClient restClient;

    public MedicoClient(@LoadBalanced RestClient.Builder builder) {
        this.restClient = builder.clone().baseUrl("http://medico-service").build();
    }

    public MedicoDTO obtener(Long id) {
        log.info("Llamando a medico-service -> GET /api/medicos/{}", id);
        try {
            return restClient.get()
                    .uri("/api/medicos/{id}", id)
                    .retrieve()
                    .body(MedicoDTO.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResourceNotFoundException("No existe el medico con id " + id + " en medico-service");
        } catch (RestClientException ex) {
            log.error("medico-service no responde: {}", ex.getMessage());
            throw new ServicioNoDisponibleException("medico-service no esta disponible");
        }
    }
}
