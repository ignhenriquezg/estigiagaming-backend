package com.estigiagaming.backend.service;

import com.estigiagaming.backend.model.Categoria;
import com.estigiagaming.backend.model.Producto;
import com.estigiagaming.backend.repository.CategoriaRepository;
import com.estigiagaming.backend.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    public ProductoService(ProductoRepository productoRepository, CategoriaRepository categoriaRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    public List<Producto> listarOfertas() {
        return productoRepository.findByEnOfertaTrue();
    }

    public List<Producto> listarStockCritico() {
        List<Producto> criticos = new ArrayList<>();
        for (Producto p : productoRepository.findAll()) {
            if (p.getStock() != null && p.getStockCritico() != null && p.getStock() <= p.getStockCritico()) {
                criticos.add(p);
            }
        }
        return criticos;
    }

    public Producto buscarPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
    }

    public Producto guardar(Producto producto) {
        if (producto.getPrecio() == null || producto.getPrecio() < 0) {
            throw new RuntimeException("El precio no puede ser negativo.");
        }
        if (producto.getStock() == null || producto.getStock() < 0) {
            throw new RuntimeException("El stock no puede ser negativo.");
        }
        if (producto.getPrecioOferta() == null) {
            producto.setPrecioOferta(producto.getPrecio());
        }
        if (producto.getEnOferta() == null) {
            producto.setEnOferta(false);
        }
        if (producto.getStockCritico() == null) {
            producto.setStockCritico(5);
        }

        resolverCategoria(producto);
        return productoRepository.save(producto);
    }

    public Producto actualizar(Long id, Producto datos) {
        Producto existente = buscarPorId(id);
        existente.setCodigo(datos.getCodigo());
        existente.setNombre(datos.getNombre());
        existente.setPrecio(datos.getPrecio());
        existente.setPrecioOferta(datos.getPrecioOferta() != null ? datos.getPrecioOferta() : datos.getPrecio());
        existente.setEnOferta(datos.getEnOferta() != null ? datos.getEnOferta() : false);
        existente.setStock(datos.getStock());
        existente.setStockCritico(datos.getStockCritico() != null ? datos.getStockCritico() : 5);
        existente.setImagen(datos.getImagen());
        existente.setDescripcion(datos.getDescripcion());
        existente.setCategoria(datos.getCategoria());
        existente.setCategoriaNombre(datos.getCategoriaNombre());

        resolverCategoria(existente);
        return productoRepository.save(existente);
    }

    public void eliminar(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new RuntimeException("Producto no encontrado con ID: " + id);
        }
        productoRepository.deleteById(id);
    }

    private void resolverCategoria(Producto producto) {
        if (producto.getCategoria() != null && producto.getCategoria().getId() != null) {
            Categoria cat = categoriaRepository.findById(producto.getCategoria().getId())
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada."));
            producto.setCategoria(cat);
        } else if (producto.getCategoriaNombre() != null && !producto.getCategoriaNombre().isBlank()) {
            Categoria cat = categoriaRepository.findByNombreIgnoreCase(producto.getCategoriaNombre().trim())
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada: " + producto.getCategoriaNombre()));
            producto.setCategoria(cat);
        }
    }
}