package com.tecsup.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tecsup.model.HorarioMedico;
import com.tecsup.model.Medico;
import com.tecsup.services.MedicoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/medicos")
public class MedicoController {

    private final MedicoService service;

    public MedicoController(MedicoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Medico> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Medico obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @PostMapping
    public ResponseEntity<Medico> guardar(@Valid @RequestBody Medico medico) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(medico));
    }

    @PutMapping("/{id}")
    public Medico actualizar(@PathVariable Long id, @Valid @RequestBody Medico medico) {
        return service.actualizar(id, medico);
    }

    @DeleteMapping("/{id}")
    public Map<String, String> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return Map.of("mensaje", "Medico eliminado correctamente");
    }

    @GetMapping("/{id}/horarios")
    public List<HorarioMedico> horarios(@PathVariable Long id) {
        return service.horarios(id);
    }

    @PostMapping("/{id}/horarios")
    public ResponseEntity<HorarioMedico> agregarHorario(@PathVariable Long id,
                                                        @Valid @RequestBody HorarioMedico horario) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.agregarHorario(id, horario));
    }
}
