package com.tecsup.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tecsup.dto.CambioEstadoDTO;
import com.tecsup.dto.CitaDetalleDTO;
import com.tecsup.model.Atencion;
import com.tecsup.model.Cita;
import com.tecsup.services.CitaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/citas")
public class CitaController {

    private final CitaService service;

    public CitaController(CitaService service) {
        this.service = service;
    }

    /** Lista todas las citas, o filtra por ?idPaciente= o ?idMedico= */
    @GetMapping
    public List<Cita> listar(@RequestParam(required = false) Long idPaciente,
                             @RequestParam(required = false) Long idMedico) {
        if (idPaciente != null) {
            return service.listarPorPaciente(idPaciente);
        }
        if (idMedico != null) {
            return service.listarPorMedico(idMedico);
        }
        return service.listar();
    }

    @GetMapping("/{id}")
    public Cita obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @GetMapping("/{id}/detalle")
    public CitaDetalleDTO detalle(@PathVariable Long id) {
        return service.detalle(id);
    }

    @PostMapping
    public ResponseEntity<Cita> registrar(@Valid @RequestBody Cita cita) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(cita));
    }

    @PatchMapping("/{id}/estado")
    public Cita actualizarEstado(@PathVariable Long id, @Valid @RequestBody CambioEstadoDTO dto) {
        return service.actualizarEstado(id, dto.estado());
    }

    @PatchMapping("/{id}/cancelar")
    public Cita cancelar(@PathVariable Long id) {
        return service.cancelar(id);
    }

    @PostMapping("/{id}/atencion")
    public ResponseEntity<Atencion> registrarAtencion(@PathVariable Long id,
                                                      @Valid @RequestBody Atencion atencion) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrarAtencion(id, atencion));
    }

    @GetMapping("/{id}/atencion")
    public Atencion obtenerAtencion(@PathVariable Long id) {
        return service.obtenerAtencion(id);
    }
}
