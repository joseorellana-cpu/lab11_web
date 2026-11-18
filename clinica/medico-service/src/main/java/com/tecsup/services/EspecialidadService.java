package com.tecsup.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tecsup.exception.ResourceNotFoundException;
import com.tecsup.model.Especialidad;
import com.tecsup.repository.EspecialidadRepository;

@Service
public class EspecialidadService {

    private final EspecialidadRepository repo;

    public EspecialidadService(EspecialidadRepository repo) {
        this.repo = repo;
    }

    public List<Especialidad> listar() {
        return repo.findAll();
    }

    public Especialidad obtener(Long id) {
        return repo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Especialidad no encontrada con id: " + id));
    }

    public Especialidad guardar(Especialidad especialidad) {
        especialidad.setIdEspecialidad(null);
        return repo.save(especialidad);
    }

    public Especialidad actualizar(Long id, Especialidad especialidad) {
        obtener(id);
        especialidad.setIdEspecialidad(id);
        return repo.save(especialidad);
    }

    public void eliminar(Long id) {
        repo.delete(obtener(id));
    }
}
