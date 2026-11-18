package com.tecsup.services;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tecsup.client.MedicoClient;
import com.tecsup.client.PacienteClient;
import com.tecsup.dto.CitaDetalleDTO;
import com.tecsup.dto.MedicoDTO;
import com.tecsup.dto.PacienteDTO;
import com.tecsup.exception.BusinessException;
import com.tecsup.exception.ResourceNotFoundException;
import com.tecsup.model.Atencion;
import com.tecsup.model.Cita;
import com.tecsup.model.EstadoCita;
import com.tecsup.repository.AtencionRepository;
import com.tecsup.repository.CitaRepository;

@Service
@Transactional
public class CitaService {

    /** Cambios de estado permitidos. ATENDIDA y CANCELADA son estados finales. */
    private static final Map<EstadoCita, Set<EstadoCita>> TRANSICIONES = Map.of(
            EstadoCita.PROGRAMADA, EnumSet.of(EstadoCita.CONFIRMADA, EstadoCita.CANCELADA),
            EstadoCita.CONFIRMADA, EnumSet.of(EstadoCita.ATENDIDA, EstadoCita.CANCELADA),
            EstadoCita.ATENDIDA, EnumSet.noneOf(EstadoCita.class),
            EstadoCita.CANCELADA, EnumSet.noneOf(EstadoCita.class));

    private static final Set<EstadoCita> ACTIVAS = EnumSet.of(EstadoCita.PROGRAMADA, EstadoCita.CONFIRMADA);

    private final CitaRepository repo;
    private final AtencionRepository atencionRepo;
    private final PacienteClient pacienteClient;
    private final MedicoClient medicoClient;

    public CitaService(CitaRepository repo, AtencionRepository atencionRepo,
                       PacienteClient pacienteClient, MedicoClient medicoClient) {
        this.repo = repo;
        this.atencionRepo = atencionRepo;
        this.pacienteClient = pacienteClient;
        this.medicoClient = medicoClient;
    }

    @Transactional(readOnly = true)
    public List<Cita> listar() {
        return repo.findAll();
    }

    @Transactional(readOnly = true)
    public List<Cita> listarPorPaciente(Long idPaciente) {
        return repo.findByIdPaciente(idPaciente);
    }

    @Transactional(readOnly = true)
    public List<Cita> listarPorMedico(Long idMedico) {
        return repo.findByIdMedico(idMedico);
    }

    @Transactional(readOnly = true)
    public Cita obtener(Long id) {
        return repo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Cita no encontrada con id: " + id));
    }

    /** Cita + datos del paciente y del medico (consulta a los otros microservicios). */
    @Transactional(readOnly = true)
    public CitaDetalleDTO detalle(Long id) {
        Cita cita = obtener(id);
        return new CitaDetalleDTO(cita,
                pacienteClient.obtener(cita.getIdPaciente()),
                medicoClient.obtener(cita.getIdMedico()));
    }

    public Cita registrar(Cita cita) {
        PacienteDTO paciente = pacienteClient.obtener(cita.getIdPaciente());
        if (!"A".equals(paciente.estado())) {
            throw new BusinessException("El paciente no esta activo");
        }
        MedicoDTO medico = medicoClient.obtener(cita.getIdMedico());
        if (!"A".equals(medico.estado())) {
            throw new BusinessException("El medico no esta activo");
        }
        if (repo.existsByIdMedicoAndFechaAndHoraAndEstadoIn(
                cita.getIdMedico(), cita.getFecha(), cita.getHora(), ACTIVAS)) {
            throw new BusinessException("El medico ya tiene una cita en esa fecha y hora");
        }
        cita.setIdCita(null);
        cita.setEstado(EstadoCita.PROGRAMADA);
        return repo.save(cita);
    }

    public Cita actualizarEstado(Long id, EstadoCita nuevo) {
        Cita cita = obtener(id);
        if (!TRANSICIONES.get(cita.getEstado()).contains(nuevo)) {
            throw new BusinessException("No se puede pasar de " + cita.getEstado() + " a " + nuevo);
        }
        cita.setEstado(nuevo);
        return repo.save(cita);
    }

    public Cita cancelar(Long id) {
        return actualizarEstado(id, EstadoCita.CANCELADA);
    }

    /** Registra el resultado de la cita y la marca como ATENDIDA. */
    public Atencion registrarAtencion(Long idCita, Atencion atencion) {
        Cita cita = obtener(idCita);
        if (atencionRepo.findByIdCita(idCita).isPresent()) {
            throw new BusinessException("La cita ya tiene una atencion registrada");
        }
        actualizarEstado(cita.getIdCita(), EstadoCita.ATENDIDA);
        atencion.setIdAtencion(null);
        atencion.setIdCita(idCita);
        return atencionRepo.save(atencion);
    }

    @Transactional(readOnly = true)
    public Atencion obtenerAtencion(Long idCita) {
        obtener(idCita);
        return atencionRepo.findByIdCita(idCita).orElseThrow(
                () -> new ResourceNotFoundException("La cita " + idCita + " no tiene atencion registrada"));
    }
}
