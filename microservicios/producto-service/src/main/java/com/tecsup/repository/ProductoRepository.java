package com.tecsup.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tecsup.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
}
