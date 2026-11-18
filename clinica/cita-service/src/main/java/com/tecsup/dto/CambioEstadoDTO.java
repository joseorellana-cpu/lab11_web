package com.tecsup.dto;

import com.tecsup.model.EstadoCita;

import jakarta.validation.constraints.NotNull;

public record CambioEstadoDTO(@NotNull(message = "El estado es obligatorio") EstadoCita estado) {
}
