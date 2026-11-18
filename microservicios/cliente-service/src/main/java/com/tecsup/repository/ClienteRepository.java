package com.tecsup.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tecsup.model.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}
