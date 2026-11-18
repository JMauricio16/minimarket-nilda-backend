package com.minimarket.minimarket_nilda_api.service;

import com.minimarket.minimarket_nilda_api.entity.Producto;
import com.minimarket.minimarket_nilda_api.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;

    public List<Producto> buscar(String buscar, Long idCategoria) {
        return productoRepository.buscarConFiltros(buscar, idCategoria);
    }

    public Producto obtenerPorId(Long id) {
        return productoRepository.findById(id)
                .filter(Producto::getActivo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
    }

    public List<Producto> listarStockCritico() {
        return productoRepository.findStockCritico();
    }

    public Producto registrar(Producto producto) {
        if (productoRepository.existsByCodigo(producto.getCodigo())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El código ya existe: " + producto.getCodigo());
        }
        if (producto.getPrecioVenta().compareTo(producto.getPrecioCompra()) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El precio de venta debe ser mayor al de compra");
        }
        producto.setActivo(true);
        return productoRepository.save(producto);
    }

    public Producto actualizar(Long id, Producto datos) {
        Producto existente = obtenerPorId(id);
        if (!existente.getCodigo().equals(datos.getCodigo()) && productoRepository.existsByCodigo(datos.getCodigo())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El código ya existe: " + datos.getCodigo());
        }
        if (datos.getPrecioVenta().compareTo(datos.getPrecioCompra()) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El precio de venta debe ser mayor al de compra");
        }
        existente.setCodigo(datos.getCodigo());
        existente.setNombre(datos.getNombre());
        existente.setCategoria(datos.getCategoria());
        existente.setPrecioCompra(datos.getPrecioCompra());
        existente.setPrecioVenta(datos.getPrecioVenta());
        existente.setStockMinimo(datos.getStockMinimo());
        return productoRepository.save(existente);
    }

    public void eliminarLogico(Long id) {
        Producto p = obtenerPorId(id);
        p.setActivo(false);
        productoRepository.save(p);
    }
}