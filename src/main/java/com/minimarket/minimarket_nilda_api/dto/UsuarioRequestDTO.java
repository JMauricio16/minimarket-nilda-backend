package com.minimarket.minimarket_nilda_api.dto;

import com.minimarket.minimarket_nilda_api.entity.Usuario.Rol;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UsuarioRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 80, message = "El nombre no debe exceder 80 caracteres")
    private String nombre;

    @NotBlank(message = "El usuario es obligatorio")
    @Size(max = 40, message = "El usuario no debe exceder 40 caracteres")
    private String username;

    @Size(min = 4, max = 100, message = "La contraseña debe tener al menos 4 caracteres")
    private String password;

    @NotNull(message = "El rol es obligatorio")
    private Rol rol;

    private Boolean activo = true;
}