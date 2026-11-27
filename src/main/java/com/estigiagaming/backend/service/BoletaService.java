package com.estigiagaming.backend.service;

import com.estigiagaming.backend.model.Boleta;
import com.estigiagaming.backend.model.DetalleBoleta;
import com.estigiagaming.backend.model.Producto;
import com.estigiagaming.backend.model.Usuario;
import com.estigiagaming.backend.repository.BoletaRepository;
import com.estigiagaming.backend.repository.ProductoRepository;
import com.estigiagaming.backend.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
public class BoletaService {

    private final BoletaRepository boletaRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    public BoletaService(BoletaRepository boletaRepository,
                         ProductoRepository productoRepository,
                         UsuarioRepository usuarioRepository) {
        this.boletaRepository = boletaRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<Boleta> listarTodas() {
        return boletaRepository.findAll();
    }

    public Boleta buscarPorId(Long id) {
        return boletaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Boleta no encontrada con ID: " + id));
    }

    @Transactional
    public Boleta crearBoleta(Boleta boleta) {
        if (boleta.getItems() == null || boleta.getItems().isEmpty()) {
            throw new RuntimeException("La boleta debe contener al menos un producto.");
        }

        if (boleta.getNumero() == null || boleta.getNumero().isBlank()) {
            boleta.setNumero("#" + System.currentTimeMillis());
        }
        if (boleta.getFecha() == null || boleta.getFecha().isBlank()) {
            boleta.setFecha(LocalDate.now().toString());
        }
        if (boleta.getEstado() == null || boleta.getEstado().isBlank()) {
            boleta.setEstado("Pagado");
        }

        if (boleta.getCorreoCliente() != null) {
            usuarioRepository.findByCorreoIgnoreCase(boleta.getCorreoCliente())
                    .ifPresent(boleta::setUsuario);
        }

        int totalCalculado = 0;
        boolean pagoExitoso = "Pagado".equalsIgnoreCase(boleta.getEstado());

        for (DetalleBoleta item : boleta.getItems()) {
            if (item.getProducto() == null || item.getProducto().getId() == null) {
                throw new RuntimeException("Cada detalle debe indicar el ID del producto.");
            }
            Producto prod = productoRepository.findById(item.getProducto().getId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado ID: " + item.getProducto().getId()));

            int cantidad = item.getCantidad() != null ? item.getCantidad() : 1;
            if (pagoExitoso && prod.getStock() < cantidad) {
                throw new RuntimeException("Stock insuficiente para: " + prod.getNombre());
            }

            if (pagoExitoso) {
                prod.setStock(prod.getStock() - cantidad);
                productoRepository.save(prod);
            }

            int precioUnit = Boolean.TRUE.equals(prod.getEnOferta()) ? prod.getPrecioOferta() : prod.getPrecio();
            item.setProducto(prod);
            item.setPrecioUnitario(precioUnit);
            item.setSubtotal(precioUnit * cantidad);
            item.setBoleta(boleta);

            totalCalculado += item.getSubtotal();
        }

        boleta.setTotal(totalCalculado);
        return boletaRepository.save(boleta);
    }
}