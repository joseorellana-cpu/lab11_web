package com.tecsup.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tecsup.dto.ClienteDTO;
import com.tecsup.exception.ResourceNotFoundException;
import com.tecsup.model.Cliente;
import com.tecsup.repository.ClienteRepository;

@Service
public class ClienteService {

    private final ClienteRepository repo;

    public ClienteService(ClienteRepository repo) {
        this.repo = repo;
    }

    public List<Cliente> listar() {
        return repo.findAll();
    }

    public Cliente obtener(Long id) {
        return repo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Cliente no encontrado con id: " + id));
    }

    public Cliente guardar(ClienteDTO dto) {
        Cliente cliente = new Cliente();
        copiar(dto, cliente);
        return repo.save(cliente);
    }

    public Cliente actualizar(Long id, ClienteDTO dto) {
        Cliente cliente = obtener(id);
        copiar(dto, cliente);
        return repo.save(cliente);
    }

    public void eliminar(Long id) {
        repo.delete(obtener(id));
    }

    private void copiar(ClienteDTO dto, Cliente cliente) {
        cliente.setNombre(dto.nombre());
        cliente.setApellidos(dto.apellidos());
        cliente.setDireccion(dto.direccion());
        cliente.setTelefono(dto.telefono());
        cliente.setNroDni(dto.nroDni());
        cliente.setEstado(dto.estado());
    }
}
