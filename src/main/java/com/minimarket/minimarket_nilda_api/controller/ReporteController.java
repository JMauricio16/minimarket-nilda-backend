package com.minimarket.minimarket_nilda_api.controller;

import com.minimarket.minimarket_nilda_api.entity.Venta;
import com.minimarket.minimarket_nilda_api.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping("/ventas")
    public List<Venta> listarVentas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return reporteService.listarVentasPorRango(desde, hasta);
    }

    @GetMapping("/ventas/pdf")
    public ResponseEntity<byte[]> descargarReportePdf(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        byte[] pdf = reporteService.generarReporteVentasPdf(desde, hasta);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte-ventas.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}