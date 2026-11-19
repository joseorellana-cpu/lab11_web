package com.tecsup.dto;

/** Cliente tal como lo devuelve cliente-service. */
public record ClienteDTO(Long id, String nombre, String apellidos, String nroDni, String estado) {
}
