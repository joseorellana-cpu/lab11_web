package com.tecsup.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/** Datos de entrada para registrar o actualizar un cliente. */
public record ClienteDTO(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotBlank(message = "Los apellidos son obligatorios")
        String apellidos,

        String direccion,

        @Pattern(regexp = "\\d{9}", message = "El telefono debe tener 9 digitos")
        String telefono,

        @NotBlank(message = "El DNI es obligatorio")
        @Pattern(regexp = "\\d{8}", message = "El DNI debe tener 8 digitos")
        String nroDni,

        @NotBlank(message = "El estado es obligatorio")
        String estado) {
}
