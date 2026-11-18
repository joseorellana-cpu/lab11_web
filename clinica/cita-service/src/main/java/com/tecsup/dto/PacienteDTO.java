package com.tecsup.dto;

/** Datos del paciente que devuelve paciente-service. */
public record PacienteDTO(Long idPaciente, String dni, String nombres, String apellidos, String estado) {
}
