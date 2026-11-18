package com.tecsup.services;

import java.util.List;

import org.springframework.stereotype.Service;

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

    public Cliente guardar(Cliente cliente) {
        cliente.setId(null);
        return repo.save(cliente);
    }

    public Cliente actualizar(Long id, Cliente cliente) {
        obtener(id);
        cliente.setId(id);
        return repo.save(cliente);
    }

    public void eliminar(Long id) {
        repo.delete(obtener(id));
    }
}
