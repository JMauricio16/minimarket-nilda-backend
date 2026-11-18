package com.minimarket.minimarket_nilda_api.controller;

import com.minimarket.minimarket_nilda_api.entity.Usuario;
import com.minimarket.minimarket_nilda_api.repository.UsuarioRepository;
import com.minimarket.minimarket_nilda_api.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> body) {
        Usuario user = usuarioRepository.findByUsername(body.get("username"))
                .filter(Usuario::getActivo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas"));

        if (!passwordEncoder.matches(body.get("password"), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
        }

        String token = jwtService.generarToken(user.getUsername(), user.getRol().name());
        return Map.of(
                "token", token,
                "nombre", user.getNombre(),
                "rol", user.getRol().name()
        );
    }
}