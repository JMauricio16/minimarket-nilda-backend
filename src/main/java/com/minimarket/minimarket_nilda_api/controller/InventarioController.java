package com.minimarket.minimarket_nilda_api.controller;

import com.minimarket.minimarket_nilda_api.dto.EntradaInventarioDTO;
import com.minimarket.minimarket_nilda_api.entity.MovimientoInventario;
import com.minimarket.minimarket_nilda_api.service.InventarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventario")
@RequiredArgsConstructor
public class InventarioController {

    private final InventarioService inventarioService;

    @PostMapping("/entradas")
    public ResponseEntity<MovimientoInventario> registrarEntrada(
            @Valid @RequestBody EntradaInventarioDTO dto,
            Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inventarioService.registrarEntrada(dto, auth.getName()));
    }
}
