package com.tecsup.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tecsup.exception.BusinessException;
import com.tecsup.exception.ResourceNotFoundException;
import com.tecsup.model.HorarioMedico;
import com.tecsup.model.Medico;
import com.tecsup.repository.HorarioMedicoRepository;
import com.tecsup.repository.MedicoRepository;

@Service
public class MedicoService {

    private final MedicoRepository repo;
    private final HorarioMedicoRepository horarioRepo;
    private final EspecialidadService especialidadService;

    public MedicoService(MedicoRepository repo, HorarioMedicoRepository horarioRepo,
                         EspecialidadService especialidadService) {
        this.repo = repo;
        this.horarioRepo = horarioRepo;
        this.especialidadService = especialidadService;
    }

    public List<Medico> listar() {
        return repo.findAll();
    }

    public Medico obtener(Long id) {
        return repo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Medico no encontrado con id: " + id));
    }

    public Medico guardar(Medico medico) {
        especialidadService.obtener(medico.getIdEspecialidad()); // valida que exista
        medico.setIdMedico(null);
        return repo.save(medico);
    }

    public Medico actualizar(Long id, Medico medico) {
        obtener(id);
        especialidadService.obtener(medico.getIdEspecialidad());
        medico.setIdMedico(id);
        return repo.save(medico);
    }

    public void eliminar(Long id) {
        Medico medico = obtener(id);
        horarioRepo.deleteAll(horarioRepo.findByIdMedico(id));
        repo.delete(medico);
    }

    // ----- horarios del medico
    public List<HorarioMedico> horarios(Long idMedico) {
        obtener(idMedico);
        return horarioRepo.findByIdMedico(idMedico);
    }

    public HorarioMedico agregarHorario(Long idMedico, HorarioMedico horario) {
        obtener(idMedico);
        if (!horario.getHoraFin().isAfter(horario.getHoraInicio())) {
            throw new BusinessException("La hora de fin debe ser posterior a la hora de inicio");
        }
        horario.setIdHorario(null);
        horario.setIdMedico(idMedico);
        return horarioRepo.save(horario);
    }
}
