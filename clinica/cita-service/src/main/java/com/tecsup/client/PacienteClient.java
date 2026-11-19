package com.tecsup.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.tecsup.dto.PacienteDTO;
import com.tecsup.exception.ResourceNotFoundException;
import com.tecsup.exception.ServicioNoDisponibleException;

/**
 * Comunicacion con paciente-service. No hay host ni puerto: el nombre se resuelve en Eureka.
 */
@Component
public class PacienteClient {

    private static final Logger log = LoggerFactory.getLogger(PacienteClient.class);

    private final RestClient restClient;

    public PacienteClient(@LoadBalanced RestClient.Builder builder) {
        this.restClient = builder.clone().baseUrl("http://paciente-service").build();
    }

    public PacienteDTO obtener(Long id) {
        log.info("Llamando a paciente-service -> GET /api/pacientes/{}", id);
        try {
            return restClient.get()
                    .uri("/api/pacientes/{id}", id)
                    .retrieve()
                    .body(PacienteDTO.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResourceNotFoundException("No existe el paciente con id " + id + " en paciente-service");
        } catch (RestClientException ex) {
            log.error("paciente-service no responde: {}", ex.getMessage());
            throw new ServicioNoDisponibleException("paciente-service no esta disponible");
        }
    }
}
