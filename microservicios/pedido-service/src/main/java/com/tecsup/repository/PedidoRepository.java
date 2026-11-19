package com.tecsup.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tecsup.model.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}
