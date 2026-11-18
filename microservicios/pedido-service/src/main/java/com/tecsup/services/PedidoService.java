package com.tecsup.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tecsup.exception.ResourceNotFoundException;
import com.tecsup.model.DetallePedido;
import com.tecsup.model.Pedido;
import com.tecsup.repository.PedidoRepository;

@Service
@Transactional
public class PedidoService {

    private final PedidoRepository repository;

    public PedidoService(PedidoRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Pedido> listar() {
        List<Pedido> pedidos = repository.findAll();
        pedidos.forEach(p -> p.getDetalles().size()); // carga los detalles (LAZY)
        return pedidos;
    }

    @Transactional(readOnly = true)
    public Pedido buscarPorId(Long id) {
        Pedido pedido = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con id: " + id));
        pedido.getDetalles().size();
        return pedido;
    }

    public Pedido guardar(Pedido pedido) {
        pedido.setIdPedido(null);
        if (pedido.getDetalles() != null) {
            for (DetallePedido detalle : pedido.getDetalles()) {
                detalle.setPedido(pedido);
            }
        }
        return repository.save(pedido);
    }

    public Pedido actualizar(Long id, Pedido pedido) {
        Pedido pedidoExistente = buscarPorId(id);
        pedidoExistente.setFecha(pedido.getFecha());
        pedidoExistente.setIdCliente(pedido.getIdCliente());
        pedidoExistente.setTotal(pedido.getTotal());
        return repository.save(pedidoExistente);
    }

    public void eliminar(Long id) {
        repository.delete(buscarPorId(id));
    }
}
