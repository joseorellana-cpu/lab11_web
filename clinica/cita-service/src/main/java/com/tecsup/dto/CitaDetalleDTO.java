package com.tecsup.dto;

import com.tecsup.model.Cita;

/** Cita con los datos del paciente y del medico obtenidos de sus microservicios. */
public record CitaDetalleDTO(Cita cita, PacienteDTO paciente, MedicoDTO medico) {
}
