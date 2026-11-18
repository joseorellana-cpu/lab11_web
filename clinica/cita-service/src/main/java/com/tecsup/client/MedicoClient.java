package com.tecsup.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import com.tecsup.dto.MedicoDTO;
import com.tecsup.exception.ResourceNotFoundException;
import com.tecsup.exception.ServicioNoDisponibleException;

/** Cliente HTTP hacia medico-service. */
@Component
public class MedicoClient {

    private final RestClient restClient;

    public MedicoClient(@Value("${servicios.medico.url}") String url) {
        this.restClient = RestClient.create(url);
    }

    public MedicoDTO obtener(Long idMedico) {
        try {
            return restClient.get()
                    .uri("/api/medicos/{id}", idMedico)
                    .retrieve()
                    .body(MedicoDTO.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResourceNotFoundException("No existe el medico con id: " + idMedico);
        } catch (ResourceAccessException ex) {
            throw new ServicioNoDisponibleException("medico-service no esta disponible");
        }
    }
}
