package com.minimarket.minimarket_nilda_api.service;

import com.minimarket.minimarket_nilda_api.entity.Producto;
import com.minimarket.minimarket_nilda_api.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

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

    @Transactional
    public Producto registrar(Producto producto) {
        if (producto.getPrecioVenta().compareTo(producto.getPrecioCompra()) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El precio de venta debe ser mayor al de compra");
        }

        if (producto.getCodigo() == null || producto.getCodigo().isBlank()) {
            int maxNumero = productoRepository.findAll().stream()
                    .map(Producto::getCodigo)
                    .filter(c -> c != null && c.toUpperCase().startsWith("PD-"))
                    .mapToInt(c -> {
                        try {
                            return Integer.parseInt(c.substring(3).trim());
                        } catch (NumberFormatException e) {
                            return 0;
                        }
                    })
                    .max()
                    .orElse(0);

            int siguienteNumero = maxNumero + 1;
            String nuevoCodigo = String.format("PD-%02d", siguienteNumero);
            while (productoRepository.existsByCodigo(nuevoCodigo)) {
                siguienteNumero++;
                nuevoCodigo = String.format("PD-%02d", siguienteNumero);
            }
            producto.setCodigo(nuevoCodigo);
        } else {
            producto.setCodigo(producto.getCodigo().trim().toUpperCase());
            if (productoRepository.existsByCodigo(producto.getCodigo())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "El código ya existe: " + producto.getCodigo());
            }
        }

        producto.setActivo(true);
        return productoRepository.save(producto);
    }

@Transactional
public Producto actualizar(Long id, Producto datos) {
    Producto existente = obtenerPorId(id);

    String nuevoCodigo = (datos.getCodigo() == null || datos.getCodigo().isBlank())
            ? existente.getCodigo()
            : datos.getCodigo().trim();

    if (!existente.getCodigo().equals(nuevoCodigo) && productoRepository.existsByCodigo(nuevoCodigo)) {
        throw new ResponseStatusException(HttpStatus.CONFLICT, "El código ya existe: " + nuevoCodigo);
    }
    if (datos.getPrecioVenta().compareTo(datos.getPrecioCompra()) <= 0) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El precio de venta debe ser mayor al de compra");
    }

    existente.setCodigo(nuevoCodigo);
    existente.setNombre(datos.getNombre());
    existente.setCategoria(datos.getCategoria());
    existente.setUnidadMedida(datos.getUnidadMedida());
    existente.setPrecioCompra(datos.getPrecioCompra());
    existente.setPrecioVenta(datos.getPrecioVenta());
    existente.setStockMinimo(datos.getStockMinimo());
    // No se modifica existente.setStock(...) aquí: solo cambia con movimientos de inventario
    return productoRepository.save(existente);
}
    public void eliminarLogico(Long id) {
        Producto p = obtenerPorId(id);
        p.setActivo(false);
        productoRepository.save(p);
    }
}