package com.tecsup.dto;

/** Datos del medico que devuelve medico-service. */
public record MedicoDTO(Long idMedico, String cmp, String nombres, String apellidos,
                        Long idEspecialidad, String estado) {
}
