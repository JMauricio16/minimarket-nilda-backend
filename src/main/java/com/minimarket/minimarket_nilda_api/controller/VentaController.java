package com.minimarket.minimarket_nilda_api.controller;

import com.minimarket.minimarket_nilda_api.dto.VentaRequestDTO;
import com.minimarket.minimarket_nilda_api.entity.Usuario;
import com.minimarket.minimarket_nilda_api.entity.Venta;
import com.minimarket.minimarket_nilda_api.repository.UsuarioRepository;
import com.minimarket.minimarket_nilda_api.service.ReporteService;
import com.minimarket.minimarket_nilda_api.service.VentaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
public class VentaController {

    private final VentaService ventaService;
    private final UsuarioRepository usuarioRepository;
    private final ReporteService reporteService;

    @PostMapping
    public ResponseEntity<Venta> registrar(@Valid @RequestBody VentaRequestDTO request, Authentication auth) {
        Usuario usuario = usuarioRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no válido"));
        Venta creada = ventaService.registrarVenta(request, usuario.getIdUsuario());
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @GetMapping("/{id}/comprobante")
    public ResponseEntity<byte[]> descargarComprobante(@PathVariable Long id) {
        byte[] pdf = reporteService.generarComprobantePdf(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=comprobante-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}