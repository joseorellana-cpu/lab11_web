package com.tecsup.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tecsup.client.ClienteClient;
import com.tecsup.client.ProductoClient;
import com.tecsup.dto.ClienteDTO;
import com.tecsup.dto.DetallePedidoDTO;
import com.tecsup.dto.PedidoDTO;
import com.tecsup.dto.PedidoDetalleDTO;
import com.tecsup.dto.ProductoDTO;
import com.tecsup.exception.BusinessException;
import com.tecsup.exception.ResourceNotFoundException;
import com.tecsup.model.DetallePedido;
import com.tecsup.model.Pedido;
import com.tecsup.repository.PedidoRepository;

@Service
@Transactional
public class PedidoService {

    private final PedidoRepository repository;
    private final ClienteClient clienteClient;
    private final ProductoClient productoClient;

    public PedidoService(PedidoRepository repository, ClienteClient clienteClient, ProductoClient productoClient) {
        this.repository = repository;
        this.clienteClient = clienteClient;
        this.productoClient = productoClient;
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

    /** Pedido + datos del cliente y de cada producto (consulta a los otros microservicios). */
    @Transactional(readOnly = true)
    public PedidoDetalleDTO detalle(Long id) {
        Pedido pedido = buscarPorId(id);
        ClienteDTO cliente = clienteClient.obtener(pedido.getIdCliente());
        List<ProductoDTO> productos = pedido.getDetalles().stream()
                .map(d -> productoClient.obtener(d.getIdProducto()))
                .toList();
        return new PedidoDetalleDTO(pedido, cliente, productos);
    }

    /**
     * Antes de guardar se comunica con cliente-service y producto-service:
     * el cliente y cada producto deben existir y haber stock suficiente.
     */
    public Pedido guardar(PedidoDTO dto) {
        Pedido pedido = new Pedido();
        pedido.setDetalles(new ArrayList<>());
        llenar(pedido, dto);
        return repository.save(pedido);
    }

    public Pedido actualizar(Long id, PedidoDTO dto) {
        Pedido pedido = buscarPorId(id);
        pedido.getDetalles().clear();
        llenar(pedido, dto);
        return repository.save(pedido);
    }

    public void eliminar(Long id) {
        repository.delete(buscarPorId(id));
    }

    private void llenar(Pedido pedido, PedidoDTO dto) {
        ClienteDTO cliente = clienteClient.obtener(dto.idCliente());
        if (!"ACTIVO".equalsIgnoreCase(cliente.estado())) {
            throw new BusinessException("El cliente " + cliente.id() + " no esta ACTIVO");
        }
        pedido.setFecha(dto.fecha());
        pedido.setIdCliente(dto.idCliente());

        double total = 0;
        for (DetallePedidoDTO item : dto.detalles()) {
            ProductoDTO producto = productoClient.obtener(item.idProducto());
            if (producto.stock() != null && producto.stock() < item.cantidad()) {
                throw new BusinessException("Stock insuficiente para '" + producto.nombre()
                        + "': disponible " + producto.stock() + ", solicitado " + item.cantidad());
            }
            DetallePedido detalle = new DetallePedido();
            detalle.setIdProducto(producto.id());
            detalle.setCantidad(item.cantidad());
            detalle.setPrecio(producto.precio());
            detalle.setPedido(pedido);
            pedido.getDetalles().add(detalle);
            total += producto.precio() * item.cantidad();
        }
        pedido.setTotal(Math.round(total * 100) / 100.0);
    }
}
