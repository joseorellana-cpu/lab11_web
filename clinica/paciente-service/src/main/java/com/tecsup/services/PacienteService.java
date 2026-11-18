package com.tecsup.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tecsup.exception.ResourceNotFoundException;
import com.tecsup.model.Paciente;
import com.tecsup.repository.PacienteRepository;

@Service
public class PacienteService {

    private final PacienteRepository repo;

    public PacienteService(PacienteRepository repo) {
        this.repo = repo;
    }

    public List<Paciente> listar() {
        return repo.findAll();
    }

    public Paciente obtener(Long id) {
        return repo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Paciente no encontrado con id: " + id));
    }

    public Paciente guardar(Paciente paciente) {
        paciente.setIdPaciente(null);
        return repo.save(paciente);
    }

    public Paciente actualizar(Long id, Paciente paciente) {
        obtener(id);
        paciente.setIdPaciente(id);
        return repo.save(paciente);
    }

    public void eliminar(Long id) {
        repo.delete(obtener(id));
    }
}
