package com.tecsup.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tecsup.model.Atencion;

public interface AtencionRepository extends JpaRepository<Atencion, Long> {

    Optional<Atencion> findByIdCita(Long idCita);
}
