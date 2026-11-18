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

    public Map<String, Object> obtenerResumenDashboard(String username, boolean esAdmin) {
        LocalDateTime inicioHoy = LocalDate.now().atStartOfDay();
        LocalDateTime finHoy = LocalDate.now().atTime(LocalTime.MAX);

        List<Venta> ventasHoyList = ventaRepository.findByFechaBetweenOrderByFechaDesc(inicioHoy, finHoy);
        if (!esAdmin) {
            ventasHoyList = ventasHoyList.stream()
                    .filter(v -> v.getUsuario().getUsername().equals(username))
                    .toList();
        }

        BigDecimal totalHoy = ventasHoyList.stream()
                .map(Venta::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return Map.of(
                "ventasHoy", totalHoy,
                "totalProductos", productoRepository.count(),
                "stockCritico", productoRepository.findStockCritico().size(),
                "productosCriticos", productoRepository.findStockCritico()
        );
    }

    public List<Venta> listarVentasPorRango(LocalDate desde, LocalDate hasta) {
        if (hasta.isBefore(desde)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fecha fin debe ser igual o posterior a la fecha inicio");
        }
        return ventaRepository.findByFechaBetweenOrderByFechaDesc(desde.atStartOfDay(), hasta.atTime(LocalTime.MAX));
    }

    public byte[] generarComprobantePdf(Long idVenta) {
        Venta venta = ventaRepository.findById(idVenta)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Venta no encontrada"));

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A5);
            PdfWriter.getInstance(doc, out);
            doc.open();
            doc.add(new Paragraph("MINIMARKET NILDA", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
            doc.add(new Paragraph("Comprobante: " + venta.getNroComprobante()));
            doc.add(new Paragraph("Fecha: " + venta.getFecha()));
            doc.add(new Paragraph("Cliente: " + venta.getCliente()));
            doc.add(new Paragraph(" "));

            PdfPTable tabla = new PdfPTable(4);
            tabla.addCell("Producto");
            tabla.addCell("Cant.");
            tabla.addCell("P. Unit");
            tabla.addCell("Subtotal");
            for (DetalleVenta d : venta.getDetalles()) {
                tabla.addCell(d.getProducto().getNombre());
                tabla.addCell(String.valueOf(d.getCantidad()));
                tabla.addCell("S/ " + d.getPrecioUnitario());
                tabla.addCell("S/ " + d.getSubtotal());
            }
            doc.add(tabla);
            doc.add(new Paragraph("\nTOTAL A PAGAR: S/ " + venta.getTotal(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
            doc.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error generando comprobante PDF");
        }
    }

    public byte[] generarReporteVentasPdf(LocalDate desde, LocalDate hasta) {
        List<Venta> ventas = listarVentasPorRango(desde, hasta);
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4);
            PdfWriter.getInstance(doc, out);
            doc.open();
            doc.add(new Paragraph("REPORTE DE VENTAS - MINIMARKET NILDA", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
            doc.add(new Paragraph("Periodo: " + desde + " al " + hasta + "\n\n"));

            PdfPTable tabla = new PdfPTable(4);
            tabla.addCell("Fecha");
            tabla.addCell("Comprobante");
            tabla.addCell("Cliente");
            tabla.addCell("Total");
            BigDecimal suma = BigDecimal.ZERO;
            for (Venta v : ventas) {
                tabla.addCell(v.getFecha().toLocalDate().toString());
                tabla.addCell(v.getNroComprobante());
                tabla.addCell(v.getCliente());
                tabla.addCell("S/ " + v.getTotal());
                suma = suma.add(v.getTotal());
            }
            doc.add(tabla);
            doc.add(new Paragraph("\nTOTAL PERIODO: S/ " + suma, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
            doc.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error generando reporte PDF");
        }
    }
}