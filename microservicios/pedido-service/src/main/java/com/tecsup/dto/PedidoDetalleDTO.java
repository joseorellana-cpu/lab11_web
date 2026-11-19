package com.tecsup.dto;

import java.util.List;

import com.tecsup.model.Pedido;

/** Pedido con los datos del cliente y de los productos obtenidos de sus microservicios. */
public record PedidoDetalleDTO(Pedido pedido, ClienteDTO cliente, List<ProductoDTO> productos) {
}
