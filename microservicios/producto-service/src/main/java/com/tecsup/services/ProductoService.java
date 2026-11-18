package com.tecsup.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tecsup.exception.ResourceNotFoundException;
import com.tecsup.model.Producto;
import com.tecsup.repository.ProductoRepository;

@Service
public class ProductoService {

    private final ProductoRepository repo;

    public ProductoService(ProductoRepository repo) {
        this.repo = repo;
    }

    public List<Producto> listar() {
        return repo.findAll();
    }

    public Producto obtener(Long id) {
        return repo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Producto no encontrado con id: " + id));
    }

    public Producto guardar(Producto producto) {
        producto.setId(null);
        return repo.save(producto);
    }

    public Producto actualizar(Long id, Producto producto) {
        obtener(id);
        producto.setId(id);
        return repo.save(producto);
    }

    public void eliminar(Long id) {
        repo.delete(obtener(id));
    }
}
