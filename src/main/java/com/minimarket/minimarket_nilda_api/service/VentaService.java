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

            if (prod.getStock() < item.getCantidad()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Stock insuficiente para " + prod.getNombre() + " (disponible: " + prod.getStock() + ")");
            }

            prod.setStock(prod.getStock() - item.getCantidad());
            productoRepository.save(prod);

            DetalleVenta det = new DetalleVenta();
            det.setVenta(venta);
            det.setProducto(prod);
            det.setCantidad(item.getCantidad());
            det.setPrecioUnitario(prod.getPrecioVenta());
            det.setSubtotal(prod.getPrecioVenta().multiply(BigDecimal.valueOf(item.getCantidad())));
            venta.getDetalles().add(det);

            total = total.add(det.getSubtotal());

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