package com.minimarket.minimarket_nilda_api.controller;

import com.minimarket.minimarket_nilda_api.entity.Producto;
import com.minimarket.minimarket_nilda_api.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping
    public List<Producto> listar(
            @RequestParam(required = false) String buscar,
            @RequestParam(required = false) Long categoria) {
        return productoService.buscar(buscar, categoria);
    }

    @GetMapping("/{id}")
    public Producto obtener(@PathVariable Long id) {
        return productoService.obtenerPorId(id);
    }

    @GetMapping("/stock-critico")
    public List<Producto> stockCritico() {
        return productoService.listarStockCritico();
    }

    @PostMapping
    public ResponseEntity<Producto> registrar(@Valid @RequestBody Producto producto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.registrar(producto));
    }

    @PutMapping("/{id}")
    public Producto actualizar(@PathVariable Long id, @Valid @RequestBody Producto producto) {
        return productoService.actualizar(id, producto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoService.eliminarLogico(id);
        return ResponseEntity.noContent().build();
    }
}