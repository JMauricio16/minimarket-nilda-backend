package com.minimarket.minimarket_nilda_api.controller;

import com.minimarket.minimarket_nilda_api.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final ReporteService reporteService;

    @GetMapping("/resumen")
    public Map<String, Object> resumen(Authentication auth) {
        boolean esAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return reporteService.obtenerResumenDashboard(auth.getName(), esAdmin);
    }
}