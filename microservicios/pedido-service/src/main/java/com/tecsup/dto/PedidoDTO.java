package com.tecsup.dto;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * Datos de entrada del pedido. El precio y el total NO se reciben:
 * se calculan con el precio actual que informa producto-service.
 */
public record PedidoDTO(
        @NotNull(message = "La fecha es obligatoria")
        LocalDate fecha,

        @NotNull(message = "El cliente es obligatorio")
        Long idCliente,

        @NotEmpty(message = "El pedido debe tener al menos un detalle")
        @Valid
        List<DetallePedidoDTO> detalles) {
}
