package com.tecsup.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tecsup.model.HorarioMedico;

public interface HorarioMedicoRepository extends JpaRepository<HorarioMedico, Long> {

    List<HorarioMedico> findByIdMedico(Long idMedico);
}
