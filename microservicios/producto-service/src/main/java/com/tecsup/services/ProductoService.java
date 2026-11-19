package com.tecsup.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.tecsup.client.CategoriaClient;
import com.tecsup.dto.CategoriaDTO;
import com.tecsup.dto.ProductoDTO;
import com.tecsup.exception.ResourceNotFoundException;
import com.tecsup.exception.ServicioNoDisponibleException;
import com.tecsup.model.Producto;
import com.tecsup.repository.ProductoRepository;

@Service
public class ProductoService {

    private static final Logger log = LoggerFactory.getLogger(ProductoService.class);

    private final ProductoRepository repo;
    private final CategoriaClient categoriaClient;

    public ProductoService(ProductoRepository repo, CategoriaClient categoriaClient) {
        this.repo = repo;
        this.categoriaClient = categoriaClient;
    }

    public List<Producto> listar() {
        List<Producto> productos = repo.findAll();
        productos.forEach(this::completarCategoria);
        return productos;
    }

    public Producto obtener(Long id) {
        Producto producto = buscar(id);
        completarCategoria(producto);
        return producto;
    }

    /** Al registrar, la categoria DEBE existir en categoria-service (404 si no existe, 503 si esta caido). */
    public Producto guardar(ProductoDTO dto) {
        CategoriaDTO categoria = categoriaClient.obtener(dto.categoriaId());
        Producto producto = new Producto();
        copiar(dto, producto);
        Producto guardado = repo.save(producto);
        guardado.setCategoria(categoria);
        return guardado;
    }

    public Producto actualizar(Long id, ProductoDTO dto) {
        Producto producto = buscar(id);
        CategoriaDTO categoria = categoriaClient.obtener(dto.categoriaId());
        copiar(dto, producto);
        Producto guardado = repo.save(producto);
        guardado.setCategoria(categoria);
        return guardado;
    }

    public void eliminar(Long id) {
        repo.delete(buscar(id));
    }

    private Producto buscar(Long id) {
        return repo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Producto no encontrado con id: " + id));
    }

    private void copiar(ProductoDTO dto, Producto producto) {
        producto.setNombre(dto.nombre());
        producto.setPrecio(dto.precio());
        producto.setStock(dto.stock());
        producto.setCategoriaId(dto.categoriaId());
    }

    /** En consultas: si la categoria fue eliminada o el servicio esta caido, el producto se muestra igual. */
    private void completarCategoria(Producto producto) {
        try {
            producto.setCategoria(categoriaClient.obtener(producto.getCategoriaId()));
        } catch (ResourceNotFoundException | ServicioNoDisponibleException ex) {
            log.warn("Producto {} sin datos de categoria: {}", producto.getId(), ex.getMessage());
        }
    }
}
