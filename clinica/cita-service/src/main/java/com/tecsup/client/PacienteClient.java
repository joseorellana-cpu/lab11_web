package com.tecsup.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import com.tecsup.dto.PacienteDTO;
import com.tecsup.exception.ResourceNotFoundException;
import com.tecsup.exception.ServicioNoDisponibleException;

/** Cliente HTTP hacia paciente-service. */
@Component
public class PacienteClient {

    private final RestClient restClient;

    public PacienteClient(@Value("${servicios.paciente.url}") String url) {
        this.restClient = RestClient.create(url);
    }

    public PacienteDTO obtener(Long idPaciente) {
        try {
            return restClient.get()
                    .uri("/api/pacientes/{id}", idPaciente)
                    .retrieve()
                    .body(PacienteDTO.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResourceNotFoundException("No existe el paciente con id: " + idPaciente);
        } catch (ResourceAccessException ex) {
            throw new ServicioNoDisponibleException("paciente-service no esta disponible");
        }
    }
}
