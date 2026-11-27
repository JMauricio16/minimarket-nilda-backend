package com.minimarket.minimarket_nilda_api.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.minimarket.minimarket_nilda_api.entity.*;
import com.minimarket.minimarket_nilda_api.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReporteService {

    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;
    private final MovimientoInventarioRepository movimientoInventarioRepository;

   public Map<String, Object> obtenerResumenDashboard(String username, boolean esAdmin) {
    LocalDate hoy = LocalDate.now();
    LocalDateTime inicioHoy = hoy.atStartOfDay();
    LocalDateTime finHoy = hoy.atTime(LocalTime.MAX);

    List<Venta> ventasHoyList = ventaRepository.findByFechaBetweenOrderByFechaDesc(inicioHoy, finHoy);
    if (!esAdmin) {
        ventasHoyList = ventasHoyList.stream()
                .filter(v -> v.getUsuario().getUsername().equals(username))
                .toList();
    }

    BigDecimal totalHoy = ventasHoyList.stream()
            .map(Venta::getTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    LocalDate inicioSemana = hoy.minusDays(6);
    List<Venta> ventasSemanaList = ventaRepository.findByFechaBetweenOrderByFechaDesc(
            inicioSemana.atStartOfDay(), finHoy);
    if (!esAdmin) {
        ventasSemanaList = ventasSemanaList.stream()
                .filter(v -> v.getUsuario().getUsername().equals(username))
                .toList();
    }

    java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("dd/MM");
    List<Map<String, Object>> ventasSemana = new ArrayList<>();
    for (int i = 0; i < 7; i++) {
        LocalDate dia = inicioSemana.plusDays(i);
        BigDecimal totalDia = ventasSemanaList.stream()
                .filter(v -> v.getFecha().toLocalDate().equals(dia))
                .map(Venta::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        ventasSemana.add(Map.of(
                "dia", dia.format(fmt),
                "fecha", dia.toString(),
                "total", totalDia
        ));
    }

    List<Producto> criticos = productoRepository.findStockCritico();

    return Map.of(
            "ventasHoy", totalHoy,
            "totalProductos", productoRepository.count(),
            "stockCritico", criticos.size(),
            "productosCriticos", criticos,
            "ventasSemana", ventasSemana
    );
}

    public List<Venta> listarVentasPorRango(LocalDate desde, LocalDate hasta) {
        if (hasta.isBefore(desde)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fecha fin debe ser igual o posterior a la fecha inicio");
        }
        return ventaRepository.findByFechaBetweenOrderByFechaDesc(desde.atStartOfDay(), hasta.atTime(LocalTime.MAX));
    }

   
}