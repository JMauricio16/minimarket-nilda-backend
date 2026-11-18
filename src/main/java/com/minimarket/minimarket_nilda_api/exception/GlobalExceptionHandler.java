
package com.minimarket.minimarket_nilda_api.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> manejarNegocio(
            ResponseStatusException ex) {

        return ResponseEntity.status(ex.getStatusCode()).body(Map.of(
                "estado", ex.getStatusCode().value(),
                "mensaje", ex.getReason() != null
                        ? ex.getReason()
                        : "Error de negocio",
                "fecha", LocalDateTime.now().toString()
        ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> manejarValidacion(
            MethodArgumentNotValidException ex) {

        String error = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(fieldError -> fieldError.getDefaultMessage())
                .orElse("Error de validación en los datos enviados");

        return ResponseEntity.badRequest().body(Map.of(
                "estado", 400,
                "mensaje", error != null ? error : "Datos inválidos",
                "fecha", LocalDateTime.now().toString()
        ));
    }
}
