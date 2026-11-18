package com.tecsup.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tecsup.model.Medico;

public interface MedicoRepository extends JpaRepository<Medico, Long> {
}
