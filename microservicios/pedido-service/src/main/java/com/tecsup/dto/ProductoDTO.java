package com.tecsup.dto;

/** Producto tal como lo devuelve producto-service. */
public record ProductoDTO(Long id, String nombre, Double precio, Integer stock) {
}
