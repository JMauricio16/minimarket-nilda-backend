package com.minimarket.minimarket_nilda_api.service;

import com.minimarket.minimarket_nilda_api.dto.VentaRequestDTO;
import com.minimarket.minimarket_nilda_api.entity.*;
import com.minimarket.minimarket_nilda_api.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VentaService {

    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final MovimientoInventarioRepository movimientoRepository;

    @Transactional
    public Venta registrarVenta(VentaRequestDTO request, Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        Venta venta = new Venta();
        venta.setCliente(request.getCliente() == null || request.getCliente().isBlank() ? "Público general" : request.getCliente());
        venta.setUsuario(usuario);
        venta.setFecha(LocalDateTime.now());
        long correlativo = ventaRepository.count() + 1;
        venta.setNroComprobante(String.format("T001-%06d", correlativo));

        BigDecimal total = BigDecimal.ZERO;

        for (VentaRequestDTO.ItemVentaDTO item : request.getItems()) {
            Producto prod = productoRepository.findById(item.getIdProducto())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));

            BigDecimal stockActual = prod.getStock() != null ? prod.getStock() : BigDecimal.ZERO;
            if (stockActual.compareTo(item.getCantidad()) < 0) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Stock insuficiente para " + prod.getNombre() + " (disponible: " + stockActual + ")");
            }

            prod.setStock(stockActual.subtract(item.getCantidad()));
            productoRepository.save(prod);

            BigDecimal subtotal = prod.getPrecioVenta()
                    .multiply(item.getCantidad())
                    .setScale(2, RoundingMode.HALF_UP);

            DetalleVenta det = new DetalleVenta();
            det.setVenta(venta);
            det.setProducto(prod);
            det.setCantidad(item.getCantidad());
            det.setPrecioUnitario(prod.getPrecioVenta());
            det.setSubtotal(subtotal);
            venta.getDetalles().add(det);

            total = total.add(subtotal);

            MovimientoInventario mov = new MovimientoInventario();
            mov.setProducto(prod);
            mov.setUsuario(usuario);
            mov.setTipo(MovimientoInventario.TipoMovimiento.SALIDA);
            mov.setCantidad(item.getCantidad());
            mov.setMotivo("Venta " + venta.getNroComprobante());
            movimientoRepository.save(mov);
        }

        venta.setTotal(total);
        return ventaRepository.save(venta);
    }
}