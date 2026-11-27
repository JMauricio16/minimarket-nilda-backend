package com.minimarket.minimarket_nilda_api.service;

import com.minimarket.minimarket_nilda_api.dto.EntradaInventarioDTO;
import com.minimarket.minimarket_nilda_api.entity.*;
import com.minimarket.minimarket_nilda_api.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class InventarioService {

    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final MovimientoInventarioRepository movimientoRepository;

    @Transactional
    public MovimientoInventario registrarEntrada(EntradaInventarioDTO dto, String username) {
        Producto prod = productoRepository.findById(dto.getIdProducto())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        BigDecimal stockActual = prod.getStock() != null ? prod.getStock() : BigDecimal.ZERO;
        prod.setStock(stockActual.add(dto.getCantidad()));
        productoRepository.save(prod);

        MovimientoInventario mov = new MovimientoInventario();
        mov.setProducto(prod);
        mov.setUsuario(usuario);
        mov.setTipo(MovimientoInventario.TipoMovimiento.ENTRADA);
        mov.setCantidad(dto.getCantidad());
        mov.setMotivo(dto.getMotivo());
        return movimientoRepository.save(mov);
    }
}