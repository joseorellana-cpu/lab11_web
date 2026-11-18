package com.tecsup.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tecsup.model.Cita;
import com.tecsup.model.EstadoCita;

public interface CitaRepository extends JpaRepository<Cita, Long> {

    List<Cita> findByIdPaciente(Long idPaciente);

    List<Cita> findByIdMedico(Long idMedico);

    /** Para no programar dos citas al mismo medico en la misma fecha y hora. */
    boolean existsByIdMedicoAndFechaAndHoraAndEstadoIn(Long idMedico, LocalDate fecha, LocalTime hora,
                                                      Collection<EstadoCita> estados);
}
