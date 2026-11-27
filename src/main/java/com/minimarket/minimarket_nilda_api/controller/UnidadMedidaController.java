package com.minimarket.minimarket_nilda_api.controller;

import com.minimarket.minimarket_nilda_api.entity.UnidadMedida;
import com.minimarket.minimarket_nilda_api.repository.UnidadMedidaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/unidades-medida")
@RequiredArgsConstructor
public class UnidadMedidaController {

    private final UnidadMedidaRepository unidadMedidaRepository;

    @GetMapping
    public List<UnidadMedida> listar() {
        return unidadMedidaRepository.findByActivoTrue();
    }
}