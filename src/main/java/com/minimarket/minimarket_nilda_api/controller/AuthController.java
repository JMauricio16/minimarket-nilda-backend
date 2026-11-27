package com.minimarket.minimarket_nilda_api.controller;

import com.minimarket.minimarket_nilda_api.entity.Usuario;
import com.minimarket.minimarket_nilda_api.repository.UsuarioRepository;
import com.minimarket.minimarket_nilda_api.security.JwtService;
import com.minimarket.minimarket_nilda_api.security.LoginAttemptService;
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
    private final LoginAttemptService loginAttemptService;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        if (username == null || username.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Falta el usuario");
        }
        if (password == null || password.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Falta la contraseña");
        }

        String key = username.trim().toLowerCase();

        if (loginAttemptService.isBlocked(key)) {
            long segundos = loginAttemptService.getRemainingSeconds(key);
            throw new ResponseStatusException(
                    HttpStatus.TOO_MANY_REQUESTS,
                    "Demasiados intentos fallidos. Intenta de nuevo en " + segundos + " segundos"
            );
        }

        Usuario user = usuarioRepository.findByUsername(username.trim())
                .filter(Usuario::getActivo)
                .orElseThrow(() -> {
                    loginAttemptService.recordFailedAttempt(key);
                    return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
                });

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            loginAttemptService.recordFailedAttempt(key);
            if (loginAttemptService.isBlocked(key)) {
                throw new ResponseStatusException(
                        HttpStatus.TOO_MANY_REQUESTS,
                        "Has superado los 3 intentos. Cuenta bloqueada por 1 minuto"
                );
            }
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Contraseña incorrecta");
        }

        loginAttemptService.resetAttempts(key);

        String token = jwtService.generarToken(user.getUsername(), user.getRol().name());
        return Map.of(
                "token", token,
                "nombre", user.getNombre(),
                "rol", user.getRol().name()
        );
    }
}