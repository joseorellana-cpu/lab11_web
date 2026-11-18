package com.tecsup.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tecsup.model.Paciente;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {
}
